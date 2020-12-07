package dev.morling.demos.quarkus;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Todo extends PanacheEntity {

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "todo")
//    public List<TodoDetail> details = new ArrayList<>();
    public String title;
    public int priority;
    public boolean completed;
//    public long userId;
//    public String userName;
}
