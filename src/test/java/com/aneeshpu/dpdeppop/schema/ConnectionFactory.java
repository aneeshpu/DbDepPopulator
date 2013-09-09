package com.aneeshpu.dpdeppop.schema;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final Logger LOG = Logger.getLogger(ConnectionFactory.class);

    public Connection getConnection() {
        final String url = "jdbc:postgresql://localhost/datapopulator_test";
        final String username = "datapopulator";
        try {
            return DriverManager.getConnection(url, username, "letmein");
        } catch (SQLException e) {
            LOG.error("could not connect to " + url + " as " + username, e);

            throw new CouldNotConnectException(url, username);
        }
    }
}