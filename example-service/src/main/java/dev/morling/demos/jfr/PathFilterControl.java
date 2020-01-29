package dev.morling.demos.jfr;

import java.util.Set;
import java.util.regex.Pattern;

import jdk.jfr.SettingControl;

public class PathFilterControl extends SettingControl {

    private Pattern pattern = Pattern.compile(".*");

    @Override
    public void setValue(String value) {
        System.out.println("SetValue #### " + value);
        this.pattern = Pattern.compile(value);
    }

    @Override
    public String combine(Set<String> values) {
        System.out.println("Combine ##### " + values);
        return String.join("|", values);
    }

    @Override
    public String getValue() {
        System.out.println("GetValue ##### " + pattern);
        return pattern.toString();
    }

    public boolean matches(String s) {
        System.out.println("Matches #### " + pattern + " " + s);
        return pattern.matcher(s).matches();
    }
}
