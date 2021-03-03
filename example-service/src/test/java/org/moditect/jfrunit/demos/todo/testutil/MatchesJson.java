package org.moditect.jfrunit.demos.todo.testutil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

public class MatchesJson extends TypeSafeMatcher<String> {

    private String expected;

    public MatchesJson(String expected) {
        this.expected = expected;
    }

    @Override
    protected boolean matchesSafely(String json) {
        try {
            JSONAssert.assertEquals(expected, json, false);
            return true;
        }
        catch(AssertionError ae) {
            return false;
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches JSON " + expected);
    }

    public static Matcher<String> matchesJson(String expected) {
        return new MatchesJson(expected);
    }
}
