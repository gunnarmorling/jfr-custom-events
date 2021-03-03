package dev.morling.demos.jfrunit.user;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public User getUser(@PathParam("id") long id) {
        if (id == 1) {
            return new User(1, "Bob");
        }
        else if (id == 2) {
            return new User(2, "Alice");
        }
        else if (id == 3) {
            return new User(3, "Sarah");
        }
        else if (id == 4) {
            return new User(4, "Brandon");
        }
        else if (id == 5) {
            return new User(5, "Megan");
        }
        else {
            throw new NotFoundException("No user with id " + id);
        }
    }
}
