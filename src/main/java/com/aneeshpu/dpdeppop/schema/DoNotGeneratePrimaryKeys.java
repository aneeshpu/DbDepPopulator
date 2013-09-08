package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataTypeFactory;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DoNotGeneratePrimaryKeys implements ColumnCreationStrategy {

    public static final Logger LOG = Logger.getLogger(DoNotGeneratePrimaryKeys.class);
    private final Connection connection;

    public DoNotGeneratePrimaryKeys(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public Map<String, Column> populateColumns(final Record record) throws SQLException {

        final Map<String, ColumnTable> foreignKeyTables = record.foreignKeyTableMap();

        final ResultSet columnsResultSet = connection.getMetaData().getColumns(null, null, record.getName(), null);

        final Map<String, Column> columnMap = new HashMap<String, Column>();

        while (columnsResultSet.next()) {
            final String columnName = columnsResultSet.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultSet.getString(Column.TYPE_NAME);
            final String columnSize = columnsResultSet.getString(Column.COLUMN_SIZE);
            final String isNullable = columnsResultSet.getString(Column.IS_NULLABLE);

            final ColumnTable columnTable = foreignKeyTables.get(columnName);
            Record referencingRecord = null;
            String primaryKeyColName = null;

            if (columnTable != null) {
                referencingRecord = columnTable.getPrimaryRecord();
                primaryKeyColName = columnTable.getPrimaryKeyColName();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Building column " + columnName + " of record " + record.getName());
            }

            columnMap.put(columnName, Column.buildColumn()
                    .withName(columnName)
                    .withDataType(DataTypeFactory.create(dataType))
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(record.isPrimaryKey(columnName) ? YesNo.YES : YesNo.NO)
                    .withReferencingTable(referencingRecord)
                    .withReferencingColumn(referencingRecord == null ? null : referencingRecord.getColumn(primaryKeyColName))
                    .asPrimaryKey(record.isPrimaryKey(columnName))
                    .withTable(record).create());
        }

        return columnMap;

    }
}