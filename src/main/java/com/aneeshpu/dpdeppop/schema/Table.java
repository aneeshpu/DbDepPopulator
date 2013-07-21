package com.aneeshpu.dpdeppop.schema;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Table {
    public static final Logger LOG = Logger.getLogger(Table.class);

    public static final String PRIMARY_KEY_TABLE_NAME = "pktable_name";
    public static final String PRIMARY_KEY_COL_NAME = "pkcolumn_name";
    public static final String FOREIGN_KEY_COLUMN_NAME = "fkcolumn_name";

    private final String name;
    private final Connection connection;
    private final Map<String, Table> parentTables;
    private final Map<String, Column> columns;

    public Table(String name, final Connection connection) {
        this.name = name;
        this.connection = connection;
        parentTables = new HashMap<>();
        columns = new HashMap<>();
    }

    public Table initialize() throws SQLException {

        try {
            populateParents();
            populateColumns();

            return this;

        } catch (SQLException e) {
            LOG.error(e);

            throw e;
        }
    }

    private void populateColumns() throws SQLException {

        final Map<String, ColumnTable> foreignKeyTables = foreignKeyTableMap();

        final ResultSet columnsResultset = connection.getMetaData().getColumns(null, null, name, null);

        while (columnsResultset.next()) {
            final String columnName = columnsResultset.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultset.getString(Column.DATA_TYPE);
            final String columnSize = columnsResultset.getString(Column.COLUMN_SIZE);
            final String isNullable = columnsResultset.getString(Column.IS_NULLABLE);
            final String isAutoIncrement = columnsResultset.getString(Column.IS_AUTOINCREMENT);

            final ColumnTable columnTable = foreignKeyTables.get(columnName);
            Table referencingTable = null;
            String primaryKeyColName = null;

            if (columnTable != null) {
                referencingTable = columnTable.getPrimaryTable();
                primaryKeyColName = columnTable.getPrimaryKeyColName();
            }

            columns.put(columnName, Column.buildColumn().withName(columnName)
                    .withDataType(dataType)
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(isAutoIncrement)
                    .withReferencingTable(referencingTable)
                    .withReferencingColumn(referencingTable == null ? null : referencingTable.getColumn(primaryKeyColName))
                    .create());
        }
    }

    private Column getColumn(final String columnName) {
        return columns.get(columnName);
    }

    private void populateParents() throws SQLException {
        final ResultSet importedKeysResultSet = connection.getMetaData().getImportedKeys(null, null, name);

        while (importedKeysResultSet.next()) {
            final String primaryKeyTableName = importedKeysResultSet.getString(PRIMARY_KEY_TABLE_NAME);
//            parentTables.add(new Table(primaryKeyTableName, connection).initialize());
            parentTables.put(primaryKeyTableName, new Table(primaryKeyTableName, connection).initialize());
        }
    }

    public Map<String, Table> parents() {
        return parentTables;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Table table = (Table) o;

        if (!name.equals(table.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public Map<String, Column> columns() {
        return columns;
    }

    public Map<String, ColumnTable> foreignKeyTableMap() throws SQLException {

        final ResultSet crossReference = connection.getMetaData().getCrossReference(null, null, null, null, null, name);

        final Map<String, ColumnTable> foreignKeys = new HashMap<>();
        while (crossReference.next()) {

            final String primaryKeyTableName = crossReference.getString(PRIMARY_KEY_TABLE_NAME);
            final String primaryKeyColName = crossReference.getString(PRIMARY_KEY_COL_NAME);
            final String foreignKeyColumName = crossReference.getString(FOREIGN_KEY_COLUMN_NAME);

            final Table primaryTable = parentTables.get(primaryKeyTableName);

            foreignKeys.put(foreignKeyColumName, new ColumnTable(primaryKeyColName, primaryTable));
        }


        return foreignKeys;
    }

    private class ColumnTable {
        private final String primaryKeyColName;

        private final Table primaryTable;

        public ColumnTable(final String primaryKeyColName, final Table primaryTable) {
            this.primaryKeyColName = primaryKeyColName;
            this.primaryTable = primaryTable;
        }

        private Table getPrimaryTable() {
            return primaryTable;
        }

        private String getPrimaryKeyColName() {
            return primaryKeyColName;
        }
    }
}