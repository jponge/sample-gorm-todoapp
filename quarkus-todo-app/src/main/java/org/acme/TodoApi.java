package org.acme;

import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/")
public class TodoApi {

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Todo create(Todo todo) {
        Log.info("Creating todo " + todo.title);
        todo.persistAndFlush();
        return todo;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> all() {
        Log.info("Getting all todos");
        return Todo.findAll().list();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Todo fetch(long id) {
        Log.info("Getting todo with id " + id);
        Todo todo = Todo.find(id);
        if (todo == null) {
            throw new NotFoundException();
        }
        return todo;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Todo fetch(long id, Todo todo) {
        Log.info("Updating todo with id " + id);
        Todo entity = Todo.find(id);
        if (entity == null) {
            throw new NotFoundException("There's nothing to update");
        }
        entity.title = todo.title;
        entity.complete = todo.complete;
        entity.persistAndFlush();
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(long id) {
        Log.info("Deleting todo with id " + id);
        Todo todo = Todo.find(id);
        if (todo == null) {
            throw new NotFoundException("There's nothing to update");
        }
        todo.delete();
    }
}
