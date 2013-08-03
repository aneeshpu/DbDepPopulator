package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataTypeFactory;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Table {
    public static final Logger LOG = Logger.getLogger(Table.class);

    public static final String PRIMARY_KEY_TABLE_NAME = "pktable_name";
    public static final String PRIMARY_KEY_COLUMN_NAME = "pkcolumn_name";
    public static final String FOREIGN_KEY_COLUMN_NAME = "fkcolumn_name";

    private final String name;
    private final Connection connection;
    private final HashMap<String, Map<String, Object>> preassignedValues;
    private final Map<String, Table> parentTables;
    private final Map<String, Column> columns;

    public Table(String name, final Connection connection, final HashMap<String, Map<String, Object>> preassignedValues) {
        this.name = name;
        this.connection = connection;
        this.preassignedValues = preassignedValues;
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

            columns.put(columnName, Column.buildColumn().withName(columnName)
                    .withDataType(DataTypeFactory.create(dataType))
                    .withSize(Double.valueOf(columnSize))
                    .withIsNullable(isNullable)
                    .withIsAutoIncrement(isAutoIncrement)
                    .withReferencingTable(referencingTable)
                    .withReferencingColumn(referencingTable == null ? null : referencingTable.getColumn(primaryKeyColName))
                    .withTable(this)
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
            final String foreignKeyColumnName = importedKeysResultSet.getString(FOREIGN_KEY_COLUMN_NAME);

            if (parentTableIsPreassigned(foreignKeyColumnName)) {
                continue;
            }
            parentTables.put(primaryKeyTableName, new Table(primaryKeyTableName, connection, preassignedValues).initialize());
        }
    }

    private boolean parentTableIsPreassigned(final String foreignKeyColumnName) {
        final Map<String, Object> preassignedCols = preassignedValues.get(name);

        if (preassignedCols == null || preassignedCols.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("table " + name + " has no preassigned keys.");
            }

            return false;
        }

        return preassignedCols.containsKey(foreignKeyColumnName);
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

    private Map<String, ColumnTable> foreignKeyTableMap() throws SQLException {

        final ResultSet crossReference = connection.getMetaData().getCrossReference(null, null, null, null, null, name);

        final Map<String, ColumnTable> foreignKeys = new HashMap<>();
        while (crossReference.next()) {

            final String primaryKeyTableName = crossReference.getString(PRIMARY_KEY_TABLE_NAME);
            final String primaryKeyColName = crossReference.getString(PRIMARY_KEY_COLUMN_NAME);
            final String foreignKeyColumName = crossReference.getString(FOREIGN_KEY_COLUMN_NAME);

            final Table primaryTable = parentTables.get(primaryKeyTableName);

            foreignKeys.put(foreignKeyColumName, new ColumnTable(primaryKeyColName, primaryTable));
        }


        return foreignKeys;
    }

    public List<String> insertDefaultValues(final boolean onlyPopulateParentTables) throws SQLException {

        final List<String> queries = new ArrayList<>();

        insertDefaultValuesIntoParentTables(queries, preassignedValues);

        if (onlyPopulateParentTables) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not populating table " + name + " as onlyPopulateParentTables = " + onlyPopulateParentTables);
            }
            return queries;
        }

        final String query = insertDefaultValuesIntoCurrentTable(preassignedValues);
        queries.add(query);

        return queries;
    }

    private void insertDefaultValuesIntoParentTables(final List<String> queries, final HashMap<String, Map<String, Object>> preassignedValues) throws SQLException {
        for (Map.Entry<String, Table> entry : parentTables.entrySet()) {

            final Table parentTable = entry.getValue();
            final List<String> parentSqls = parentTable.insertDefaultValues(false);
            queries.addAll(parentSqls);
        }
    }

    private String insertDefaultValuesIntoCurrentTable(final HashMap<String, Map<String, Object>> preassignedValues) throws SQLException {
        final String insertSQL = generateInsertQuery();

        if (LOG.isDebugEnabled()) {
            LOG.debug("insert query: " + insertSQL);
        }

        final Statement statement = connection.createStatement();
        statement.executeUpdate(insertSQL, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeys = statement.getGeneratedKeys();
        final Map<String, Column> columnsWithGeneratedValues = getColumnsWithGeneratedValues();

        setGeneratedValuesOnColumns(generatedKeys, columnsWithGeneratedValues);
        return insertSQL;
    }

    private void setGeneratedValuesOnColumns(final ResultSet generatedKeys, final Map<String, Column> columnsWithGeneratedValues) throws SQLException {
        while (generatedKeys.next()) {
            for (Map.Entry<String, Column> entrySet : columnsWithGeneratedValues.entrySet()) {
                final Column column = entrySet.getValue();
                column.populateGeneratedValue(generatedKeys);
            }
        }
    }

    private Map<String, Column> getColumnsWithGeneratedValues() {
        final HashMap<String, Column> columnsWithGeneratedValues = new HashMap<>();
        for (Map.Entry<String, Column> entrySet : columns.entrySet()) {

            if (entrySet.getValue().isAutoIncrement()) {
                columnsWithGeneratedValues.put(entrySet.getKey(), entrySet.getValue());
            }
        }

        return columnsWithGeneratedValues;
    }

    private String generateInsertQuery() {
        final Set<Map.Entry<String, Column>> entrySet = columns.entrySet();

        final StringBuilder fullQuery = new StringBuilder(String.format("insert into %s ", name));
        StringBuilder columnNamesPartOfQuery = new StringBuilder("(");
        final StringBuilder valuesPartOfQuery = new StringBuilder("(");

        for (Map.Entry<String, Column> stringColumnEntry : entrySet) {

            final Column column = stringColumnEntry.getValue();
            if (column.isAutoIncrement()) {
                continue;
            }

            final NameValue nameValue = column.nameValue(this.preassignedValues);

            columnNamesPartOfQuery.append(nameValue.name());
//            columnNamesPartOfQuery.append(",");

            valuesPartOfQuery.append(nameValue.formattedQueryString());
//            valuesPartOfQuery.append(",");
        }

        columnNamesPartOfQuery.deleteCharAt(columnNamesPartOfQuery.length() - 1);
        valuesPartOfQuery.deleteCharAt(valuesPartOfQuery.length() - 1);

        columnNamesPartOfQuery.append(")");
        valuesPartOfQuery.append(")");

        fullQuery.append(columnNamesPartOfQuery.toString());
        fullQuery.append(" values ");
        fullQuery.append(valuesPartOfQuery.toString());

        final String query = fullQuery.toString();
        return query;
    }

    public String name() {
        return name;
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