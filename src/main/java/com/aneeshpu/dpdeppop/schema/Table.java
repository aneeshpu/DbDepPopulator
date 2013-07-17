package com.aneeshpu.dpdeppop.schema;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {
    public static final Logger LOG = Logger.getLogger(Table.class);

    public static final String PRIMARY_KEY_TABLE_NAME = "pktable_name";

    private final String name;
    private final Connection connection;
    private final List<Table> parentTables;
    private final List<Column> columns;

    public Table(String name, final Connection connection) {
        this.name = name;
        this.connection = connection;
        parentTables = new ArrayList<>();
        columns = new ArrayList<>();
    }

    public Table initialize() throws SQLException {

        try {
            populateColumns();

            populateParents();
            return this;

        } catch (SQLException e) {
            LOG.error(e);

            throw e;
        }
    }

    private void populateColumns() throws SQLException {
        final ResultSet columnsResultset = connection.getMetaData().getColumns(null, null, name, null);

        while(columnsResultset.next()){
            final String columnName = columnsResultset.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultset.getString(Column.DATA_TYPE);
            final String columnSize = columnsResultset.getString(Column.COLUMN_SIZE);
            final String isNullable = columnsResultset.getString(Column.IS_NULLABLE);
            final String isAutoIncrement = columnsResultset.getString(Column.IS_AUTOINCREMENT);

            columns.add(Column.buildColumn().withName(columnName)
                    .withDataType(dataType)
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(isAutoIncrement)
                    .create());
        }
    }

    private void populateParents() throws SQLException {
        final ResultSet importedKeysResultSet = connection.getMetaData().getImportedKeys(null, null, name);

        while (importedKeysResultSet.next()) {
            final String primaryKeyTableName = importedKeysResultSet.getString(PRIMARY_KEY_TABLE_NAME);
            parentTables.add(new Table(primaryKeyTableName, connection).initialize());
        }
    }

    public List<Table> parents() {
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

    public List<Column> columns() {
        return columns;
    }
}