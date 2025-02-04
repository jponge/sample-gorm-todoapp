package main

import (
	"encoding/json"
	"fmt"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"github.com/ilyakaznacheev/cleanenv"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"log"
	"net/http"
)

type Configuration struct {
	HttpPort   string `env:"HTTP_PORT" env-default:"3000"`
	PgHost     string `env:"PG_HOST" env-default:"localhost"`
	PgPort     string `env:"PG_PORT" env-default:"5432"`
	PgUser     string `env:"PG_USER" env-default:"postgres"`
	PgDatabase string `env:"PG_DATABASE" env-default:"postgres"`
	PgPassword string `env:"PG_PASSWORD" env-default:"todo-in-go"`
}

type Application struct {
	Configuration Configuration
	Router        chi.Router
	Database      *gorm.DB
}

func NewApplication() *Application {
	configuration := loadConfiguration()
	return &Application{
		configuration,
		createRouter(),
		databaseConnect(configuration),
	}
}

func loadConfiguration() Configuration {
	log.Println("Loading configuration")
	var configuration Configuration
	err := cleanenv.ReadEnv(&configuration)
	if err != nil {
		log.Fatalf("Error loading configuration: %v", err)
	}
	return configuration
}

func databaseConnect(configuration Configuration) *gorm.DB {
	log.Println("Connecting to the Postgres database")
	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=disable", configuration.PgHost, configuration.PgUser, configuration.PgPassword, configuration.PgDatabase, configuration.PgPort)
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}
	_ = db.AutoMigrate(&Todo{})
	return db
}

func createRouter() *chi.Mux {
	router := chi.NewRouter()
	router.Use(middleware.RequestID)
	router.Use(middleware.Logger)
	router.Use(middleware.Recoverer)
	router.Use(middleware.URLFormat)
	router.Use(render.SetContentType(render.ContentTypeJSON))
	return router
}

func (app *Application) Start() {
	app.createRoutes()

	log.Printf("API endpoint running at http://localhost:%s", app.Configuration.HttpPort)
	addr := fmt.Sprintf(":%s", app.Configuration.HttpPort)
	err := http.ListenAndServe(addr, app.Router)
	if err != nil {
		log.Fatalf("Failed to start the server: %s", err)
	}
}

func (app *Application) createRoutes() {
	log.Println("Configuring HTTP routes")
	app.Router.Get("/", app.listTodos())
	app.Router.Get("/{id}", app.getTodo())
	app.Router.Post("/", app.createTodo())
	app.Router.Put("/{id}", app.updateTodo())
	app.Router.Delete("/{id}", app.deleteTodo())

}

func (app *Application) listTodos() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var todos []Todo
		app.Database.Find(&todos)
		render.JSON(w, r, todos)
	}
}

func (app *Application) createTodo() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		decoder := json.NewDecoder(r.Body)
		todo := &Todo{}
		err := decoder.Decode(todo)
		if err != nil {
			log.Printf("Bad request: %v", err)
			render.Status(r, http.StatusBadRequest)
			return
		}
		app.Database.Create(todo)
		render.Status(r, http.StatusCreated)
		render.JSON(w, r, todo)
	}
}

func (app *Application) updateTodo() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		decoder := json.NewDecoder(r.Body)
		update := &Todo{}
		err := decoder.Decode(update)
		if err != nil {
			log.Printf("Bad request: %v", err)
			render.Status(r, http.StatusBadRequest)
			return
		}
		id := chi.URLParam(r, "id")
		todo := &Todo{}
		app.Database.First(todo, id)
		todo.Title = update.Title
		todo.Complete = update.Complete
		app.Database.Save(todo)
		render.JSON(w, r, todo)
	}
}

func (app *Application) deleteTodo() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id := chi.URLParam(r, "id")
		todo := &Todo{}
		app.Database.Delete(todo, id)
	}
}

func (app *Application) getTodo() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		id := chi.URLParam(r, "id")
		todo := &Todo{}
		app.Database.First(todo, id)
		render.JSON(w, r, todo)
	}
}
