package com.aneeshpu.dpdeppop.schema;

import java.sql.Connection;

abstract class AbstractColumnCreationStrategy {
    private final Connection connection;

    AbstractColumnCreationStrategy(final Connection connection) {
        this.connection = connection;
    }

    Connection getConnection() {
        return connection;
    }
}