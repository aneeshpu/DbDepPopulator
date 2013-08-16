package com.aneeshpu.dpdeppop.schema;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.regex.Pattern;

public class Matchers {
    public static Matcher<Object> anInt() {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(final Object o) {
                if (!(o instanceof String)) {
                    return false;
                }

                final String stringRepresentation = (String) o;
                try {
                    Integer.parseInt(stringRepresentation);
                    return true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }

            }

            @Override
            public void describeTo(final Description description) {
            }
        };
    }

    public static Matcher<Object> aFloat() {

        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(final Object o) {

                if (!(o instanceof String)) {
                    return false;
                }

                final String stringRepresentation = (String) o;
                try {
                    Float.parseFloat(stringRepresentation);
                    return true;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }

            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("a float");
            }
        };

    }

    public static Matcher<Object> aNumber() {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(final Object o) {
                final Pattern numberPattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
                return numberPattern.matcher(String.valueOf(o)).matches();
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("a number");
            }
        };
    }


    public static Matcher<Object> aString() {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(final Object o) {

                final Pattern stringPattern = Pattern.compile("'?\\w'?");
                return stringPattern.matcher(String.valueOf(o)).matches();
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("a string");
            }
        };
    }

    public static Matcher<? super List<? extends Object>> contains(final Object expectedObject) {
        return new BaseMatcher<List<? extends Object>>() {
            @Override
            public boolean matches(final Object o) {
                if (!(o instanceof List)) {

                    return false;
                }
                List<Object> objects = (List<Object>) o;
                return objects.contains(expectedObject);
            }

            @Override
            public void describeTo(final Description description) {
            }
        };
    }

    public static Matcher<String> date() {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(final Object o) {
                if (!(o instanceof String))
                    return false;

                String possibleDatePattern = (String)o;
                final Pattern dashSeparatedDate = Pattern.compile("'?\\d+-\\d+-\\d+'?");
                return dashSeparatedDate.matcher(possibleDatePattern).matches();
            }

            @Override
            public void describeTo(final Description description) {
            }
        };
    }
}
