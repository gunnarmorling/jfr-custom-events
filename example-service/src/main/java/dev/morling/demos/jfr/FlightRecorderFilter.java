package dev.morling.demos.jfr;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceMethodInvoker;

@Provider
public class FlightRecorderFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        JaxRsInvocationEvent event = new JaxRsInvocationEvent();

        if (!event.isEnabled()) {
            return;
        }

        event.begin();

        requestContext.setProperty(JaxRsInvocationEvent.NAME, event);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        JaxRsInvocationEvent event = (JaxRsInvocationEvent) requestContext.getProperty(JaxRsInvocationEvent.NAME);

        if (event == null || !event.isEnabled()) {
            return;
        }

        event.end();
        event.path = String.valueOf(requestContext.getUriInfo().getPath());

        if (event.shouldCommit()) {
            event.method = requestContext.getMethod();
            event.mediaType = String.valueOf(requestContext.getMediaType());
            event.length = requestContext.getLength();
            event.queryParameters = requestContext.getUriInfo().getQueryParameters().toString();
            event.headers = requestContext.getHeaders().toString();
            event.javaMethod = getJavaMethod(requestContext);
            event.responseLength = responseContext.getLength();
            event.responseHeaders = responseContext.getHeaders().toString();
            event.status = responseContext.getStatus();

            event.commit();
        }
    }

    private String getJavaMethod(ContainerRequestContext requestContext) {
        String propName = "org.jboss.resteasy.core.ResourceMethodInvoker";
        ResourceMethodInvoker invoker = (ResourceMethodInvoker)requestContext.getProperty(propName);
        return invoker.getMethod().toString();
    }
}
