package com.aneeshpu.dpdeppop.query;

import com.aneeshpu.dpdeppop.DBDepPopException;
import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.Record;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

class InsertQuery implements Query {

    private final Map<String, Column> columns;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final Record record;
    private final Connection connection;

    public static final Logger LOG = Logger.getLogger(InsertQuery.class);

    public InsertQuery(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record, final Connection connection) {
        this.columns = columns;
        this.preassignedValues = preassignedValues;
        this.record = record;
        this.connection = connection;
    }

    @Override
    public String toString() {
        return queryString();
    }

    private String queryString() {
        final Set<Map.Entry<String, Column>> columnsEntrySet = columns.entrySet();

        //TODO:create a method for generating a formatted name
        final StringBuilder fullQuery = new StringBuilder(String.format("insert into \"%s\" ", record.tableName()));
        StringBuilder columnNamesPartOfQuery = new StringBuilder("(");
        final StringBuilder valuesPartOfQuery = new StringBuilder("(");

        for (Map.Entry<String, Column> stringColumnEntry : columnsEntrySet) {

            final Column column = stringColumnEntry.getValue();
            if (column.isAutoIncrement()) {
                continue;
            }

            columnNamesPartOfQuery.append(column.formattedName(preassignedValues));
            valuesPartOfQuery.append(column.formattedValue(preassignedValues));
        }

        columnNamesPartOfQuery.deleteCharAt(columnNamesPartOfQuery.length() - 1);
        valuesPartOfQuery.deleteCharAt(valuesPartOfQuery.length() - 1);

        columnNamesPartOfQuery.append(")");
        valuesPartOfQuery.append(")");

        fullQuery.append(columnNamesPartOfQuery.toString());
        fullQuery.append(" values ");
        fullQuery.append(valuesPartOfQuery.toString());

        //TODO: add a formatted query string method for primary keys
        try {
            fullQuery.append(" returning \"").append(record.getPrimaryKeys(connection).get(0)).append("\"");
        } catch (SQLException e) {
            LOG.error("", e);

            throw new DBDepPopException("Failed while trying to get primary keys of table," + record.tableName(), e);
        }

        return fullQuery.toString();
    }

    @Override
    public ResultSet execute() throws SQLException {
        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("insert query: " + this);
        }

        final Statement statement = connection.createStatement();
        try {
            statement.execute(queryString());
        } catch (SQLException e) {

            LOG.error("Failed to insert into table:" + record.tableName(), e);
            throw e;

        }

        return statement.getResultSet();
    }
}