package dev.morling.demos.jfrunit.todo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Todo extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "todoSequence",
            sequenceName = "todo_id_seq",
            allocationSize = 10,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todoSequence")
    public Long id;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "todo")
//    public List<TodoDetail> details = new ArrayList<>();
    public String title;
    public int priority;
    public boolean completed;
    public long userId;
    public String userName;
}
