package org.moditect.jfrunit.demos.user;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeGreetingResourceIT extends UserResourceTest {

    // Execute the same tests but in native mode.
}