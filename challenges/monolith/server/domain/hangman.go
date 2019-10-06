package domain

import (
	"errors"
	"fmt"
	"math/rand"
	pb "monolith/proto"
	"strings"
	"sync"
	"sync/atomic"
)

var idGen uint64 = 0
var possibleStrings = [...]string{"monolith", "ethereum", "blockchain", "btc"}
const maxGuesses = 5

type Hangman struct {
	Id uint64
	word string
	attempts map[string]interface{} // so that we can search O(1)
	attemptsInline []string // so that we can represent as a nice array to the client - alternative would be to flat map keys into array on each representation
	mutex sync.Mutex // protect against multiple clients mutating state of a game
	gameOver bool
}

func NewGame() *Hangman {
	atomic.AddUint64(&idGen, 1)

	h := &Hangman{
		Id: idGen,
		word: possibleStrings[rand.Int() % len(possibleStrings)],
		attempts: make(map[string]interface{}, 0),
		attemptsInline: make([]string, 0),
		gameOver: false,
	}
	return h
}

func (h *Hangman) ToClientResponse() *pb.Game {
	var wrongGuesses = len(h.attempts)
	var word string
	for _, char := range h.word {
		if _, found := h.attempts[string(char)]; found {
			wrongGuesses -= 1 // TODO err.. it's being nice and allowing extra guesses if you got dupe chars right :) // bug
			word += fmt.Sprintf("%c ", char)
		} else {
			word += "_ "
		}
	}

	// to prevent overflow exception,
	var remainingGuesses uint64
	if wrongGuesses >= maxGuesses {
		remainingGuesses = 0
	} else {
		remainingGuesses = uint64(maxGuesses - wrongGuesses)
	}

	var won = !strings.ContainsAny(word, "_")
	var gameOver = won || remainingGuesses == 0
	h.gameOver = gameOver

	return &pb.Game{
		GameId: h.Id,
		RemainingGuesses: remainingGuesses,
		Guesses: h.attemptsInline,
		Word: word,
		Won: won,
		GameOver: gameOver,
	}
}

func (h *Hangman) Guess(char string) error {
	if h.gameOver {
		return errors.New("game is over")
	}

	if len(char) != 1 {
		return errors.New(fmt.Sprintf("`%v` not a single char", char))
	}

	char = strings.ToLower(char)
	if _, found := h.attempts[char]; found {
		return errors.New(fmt.Sprintf("`%v` already attempted", char))
	}

	// serialise changes to state
	h.mutex.Lock()
	defer h.mutex.Unlock()
	h.attempts[char] = true
	h.attemptsInline = append(h.attemptsInline, char)
	return nil
}