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
	mutex sync.Mutex // protect against multiple clients mutating state of a game
}

func NewGame() *Hangman {
	atomic.AddUint64(&idGen, 1)

	h := &Hangman{
		Id: idGen,
		word: possibleStrings[rand.Int() % len(possibleStrings)],
		attempts: make(map[string]interface{}, 0),
	}
	return h
}

func (h *Hangman) ToClientResponse() *pb.Game {
	var remainingGuesses uint64
	// to prevent overflow exception,
	if len(h.attempts) >= maxGuesses {
		remainingGuesses = 0
	} else {
		remainingGuesses = uint64(maxGuesses - len(h.attempts))
	}

	return &pb.Game{
		GameId: h.Id,
		RemainingGuesses: remainingGuesses,
		//Won: h.word == h.attempts, TODO
		Won: false,
	}
}

func (h *Hangman) Guess(char string) error {
	if len(char) != 1 {
		return errors.New(fmt.Sprintf("`%v` not a single char", char))
	}

	char = strings.ToLower(char)
	if _, found := h.attempts[char]; !found {
		return errors.New(fmt.Sprintf("`%v` already attempted", char))
	}

	// serialise changes to state
	h.mutex.Lock()
	defer h.mutex.Unlock()
	h.attempts[char] = true
	return nil
}