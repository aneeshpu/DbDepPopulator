package com.aneeshpu.dpdeppop.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.Record;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class QueryFactory {

    public Query generateInsertQuery(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record, final Connection connection) throws SQLException {
        return new InsertQuery(columns, preassignedValues, record, connection);
    }

    public Query generateDeleteQuery(final Column primaryKeyColumn, final Map<String, Map<String, Object>> preassignedValues, final Record record, final Connection connection) throws SQLException {
        //TODO: Push this to the column itself?
        return !primaryKeyColumn.isAssigned() ? new NullQuery() : new DeleteQuery(primaryKeyColumn, preassignedValues, record, connection);
    }
}