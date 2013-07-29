package com.aneeshpu.dpdeppop.schema;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

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
            }
        };

    }

    public static Matcher<Object> aNumber() {
        return new BaseMatcher<Object>() {
            @Override
            public boolean matches(final Object o) {
                return o instanceof Integer;
            }

            @Override
            public void describeTo(final Description description) {
            }
        };
    }
}
