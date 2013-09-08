package com.aneeshpu.dpdeppop.schema;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final Logger LOG = Logger.getLogger(ConnectionFactory.class);

    public Connection invoke() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost/datapopulator_test", "datapopulator", "letmein");
        } catch (SQLException e) {
            final String url = "jdbc:postgresql://localhost/datadeppop";
            final String username = "datapopulator";

            LOG.error("could not connect to " + url + " as " + username);
            throw new CouldNotConnectException(url, username);
        }
    }
}