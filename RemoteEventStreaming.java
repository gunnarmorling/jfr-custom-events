///usr/bin/env jbang "$0" "$@" ; exit $? # (1)

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import jdk.management.jfr.RemoteRecordingStream;

class RemoteEventStreaming {

  private static final String JAX_RS_INVOCATION =
      "dev.morling.jfr.JaxRsInvocation";

  public static void main(String[] args) throws Exception {

    try (var rs = new RemoteRecordingStream(getConnection())) {
      rs.enable(JAX_RS_INVOCATION).withoutThreshold();
      rs.onEvent(JAX_RS_INVOCATION, System.out::println);

      rs.startAsync();

      System.out.println("Awaiting events");
      rs.awaitTermination();
    }
}

  private static MBeanServerConnection getConnection() throws Exception {
    String host = "localhost";
    int port = 9010;
    String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

    JMXServiceURL jmxUrl = new JMXServiceURL(url);
    JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
  
    return jmxc.getMBeanServerConnection();
  }
}
