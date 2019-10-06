package main

import (
	"context"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
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
	log.Print("Creating new game")
	game := domain.NewGame()
	games[game.Id] = game
	if err := stream.Send(game.ToClientResponse()); err != nil {
		log.Printf("Error while streaming back to client: %v", err)
		return err
	}
	return nil
}

func (s *server) ListGames(in *pb.ListGamesParams, stream pb.Hangman_ListGamesServer) error {
	log.Print("Listing games")
	for _, game := range games {
		if err := stream.Send(game.ToClientResponse()); err != nil {
			log.Printf("Error while streaming back to client: %v", err)
			return err
		}
	}
	return nil
}

func (s *server) ResumeGame(in *pb.GameToResume, stream pb.Hangman_ResumeGameServer) error {
	log.Printf("Resuming game %v", in.GameId)
	game, found := games[in.GameId]
	if !found {
		return status.Errorf(codes.NotFound, "Game not found: %v", in.GameId)
	}

	if err := stream.Send(game.ToClientResponse()); err != nil {
		log.Printf("Error while streaming back to client: %v", err)
		return err
	}
	return nil
}

func (s *server) GuessChar(ctx context.Context, in *pb.Guess) (*pb.Game, error) {
	log.Print("Received a new guess")
	game, found := games[in.GameId]
	if !found {
		return nil, status.Errorf(codes.NotFound, "Game not found: %v", in.GameId)
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

	if err = g.Serve(l); err != nil {
		log.Fatalf("failed when serving: %v", err)
	}
	log.Print("server up")
}
