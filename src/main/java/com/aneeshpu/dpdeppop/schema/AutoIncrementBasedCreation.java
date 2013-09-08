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
    public Map<String, Column> populateColumns(final Record record) throws SQLException {

        final Map<String, ColumnTable> foreignKeyTables = record.foreignKeyTableMap();

        final ResultSet columnsResultset = getConnection().getMetaData().getColumns(null, null, record.getName(), null);

        final HashMap<String, Column> stringColumnHashMap = new HashMap<String, Column>();

        while (columnsResultset.next()) {
            final String columnName = columnsResultset.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultset.getString(Column.TYPE_NAME);
            final String columnSize = columnsResultset.getString(Column.COLUMN_SIZE);
            final String isNullable = columnsResultset.getString(Column.IS_NULLABLE);
            final String isAutoIncrement = columnsResultset.getString(Column.IS_AUTOINCREMENT);

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
                    .asPrimaryKey(record.isPrimaryKey(columnName))
                    .create());
        }

        return stringColumnHashMap;
    }

}