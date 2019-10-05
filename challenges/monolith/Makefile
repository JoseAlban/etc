PROTOC_PLUGIN_BIN=$(shell go env GOPATH)/bin/protoc-gen-go

generate:
	protoc -I ./proto hangman.proto --plugin="$(PROTOC_PLUGIN_BIN)" --go_out=plugins=grpc:proto

build: generate
	go build -o bin/client ./client
	go build -o bin/server ./server
