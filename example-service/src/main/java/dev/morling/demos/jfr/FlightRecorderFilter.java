package dev.morling.demos.jfr;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class FlightRecorderFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        JaxRsInvocationEvent event = JaxRsInvocationEvent.INSTANCE.get();

        if (!event.isEnabled()) {
            return;
        }

        event.begin();

        event.method = requestContext.getMethod();
        event.mediaType = String.valueOf(requestContext.getMediaType());
        event.path = String.valueOf(requestContext.getUriInfo().getPath());
        event.length = requestContext.getLength();
        event.queryParameters = requestContext.getUriInfo().getQueryParameters().toString();
        event.headers = requestContext.getHeaders().toString();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        JaxRsInvocationEvent event = JaxRsInvocationEvent.INSTANCE.get();

        if (!event.isEnabled()) {
            return;
        }

        event.responseLength = responseContext.getLength();
        event.responseHeaders = responseContext.getHeaders().toString();
        event.status = responseContext.getStatus();

        event.end();

        if (event.shouldCommit()) {
            event.commit();
        }

        event.reset();
    }
}
