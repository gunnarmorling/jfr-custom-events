package dev.morling.demos.jfrunit.todo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class TodoWithAvatar extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "todoWithAvatarSequence",
            sequenceName = "todo_with_avatar_id_seq",
            allocationSize = 10,
            initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todoWithAvatarSequence")
    public Long id;

    public String title;
    public int priority;
    public boolean completed;
    public long userId;
    public String userName;

    @Lob
    public byte[] avatar;
}
