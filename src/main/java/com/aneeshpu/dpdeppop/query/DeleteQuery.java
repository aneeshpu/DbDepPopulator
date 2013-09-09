package com.aneeshpu.dpdeppop.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.DbPopulatorException;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;

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

    DeleteQuery(final Column primaryKeyColumn, final Map<String, Map<String, Object>> preassignedValues, final Record record, final Connection connection) {
        this.primaryKeyColumn = primaryKeyColumn;
        this.preassignedValues = preassignedValues;
        this.record = record;
        this.connection = connection;
    }

    @Override
    public String toString() {
        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("deleting record from " + this.record);
        }

        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("deleting record id " + this.primaryKeyColumn.value() + " from table " + this.record);
        }

        //TODO:push formattedName and formattedValue into Column
        final NameValue nameValue = this.primaryKeyColumn.nameValue(this.preassignedValues);
        final String deleteQuery = String.format("delete from \"%s\" where %s=%s", this.record.tableName(), nameValue.formattedNameWithoutTrailingComma(), nameValue.formattedValueWithoutTrailingComma());

        if (Record.LOG.isInfoEnabled()) {
            Record.LOG.info("delete query: " + deleteQuery);
        }
        return deleteQuery;
    }

    @Override
    public ResultSet execute() throws SQLException {
        final Statement deleteStatement = connection.createStatement();
        final int noOfRowsDeleted = deleteStatement.executeUpdate(this.toString());

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