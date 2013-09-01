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

    //TODO: Move this to Table.
    Map<String, ColumnTable> foreignKeyTableMap(final String tableName, final Map<String, Table> parentTables) throws SQLException {

        final ResultSet crossReference = this.connection.getMetaData().getCrossReference(null, null, null, null, null, tableName);

        final Map<String, ColumnTable> foreignKeys = new HashMap<String, ColumnTable>();
        while (crossReference.next()) {

            final String primaryKeyTableName = crossReference.getString(Table.PRIMARY_KEY_TABLE_NAME);
            final String primaryKeyColName = crossReference.getString(Table.PRIMARY_KEY_COLUMN_NAME);
            final String foreignKeyColumnName = crossReference.getString(Table.FOREIGN_KEY_COLUMN_NAME);

            final Table primaryTable = parentTables.get(primaryKeyTableName);

            foreignKeys.put(foreignKeyColumnName, new ColumnTable(primaryKeyColName, primaryTable));
        }


        return foreignKeys;
    }

    Connection getConnection() {
        return connection;
    }

    protected class ColumnTable {
        private final String primaryKeyColName;

        private final Table primaryTable;

        public ColumnTable(final String primaryKeyColName, final Table primaryTable) {
            this.primaryKeyColName = primaryKeyColName;
            this.primaryTable = primaryTable;
        }

        Table getPrimaryTable() {
            return primaryTable;
        }

        String getPrimaryKeyColName() {
            return primaryKeyColName;
        }
    }
}
