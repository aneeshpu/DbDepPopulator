package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.DBDepPopException;import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

class Insert implements Query{

    private final Map<String, Column> columns;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final Record record;

    public static final Logger LOG = Logger.getLogger(Insert.class);

    public Insert(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record) {
        this.columns = columns;
        this.preassignedValues = preassignedValues;
        this.record = record;
    }

    @Override
    public String toString() {
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

            final NameValue nameValue = column.nameValue(preassignedValues);

            //TODO:Push the formattedName and formattedValue method into Column
            columnNamesPartOfQuery.append(nameValue.formattedName());
            valuesPartOfQuery.append(nameValue.formattedValue());
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
            fullQuery.append(" returning \"").append(record.getPrimaryKeys().get(0)).append("\"");
        } catch (SQLException e) {
            LOG.error("", e);

            throw new DBDepPopException("Failed while trying to get primary keys of table," + record.tableName(),e);
        }

        return fullQuery.toString();
    }
}