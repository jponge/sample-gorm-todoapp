set shell := ["zsh", "-cu"]

# Go tasks
run:
    go run .

build:
    go build -o todo-app .

build-minimal:
    @echo "💡Removing debug symbols from Go binaries is not the best idea"
    go build -ldflags "-w -s" -o todo-app-minimal .

clean:
    - rm todo-app*

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

# Running tasks

push-data port:
    http :{{port}}/ title="Buy bread" complete:=false
    http :{{port}}/ title="Buy cheese" complete:=false
    http :{{port}}/ title="Buy red wine" complete:=true
    http :{{port}}/ title="Buy chocolate" complete:=false

apply-load port:
    wrk --latency --threads 4 --connections 20 --duration 30s http://localhost:{{port}}
    wrk --latency --threads 4 --connections 20 --duration 30s http://localhost:{{port}}/1