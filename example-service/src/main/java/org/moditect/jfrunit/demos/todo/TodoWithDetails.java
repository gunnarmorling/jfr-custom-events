package org.moditect.jfrunit.demos.todo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class TodoWithDetails extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "todoWithDetailsSequence",
            sequenceName = "todo_with_details_id_seq",
            allocationSize = 10,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todoWithDetailsSequence")
    public Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "todo", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    public List<TodoDetail> details = new ArrayList<>();
    public String title;
    public int priority;
    public boolean completed;
    public long userId;
    public String userName;
}
