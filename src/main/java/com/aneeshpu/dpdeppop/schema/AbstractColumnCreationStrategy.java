package com.aneeshpu.dpdeppop.schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractColumnCreationStrategy {
    private final Connection connection;

    AbstractColumnCreationStrategy(final Connection connection) {
        this.connection = connection;
    }

    //TODO: Move this to Record.
    Map<String, ColumnTable> foreignKeyTableMap(final String tableName, final Map<String, Record> parentTables) throws SQLException {

        final ResultSet crossReference = this.connection.getMetaData().getCrossReference(null, null, null, null, null, tableName);

        final Map<String, ColumnTable> foreignKeys = new HashMap<String, ColumnTable>();
        while (crossReference.next()) {

            final String primaryKeyTableName = crossReference.getString(Record.PRIMARY_KEY_TABLE_NAME);
            final String primaryKeyColName = crossReference.getString(Record.PRIMARY_KEY_COLUMN_NAME);
            final String foreignKeyColumnName = crossReference.getString(Record.FOREIGN_KEY_COLUMN_NAME);

            final Record primaryRecord = parentTables.get(primaryKeyTableName);

            foreignKeys.put(foreignKeyColumnName, new ColumnTable(primaryKeyColName, primaryRecord));
        }


        return foreignKeys;
    }

    Connection getConnection() {
        return connection;
    }

    protected class ColumnTable {
        private final String primaryKeyColName;

        private final Record primaryRecord;

        public ColumnTable(final String primaryKeyColName, final Record primaryRecord) {
            this.primaryKeyColName = primaryKeyColName;
            this.primaryRecord = primaryRecord;
        }

        Record getPrimaryRecord() {
            return primaryRecord;
        }

        String getPrimaryKeyColName() {
            return primaryKeyColName;
        }
    }
}
