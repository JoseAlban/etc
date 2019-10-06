package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"monolith/server/domain"
	"net"

	"google.golang.org/grpc"
	pb "monolith/proto"
)

var games = map[uint64]*domain.Hangman{}

type server struct {
	pb.UnimplementedHangmanServer
}

func (s *server) StartGame(in *pb.StartGameParams, stream pb.Hangman_StartGameServer) error {
	game := domain.NewGame()
	games[game.Id] = game
	if err := stream.Send(game.ToClientResponse()); err != nil {
		log.Printf("Error while streaming back to client: %v", err)
		return err
	}
	return nil
}

func (s *server) ListGames(in *pb.ListGamesParams, stream pb.Hangman_ListGamesServer) error {
	for _, game := range games {
		if err := stream.Send(game.ToClientResponse()); err != nil {
			log.Printf("Error while streaming back to client: %v", err)
			return err
		}
	}
	return nil
}

func (s *server) ResumeGame(in *pb.GameToResume, stream pb.Hangman_ResumeGameServer) error {
	game, found := games[in.GameId]
	if !found {
		// TODO use grpc status
		return errors.New(fmt.Sprintf("Game not found: %v", in.GameId))
	}

	if err := stream.Send(game.ToClientResponse()); err != nil {
		log.Printf("Error while streaming back to client: %v", err)
		return err
	}
	return nil
}

func (s *server) GuessChar(ctx context.Context, in *pb.Guess) (*pb.Game, error) {
	game, found := games[in.GameId]
	if !found {
		// TODO use grpc status
		return nil, errors.New(fmt.Sprintf("Game not found: %v", in.GameId))
	}

	if err := game.Guess(in.Char); err != nil {
		return nil, err
	}
	return game.ToClientResponse(), nil
}

func main() {
	l, err := net.Listen("tcp", ":6666")
	if err != nil {
		log.Fatalf("Can't listen: %v", err)
	}
	g := grpc.NewServer()
	pb.RegisterHangmanServer(g, &server{})
	log.Print("test server")

	if err = g.Serve(l); err != nil {
		log.Fatalf("failed when serving: %v", err)
	}
}
