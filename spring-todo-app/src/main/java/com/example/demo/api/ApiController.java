package com.example.demo.api;

import com.example.demo.data.Todo;
import com.example.demo.data.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final TodoRepository repository;

    public ApiController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public List<Todo> all() {
        logger.info("Fetching all todos");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Todo fetchOne(@PathVariable Long id) {
        logger.info("Fetching todo with id {}", id);
        return repository.findById(id).orElse(null);
    }

    @PostMapping("/")
    public Todo create(@RequestBody Todo todo) {
        logger.info("Creating todo {}", todo.getTitle());
        return repository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo update) {
        logger.info("Updating todo with id {}", id);
        return repository.findById(id)
                .map(todo -> {
                    todo.setTitle(update.getTitle());
                    todo.setComplete(update.getComplete());
                    return repository.save(todo);
                }).orElseGet(() -> repository.save(update));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Deleting todo with id {}", id);
        repository.deleteById(id);
    }
}
