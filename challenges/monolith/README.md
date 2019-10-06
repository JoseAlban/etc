Golang hangman

# Design decisions
- Atomic integer increase and mutex is used to prevent state corruption when shared game has state changed by multiple clients

- We have 4 RPC entries on the service, but only 3 CLI commands, that's because the `Guess` call is done via CLI interactive, not at CLI startup.
