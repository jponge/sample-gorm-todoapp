set shell := ["zsh", "-cu"]

# Go tasks
run:
    go run .

build:
    go build .

clean:
    - rm todoapp

# Postgres tasks

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

# Podman tasks

build-container-image:
    podman build . --tag todoapp

create-pod:
    podman kube play --replace pod.yml

delete-pod:
    - podman pod kill todo-app-pod
    - podman pod rm todo-app-pod