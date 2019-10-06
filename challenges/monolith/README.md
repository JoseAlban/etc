Golang hangman

# Design decisions
- Atomic integer increase and mutex is used to prevent state corruption when shared game has state changed by multiple clients

- We have some RPC entries on the service, but only 3 CLI commands, that's because the `Guess` call is done via CLI interactive, not at CLI startup.

# Testing considerations
- We need to mainly test the domain functions in `hangman.go`

- Ideally an integration test covering multi clients playing same game against server, check that they receive proper notifications