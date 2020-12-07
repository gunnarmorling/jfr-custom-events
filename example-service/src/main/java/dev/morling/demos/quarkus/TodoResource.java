package dev.morling.demos.quarkus;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

@Path("/todo")
public class TodoResource {

    @Inject
    Template error;

    @Inject
    Template todo;

    @Inject
    Template todos;

    @Inject
    EntityManager em;

    final List<Integer> priorities = IntStream.range(1, 6).boxed().collect(Collectors.toList());

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listTodos(@QueryParam("filter") String filter) {
        return todos.data("todos", find(filter))
            .data("priorities", priorities)
            .data("filter", filter)
            .data("filtered", filter != null && !filter.isEmpty());
    }

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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Path("/new")
    public Response addTodo(@MultipartForm TodoForm todoForm) {
        Todo todo = todoForm.convertIntoTodo();
        todo.persist();

        return Response.status(301)
            .location(URI.create("/todo"))
            .build();
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

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{id}/edit")
    public TemplateInstance updateForm(@PathParam("id") long id) {
        Todo loaded = Todo.findById(id);

        if (loaded == null) {
            return error.data("error", "Todo with id " + id + " does not exist.");
        }

        return todo.data("todo", loaded)
            .data("priorities", priorities)
            .data("update", true);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Path("/{id}/edit")
    public Object updateTodo(
        @PathParam("id") long id,
        @MultipartForm TodoForm todoForm) {

        Todo loaded = Todo.findById(id);

        if (loaded == null) {
            return error.data("error", "Todo with id " + id + " has been deleted after loading this form.");
        }

        loaded = todoForm.updateTodo(loaded);

        return Response.status(301)
            .location(URI.create("/todo"))
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

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/multi/{ids}")
    public Response getMulti(@PathParam("ids") String id) {
        MultiIdentifierLoadAccess<Todo> multi = em.unwrap(Session.class).byMultipleIds(Todo.class);
        String[] ids = id.split(",");
        List<Long> longIds = Arrays.stream(ids)
            .map(Long::valueOf)
            .collect(Collectors.toList());

        List<Todo> todoList = multi.multiLoad(longIds);

        return Response.ok()
                .entity(todoList)
                .build();
    }
}
