package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataTypeFactory;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoNotGeneratePrimaryKeys extends AbstractColumnCreationStrategy implements ColumnCreationStrategy {

    public static final Logger LOG = Logger.getLogger(DoNotGeneratePrimaryKeys.class);

    public static final String COLUMN_NAME = "column_name";

    public DoNotGeneratePrimaryKeys(final Connection connection) {
        super(connection);
    }

    @Override
    public Map<String, Column> populateColumns(final Table table, final Map<String, Table> parentTables) throws SQLException {

        final Map<String, ColumnTable> foreignKeyTables = foreignKeyTableMap(table.getName(), parentTables);
        final List<String> primaryKeys = primaryKeys(table);

        final ResultSet columnsResultSet = getConnection().getMetaData().getColumns(null, null, table.getName(), null);

        final Map<String, Column> columnMap = new HashMap<String, Column>();

        while (columnsResultSet.next()) {
            final String columnName = columnsResultSet.getString(Column.COLUMN_NAME);
            final String dataType = columnsResultSet.getString(Column.TYPE_NAME);
            final String columnSize = columnsResultSet.getString(Column.COLUMN_SIZE);
            final String isNullable = columnsResultSet.getString(Column.IS_NULLABLE);

            final ColumnTable columnTable = foreignKeyTables.get(columnName);
            Table referencingTable = null;
            String primaryKeyColName = null;

            if (columnTable != null) {
                referencingTable = columnTable.getPrimaryTable();
                primaryKeyColName = columnTable.getPrimaryKeyColName();
            }

            if(LOG.isDebugEnabled()){
                LOG.debug("Building column " + columnName + " of table " + table.getName());
            }

            columnMap.put(columnName, Column.buildColumn().withName(columnName)
                    .withDataType(DataTypeFactory.create(dataType))
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(primaryKeys.contains(columnName) ? YesNo.YES : YesNo.NO)
                    .withReferencingTable(referencingTable)
                    .withReferencingColumn(referencingTable == null ? null : referencingTable.getColumn(primaryKeyColName))
                    .withTable(table)
                    .create());
        }

        return columnMap;

    }

    private List<String> primaryKeys(final Table table) throws SQLException {
        final ResultSet primaryKeysResultSet = getConnection().getMetaData().getPrimaryKeys(null, null, table.getName());

        final List<String> primaryKeys = new ArrayList<String>();
        while (primaryKeysResultSet.next()) {
            final String primaryKey = primaryKeysResultSet.getString(COLUMN_NAME);
            primaryKeys.add(primaryKey);
        }

        return primaryKeys;
    }
}
