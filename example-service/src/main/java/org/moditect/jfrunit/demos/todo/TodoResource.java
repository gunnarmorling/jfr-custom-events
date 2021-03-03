package org.moditect.jfrunit.demos.todo;

import java.net.URI;
import java.util.Base64;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.restassured.RestAssured;

@Path("/todo")
public class TodoResource {

    @Inject
    EntityManager em;

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 20; i++) {
            sb.append("Hello World, hello JfrUnit! ");
        }
        System.out.println(Base64.getEncoder().encodeToString(sb.toString().getBytes()));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addTodo(Todo todo) {
        todo.persist();

        return Response.created(URI.create("/todo/" + todo.id))
            .entity(todo)
            .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo-with-avatar")
    @Transactional
    public Response addTodoWithAvatar(TodoWithAvatar todo) {
        todo.persist();

        return Response.created(URI.create("/todo-with-avatar/" + todo.id))
            .entity(todo)
            .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/todo-with-details")
    @Transactional
    public Response addTodoWithDetails(TodoWithDetails todo) {
        TodoDetail detail = new TodoDetail();
        detail.todo = todo;
        detail.title = "Detail 1";
        todo.details.add(detail);

        detail = new TodoDetail();
        detail.todo = todo;
        detail.title = "Detail 2";
        todo.details.add(detail);

        detail = new TodoDetail();
        detail.todo = todo;
        detail.title = "Detail 3";
        todo.details.add(detail);

        detail = new TodoDetail();
        detail.todo = todo;
        detail.title = "Detail 4";
        todo.details.add(detail);

        todo.persist();

        return Response.created(URI.create("/todo/" + todo.id))
            .entity(todo)
            .build();
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response get(@PathParam("id") long id) throws Exception {
        Todo todo = Todo.findById(id);

        return todo != null ?
                Response.ok().entity(todo).build() :
                Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/with-allocation-regression/{id}")
    public Response getWithAllocationRegression(@PathParam("id") long id) throws Exception {
        Todo todo = Todo.findById(id);

        User user = RestAssured
            .given()
                .port(8082)
            .when()
                .get("/users/" + todo.userId)
                .as(User.class);

        todo.userName = user.name;

        return todo != null ?
                Response.ok().entity(todo).build() :
                Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/with-io-regression/{id}")
    public Response getWithIoRegression(@PathParam("id") long id) throws Exception {
        TodoWithAvatar todo = TodoWithAvatar.findById(id);

        return todo != null ?
                Response.ok().entity(todo).build() :
                Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/with-sql-regression/{id}")
    public Response getWithSqlRegression(@PathParam("id") long id) throws Exception {
        TodoWithDetails todo = TodoWithDetails.findById(id);

        for (int i = 0; i < todo.details.size(); i++) {
            System.out.println(todo.details.get(i).title);
        }

        return todo != null ?
                Response.ok().entity(todo).build() :
                Response.status(Status.NOT_FOUND).build();
    }
}
