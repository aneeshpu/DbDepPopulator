package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.Record;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class QueryFactory {
    private final Connection connection;

    public QueryFactory(final Connection connection) {
        this.connection = connection;
    }

    public Query generateInsertQuery(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record) throws SQLException {
        return new Insert(columns, preassignedValues, record, connection);
    }

    public void generateDeleteQuery(final Column primaryKeyColumn, final Map<String, Map<String, Object>> preassignedValues, final String tableName, final Record record) throws SQLException {

        final Delete deleteQuery = new Delete(primaryKeyColumn, preassignedValues, record, connection);

        deleteQuery.execute();

/*
        final Statement deleteStatement = connection.createStatement();
        final int noOfRowsDeleted = deleteStatement.executeUpdate(deleteQuery.toString());

        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("no of rows deleted from table " + record + " = " + noOfRowsDeleted);
        }
        if (noOfRowsDeleted <= 0) {
            final String message = "could not delete " + record;
            Record.LOG.error(message);
            throw new DbPopulatorException(message);
        }
*/
    }

}