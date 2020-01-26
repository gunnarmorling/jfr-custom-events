package dev.morling.demos.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Todo extends PanacheEntity {

    public String title;
    public int priority;
    public boolean completed;
}
