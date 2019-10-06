package main

import (
	"context"
	"google.golang.org/grpc"
	"io"
	"log"
	"time"
	//"gopkg.in/alecthomas/kingpin.v2"

	pb "monolith/proto"
)
//
//var (
//	debug   = kingpin.Flag("debug", "Enable debug mode.").Bool()
//	timeout = kingpin.Flag("timeout", "Timeout waiting for ping.").Default("5s").OverrideDefaultFromEnvar("PING_TIMEOUT").Short('t').Duration()
//	ip      = kingpin.Arg("ip", "IP address to ping.").Required().IP()
//	count   = kingpin.Arg("count", "Number of packets to send").Int()
//)

func main() {


	log.Print("test client")
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

	//msg := "sausage"
	//if len(os.Args) > 1 {
	//	msg = os.Args[1]
	//}
	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
	defer cancel()
	playGame(c, ctx)
}

func playGame(c pb.HangmanClient, ctx context.Context) {
	stream, err := c.StartGame(ctx, &pb.StartGameParams{})
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
		log.Printf("Game state: %v", game)
	}
}
