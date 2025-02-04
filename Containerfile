FROM docker.io/golang:1.23 as builder
WORKDIR /src
COPY go.mod go.sum ./
RUN go mod download
COPY *.go ./
RUN CGO_ENABLED=0 GOOS=linux go build -o /todoapp

FROM scratch
COPY --from=builder /todoapp /todoapp
EXPOSE 3000
ENTRYPOINT ["/todoapp"]