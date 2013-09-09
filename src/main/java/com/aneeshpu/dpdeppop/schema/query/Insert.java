package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class Insert {
    public Insert() {
    }

    public String generateInsertQuery(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record) throws SQLException {
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
        fullQuery.append(" returning \"").append(record.getPrimaryKeys().get(0)).append("\"");

        return fullQuery.toString();
    }
}