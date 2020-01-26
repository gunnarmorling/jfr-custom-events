package dev.morling.demos.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name(JaxRsInvocationEvent.NAME)
@Label("JAX-RS Invocation")
@Category("JAX-RS")
@Description("Invocation of a JAX-RS resource method")
class JaxRsInvocationEvent extends Event {

    static final String NAME = "dev.morling.jfr.JaxRsInvocation";

    public static final ThreadLocal<JaxRsInvocationEvent> INSTANCE = ThreadLocal.withInitial(JaxRsInvocationEvent::new);

    @Label("Resource method name")
    public String method;

    @Label("Media type")
    public String mediaType;

    @Label("Path")
    public String path;

    @Label("Query parameters")
    public String queryParameters;

    @Label("Headers")
    public String headers;

    @Label("Length")
    public int length;

    @Label("Response headers")
    public String responseHeaders;

    @Label("Response length")
    public int responseLength;

    @Label("Response status")
    public int status;

    public void reset() {
        method = null;
        mediaType = null;
        path = null;
        length = -1;
        queryParameters = null;
        headers = null;
        responseHeaders = null;
        responseLength = -1;
        status = -1;
    }
}
