package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.datatypes.DataTypeFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class AutoIncrementBasedCreation implements ColumnCreationStrategy {

    public AutoIncrementBasedCreation() {
    }

    @Override
    public Map<String, Column> populateColumns(final Record record, final Connection connection) throws SQLException {

        final Map<String, ColumnTable> foreignKeyTables = record.foreignKeyTableMap(connection);

        final ResultSet columnsResultSet = connection.getMetaData().getColumns(null, null, record.tableName(), null);

        final HashMap<String, Column> stringColumnHashMap = new HashMap<String, Column>();

        while (columnsResultSet.next()) {
            final String columnName = columnsResultSet.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultSet.getString(Column.TYPE_NAME);
            final String columnSize = columnsResultSet.getString(Column.COLUMN_SIZE);
            final String isNullable = columnsResultSet.getString(Column.IS_NULLABLE);
            final String isAutoIncrement = columnsResultSet.getString(Column.IS_AUTOINCREMENT);

            final ColumnTable columnTable = foreignKeyTables.get(columnName);
            Record referencingRecord = null;
            String primaryKeyColName = null;

            if (columnTable != null) {
                referencingRecord = columnTable.getPrimaryRecord();
                primaryKeyColName = columnTable.getPrimaryKeyColName();
            }

            stringColumnHashMap.put(columnName, Column.buildColumn().withName(columnName)
                    .withDataType(DataTypeFactory.create(dataType))
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(isAutoIncrement)
                    .withReferencingTable(referencingRecord)
                    .withReferencingColumn(referencingRecord == null ? null : referencingRecord.getColumn(primaryKeyColName))
                    .withTable(record)
                    .asPrimaryKey(record.isPrimaryKey(columnName, connection))
                    .create());
        }

        return stringColumnHashMap;
    }

}