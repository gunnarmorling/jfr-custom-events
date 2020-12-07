package dev.morling.demos.quarkus;

public class User {
    public long id;
    public String name;

    public User() {
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
