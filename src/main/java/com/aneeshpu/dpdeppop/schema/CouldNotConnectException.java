package com.aneeshpu.dpdeppop.schema;

public class CouldNotConnectException extends RuntimeException {
    public CouldNotConnectException(final String url, final String username) {
        super("could not connect to " + url + " as " + username);
    }
}
