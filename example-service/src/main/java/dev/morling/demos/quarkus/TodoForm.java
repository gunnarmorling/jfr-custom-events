package dev.morling.demos.quarkus;

import javax.ws.rs.FormParam;

public class TodoForm {

    public @FormParam("title") String title;
    public @FormParam("completed") String completed;
    public @FormParam("priority") String priority;

    public Todo convertIntoTodo() {
        Todo todo = new Todo();
        todo.title = title;
        todo.completed = "on".equals(completed);
        todo.priority = Integer.parseInt(priority);
        return todo;
    }

    public Todo updateTodo(Todo toUpdate) {
        toUpdate.title = title;
        toUpdate.completed = "on".equals(completed);
        toUpdate.priority = Integer.parseInt(priority);
        return toUpdate;
    }
}
