package org.acme;

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
        todo.persistAndFlush();
        return todo;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Todo> all() {
        return Todo.findAll().list();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Todo fetch(long id) {
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
        Todo todo = Todo.find(id);
        if (todo == null) {
            throw new NotFoundException("There's nothing to update");
        }
        todo.delete();
    }
}
