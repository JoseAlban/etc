package domain

import (
	pb "monolith/proto"
	"reflect"
	"sync"
	"testing"
)

// Not that interesting to test
func TestNewGame(t *testing.T) {
	tests := []struct {
		name string
		want *Hangman
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := NewGame(); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("NewGame() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestHangman_ToClientResponse(t *testing.T) {
	type fields struct {
		Id             uint64
		word           string
		attempts       map[string]interface{}
		attemptsInline []string
		mutex          sync.Mutex
		gameOver       bool
		Notifications  chan string
	}
	tests := []struct {
		name   string
		fields fields
		want   *pb.Game
	}{
		{
			name: "still some guesses",
			fields: fields{
				Id:             1,
				word:           "testword",
				attempts:       map[string]interface{}{"a": true},
				attemptsInline: []string{"a"},
				gameOver:       false,
				Notifications:  make(chan string, 1),
			},
			want: &pb.Game{
				GameId:           1,
				Word:             "_ _ _ _ _ _ _ _ ",
				Guesses:          []string{"a"},
				RemainingGuesses: 4,
				Won:              false,
				GameOver:         false,
			},
		},
		{
			name: "game over - loser",
			fields: fields{
				Id:             1,
				word:           "testword",
				attempts:       map[string]interface{}{"a": true, "2": true, "3": true, "4": true, "5": true, "6": true},
				attemptsInline: []string{"a", "2", "3", "4", "5", "6"},
				gameOver:       false,
				Notifications:  make(chan string, 1),
			},
			want: &pb.Game{
				GameId:           1,
				Word:             "_ _ _ _ _ _ _ _ ",
				Guesses:          []string{"a", "2", "3", "4", "5", "6"},
				RemainingGuesses: 0,
				Won:              false,
				GameOver:         true,
			},
		},
		{
			name: "game over - winner",
			fields: fields{
				Id:             1,
				word:           "testword",
				attempts:       map[string]interface{}{"t": true, "e": true, "s": true, "w": true, "o": true, "r": true, "d": true},
				attemptsInline: []string{"a"}, // ignored
				gameOver:       false,
				Notifications:  make(chan string, 1),
			},
			want: &pb.Game{
				GameId:           1,
				Word:             "t e s t w o r d ",
				Guesses:          []string{"a"},  // ignored
				RemainingGuesses: 6, // TODO fix bug of increasing remaining guesses
				Won:              true,
				GameOver:         true,
			},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			h := &Hangman{
				Id:             tt.fields.Id,
				word:           tt.fields.word,
				attempts:       tt.fields.attempts,
				attemptsInline: tt.fields.attemptsInline,
				mutex:          tt.fields.mutex,
				gameOver:       tt.fields.gameOver,
				Notifications:  tt.fields.Notifications,
			}
			if got := h.ToClientResponse(); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("Hangman.ToClientResponse() = %v, want %v", got, tt.want)
			}
		})
	}
}

// Interesting to test the invalid arg cases here, also when multiple clients try to mutate same game
func TestHangman_Guess(t *testing.T) {
	type fields struct {
		Id             uint64
		word           string
		attempts       map[string]interface{}
		attemptsInline []string
		mutex          sync.Mutex
		gameOver       bool
		Notifications  chan string
	}
	type args struct {
		char string
	}
	tests := []struct {
		name    string
		fields  fields
		args    args
		wantErr bool
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			h := &Hangman{
				Id:             tt.fields.Id,
				word:           tt.fields.word,
				attempts:       tt.fields.attempts,
				attemptsInline: tt.fields.attemptsInline,
				mutex:          tt.fields.mutex,
				gameOver:       tt.fields.gameOver,
				Notifications:  tt.fields.Notifications,
			}
			if err := h.Guess(tt.args.char); (err != nil) != tt.wantErr {
				t.Errorf("Hangman.Guess() error = %v, wantErr %v", err, tt.wantErr)
			}
		})
	}
}
