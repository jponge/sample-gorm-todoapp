set shell := ["zsh", "-cu"]

run:
    go run .

build:
    go build .

clean:
    - rm todoapp

run-postgres:
    podman container run \
        --name postgres-todo \
        -e POSTGRES_USER='postgres' \
        -e POSTGRES_PASSWORD='todo-in-go' \
        -p 5432:5432 -d docker.io/postgres:17

kill-postgres:
    - podman container kill postgres-todo
    - podman container rm postgres-todo

restart-postgres: kill-postgres run-postgres