package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Todo extends PanacheEntity {

    public String title;
    public boolean complete;

    public static Todo find(long id) {
        return findById(id);
    }
}
