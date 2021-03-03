package org.moditect.jfrunit.demos.todo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class TodoDetail extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "todoDetailSequence",
            sequenceName = "todo_detail_id_seq",
            allocationSize = 10,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todoDetailSequence")
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    public TodoWithDetails todo;
    public String title;
}
