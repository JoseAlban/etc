package main

import (
	"bufio"
	"context"
	"google.golang.org/grpc"
	"gopkg.in/alecthomas/kingpin.v2"
	"io"
	"log"
	"os"
	"time"

	pb "monolith/proto"
)

var (
	app      = kingpin.New("client", "A client for our Hangman server.")
	newGame = app.Command("new", "Starts a new Hangman game")
	list = app.Command("list", "Lists all Hangman games")
	resume = app.Command("resume", "Resumes a current Hangman game")
	resumeId = resume.Arg("id", "Game id to resume").Required().Uint64()
)

func main() {
	conn, err := grpc.Dial("localhost:6666", grpc.WithInsecure())
	if err != nil {
		log.Fatalf("Failed to connect: %v", err)
	}
	defer func() {
		if err := conn.Close(); err != nil {
			log.Printf("Error when trying to close connection: %v", err)
		}
	}()
	c := pb.NewHangmanClient(conn)
	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
	defer cancel()

	switch kingpin.MustParse(app.Parse(os.Args[1:])) {
	case newGame.FullCommand():
		playGame(c, ctx)

	case list.FullCommand():
		listGames(c, ctx)

	case resume.FullCommand():
		resumeGame(c, ctx)
	}
}

func listGames(c pb.HangmanClient, ctx context.Context) {
	stream, err := c.ListGames(ctx, &pb.ListGamesParams{})
	if err != nil {
		log.Fatalf("Failed to req: %v", err)
	}

	for {
		game, err := stream.Recv()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Fatalf("Error while recv from server: %v", err)
		}
		log.Print(game)
	}
}

func resumeGame(c pb.HangmanClient, ctx context.Context) {
	stream, err := c.ResumeGame(ctx, &pb.GameToResume{GameId: *resumeId})
	if err != nil {
		log.Fatalf("Failed to req: %v", err)
	}

	game, err := stream.Recv()
	if err == io.EOF {
		log.Fatal("Could not get created game: premature server exit")
	}
	if err != nil {
		log.Fatalf("Error while recv from server: %v", err)
	}
	log.Printf("Game state: %v", game)

	go recvServerStreamResume(stream)
	stdioGuesses(c, game.GameId)
}

func playGame(c pb.HangmanClient, ctx context.Context) {
	stream, err := c.StartGame(ctx, &pb.StartGameParams{})
	if err != nil {
		log.Fatalf("Failed to req: %v", err)
	}

	game, err := stream.Recv()
	if err == io.EOF {
		log.Fatal("Could not get created game: premature server exit")
	}
	if err != nil {
		log.Fatalf("Error while recv from server: %v", err)
	}
	log.Printf("Game state: %v", game)

	go recvServerStreamNew(stream)
	stdioGuesses(c, game.GameId)
}

func recvServerStreamNew(stream pb.Hangman_StartGameClient) {
	for {
		game, err := stream.Recv()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Fatalf("Error while recv from server: %v", err)
		}
		log.Printf("Game state: %v", game)
	}
}

func recvServerStreamResume(stream pb.Hangman_ResumeGameClient) {
	for {
		game, err := stream.Recv()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Fatalf("Error while recv from server: %v", err)
		}
		log.Printf("Game state: %v", game)
	}
}

func stdioGuesses(c pb.HangmanClient, gameId uint64) {
	log.Print("Take your guess, and press enter to confirm. One char at time please.")
	scan := bufio.NewScanner(os.Stdin)
	for scan.Scan() {
		txt := scan.Text()
		if txt == ":q" {
			break
		} else {
			ctx, _ := context.WithTimeout(context.Background(), 5 * time.Second)
			game, err := c.GuessChar(ctx, &pb.Guess{GameId: gameId, Char: txt})
			if err != nil {
				log.Printf("Error when guessing: %v", err)
				continue
			}
			log.Printf("Game state: %v", game)
		}
	}
}
