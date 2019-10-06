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
var subscriptions = make(map[chan string][]pb.Hangman_SubscribeToGameServer)

type server struct {
	pb.UnimplementedHangmanServer
}

func (s *server) StartGame(ctx context.Context, in *pb.StartGameParams) (*pb.Game, error) {
	log.Print("Creating new game")
	game := domain.NewGame()
	games[game.Id] = game
	return game.ToClientResponse(), nil
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

func (s *server) ResumeGame(ctx context.Context, in *pb.GameId) (*pb.Game, error) {
	log.Printf("Resuming game %v", in.GameId)
	game, found := games[in.GameId]
	if !found {
		return nil, status.Errorf(codes.NotFound, "Game not found: %v", in.GameId)
	}
	return game.ToClientResponse(), nil
}

func (s *server) SubscribeToGame(in *pb.GameId, stream pb.Hangman_SubscribeToGameServer) error {
	log.Printf("Subscribing to game %v", in.GameId)
	game, found := games[in.GameId]
	if !found {
		return status.Errorf(codes.NotFound, "Game not found: %v", in.GameId)
	}

	// TODO then would like to have a goroutine under main that loops forever on all channels in map and sends notif to streams attached if stream still open
	subscriptions[game.Notifications] = append(subscriptions[game.Notifications], stream)
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
