package com.example.demo.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Todo {

    private @Id
    @GeneratedValue Long id;

    private String title;

    private boolean complete;

    public Long getId() {
        return id;
    }

    public Todo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Todo setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean getComplete() {
        return complete;
    }

    public Todo setComplete(boolean complete) {
        this.complete = complete;
        return this;
    }

    @Override
    public String toString() {
        return "Todo{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", complete=" + complete +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return complete == todo.complete && Objects.equals(id, todo.id) && Objects.equals(title, todo.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, complete);
    }
}
