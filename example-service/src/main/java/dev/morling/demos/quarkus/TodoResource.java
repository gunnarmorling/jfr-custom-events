package dev.morling.demos.quarkus;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

@Path("/todo")
public class TodoResource {

    @Inject
    EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Todo> listTodosJson(@QueryParam("filter") String filter) {
        return find(filter);
    }

    private List<Todo> find(String filter) {
        Sort sort = Sort.ascending("completed")
            .and("priority", Direction.Descending)
            .and("title", Direction.Ascending);

        if (filter != null && !filter.isEmpty()) {
            return Todo.find("LOWER(title) LIKE LOWER(?1)", sort, "%" + filter + "%").list();
        }
        else {
            return Todo.findAll(sort).list();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addTodo(Todo todo) {

//        TodoDetail detail = new TodoDetail();
//        detail.todo = todo;
//        detail.title = "Detail 1";
//        todo.details.add(detail);
//
//        detail = new TodoDetail();
//        detail.todo = todo;
//        detail.title = "Detail 2";
//        todo.details.add(detail);

        todo.persist();

        return Response.created(URI.create("/todo/" + todo.id))
            .entity(todo)
            .build();
    }

    @POST
    @Transactional
    @Path("/{id}/delete")
    public Response deleteTodo(@PathParam("id") long id) {
        Todo.delete("id", id);

        return Response.status(301)
            .location(URI.create("/todo"))
            .build();
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response get(@PathParam("id") long id) throws Exception {
        // Query query = em.createNativeQuery("SELECT title FROM todo.Todo, pg_sleep(1) WHERE id = ?");
//         Object res = query.getSingleResult();
//        query.setParameter(1, id);
        Todo res = Todo.findById(id);


//        User user = RestAssured
//            .given()
//                .port(8082)
//            .when()
//                .get("/users/" + res.userId)
//                .as(User.class);
//
//        res.userName = user.name;

//        for (TodoDetail detail : res.details) {
//            System.out.println(detail.title);
//        }

        return Response.ok()
                .entity(res)
                .build();
    }
}
