package dev.morling.demos.jfr;

import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.SettingDefinition;
import jdk.jfr.StackTrace;

@Name(JaxRsInvocationEvent.NAME)
@Label("JAX-RS Invocation")
@Category("JAX-RS")
@Description("Invocation of a JAX-RS resource method")
@StackTrace(false)
class JaxRsInvocationEvent extends Event {

    static final String NAME = "dev.morling.jfr.JaxRsInvocation";

    @Label("Resource Method")
    public String method;

    @Label("Media Type")
    public String mediaType;

    @Label("Java Method")
    public String javaMethod;

    @Label("Path")
    public String path;

    @Label("Query Parameters")
    public String queryParameters;

    @Label("Headers")
    public String headers;

    @Label("Length")
    @DataAmount
    public int length;

    @Label("Response Headers")
    public String responseHeaders;

    @Label("Response Length")
    public int responseLength;

    @Label("Response Status")
    public int status;

    @Label("Path Filter")
    @SettingDefinition
    protected boolean pathFilter(PathFilterControl pathFilter) {
      return pathFilter.matches(path);
    }
}
