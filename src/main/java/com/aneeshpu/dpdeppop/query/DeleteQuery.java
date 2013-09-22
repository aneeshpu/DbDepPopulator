package com.aneeshpu.dpdeppop.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.DbPopulatorException;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

class DeleteQuery implements Query {

    private final Column primaryKeyColumn;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final Record record;
    private final Connection connection;

    public static final String DELETE_QUERY_TEMPLATE = "delete from \"%s\" where %s=%s";

    public static final Logger LOG = Logger.getLogger(DeleteQuery.class);

    DeleteQuery(final Column primaryKeyColumn, final Map<String, Map<String, Object>> preassignedValues, final Record record, final Connection connection) {
        this.primaryKeyColumn = primaryKeyColumn;
        this.preassignedValues = preassignedValues;
        this.record = record;
        this.connection = connection;
    }

    @Override
    public String toString() {
        return queryString();
    }

    private String queryString() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleting record from " + record);
        }

        if (primaryKeyColumn == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not generating a query string for " + record + " as nameValue is null");
            }
            return null;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("deleting record id " + primaryKeyColumn.value() + " from table " + record);
        }

        //TODO:push formattedName and formattedValue into Column
        final NameValue nameValue = primaryKeyColumn.nameValue(preassignedValues);
        final String deleteQuery = String.format(DELETE_QUERY_TEMPLATE, record.tableName(), nameValue.formattedNameWithoutTrailingComma(), nameValue.formattedValueWithoutTrailingComma());

        if (QUERYLOG.isInfoEnabled()) {
            QUERYLOG.info("delete query:" + deleteQuery);
        }
        return deleteQuery;
    }

    @Override
    public ResultSet execute() throws SQLException {
        final String queryString = queryString();
        if (queryString == null) {
            return nullResultset();
        }

        final Statement deleteStatement = connection.createStatement();
        final int noOfRowsDeleted = deleteStatement.executeUpdate(queryString);

        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("no of rows deleted from table " + record + " = " + noOfRowsDeleted);
        }

        if (noOfRowsDeleted <= 0) {
            final String message = "could not delete " + record;
            Record.LOG.error(message);
            throw new DbPopulatorException(message);
        }

        return nullResultset();
    }

    private ResultSet nullResultset() {
        return null;
    }
}

