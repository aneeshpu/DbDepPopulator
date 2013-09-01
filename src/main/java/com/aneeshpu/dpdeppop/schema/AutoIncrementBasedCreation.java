package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataTypeFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AutoIncrementBasedCreation extends AbstractColumnCreationStrategy implements ColumnCreationStrategy {

    public AutoIncrementBasedCreation(final Connection connection) {
        super(connection);
    }

    @Override
    public Map<String, Column> populateColumns(final Table table, final Map<String, Table> parentTables) throws SQLException {

        final Map<String, ColumnTable> foreignKeyTables = foreignKeyTableMap(table.getName(), parentTables);

        final ResultSet columnsResultset = getConnection().getMetaData().getColumns(null, null, table.getName(), null);

        final HashMap<String, Column> stringColumnHashMap = new HashMap<String, Column>();

        while (columnsResultset.next()) {
            final String columnName = columnsResultset.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultset.getString(Column.TYPE_NAME);
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

            stringColumnHashMap.put(columnName, Column.buildColumn().withName(columnName)
                    .withDataType(DataTypeFactory.create(dataType))
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(isAutoIncrement)
                    .withReferencingTable(referencingTable)
                    .withReferencingColumn(referencingTable == null ? null : referencingTable.getColumn(primaryKeyColName))
                    .withTable(table)
                    .asPrimaryKey(table.isPrimaryKey(columnName))
                    .create());
        }

        return stringColumnHashMap;
    }

}