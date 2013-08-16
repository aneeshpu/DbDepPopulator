package com.aneeshpu.dpdeppop.schema;

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
    public static final String COLUMN_NAME = "column_name";

    private final String name;
    private final Connection connection;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final Map<String, Table> parentTables;
    private final Map<String, Column> columns;
    private final ColumnCreationStrategy columnCreationStrategy;

    public Table(String name, final Connection connection, final Map<String, Map<String, Object>> preassignedValues, final ColumnCreationStrategy columnCreationStrategy) {
        this.name = name;
        this.connection = connection;
        this.preassignedValues = preassignedValues;
        parentTables = new HashMap<String, Table>();
        columns = new HashMap<String, Column>();
        this.columnCreationStrategy = columnCreationStrategy;
    }

    public Table initialize() throws SQLException {

        try {
            populateParents();
            final Map<String, Column> stringColumnHashMap = columnCreationStrategy.populateColumns(this, parentTables);
            columns.putAll(stringColumnHashMap);

            return this;

        } catch (SQLException e) {
            LOG.error(e);

            throw e;
        }
    }

    String getName() {
        return name;
    }

    public Column getColumn(final String columnName) {
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
            parentTables.put(primaryKeyTableName, new Table(primaryKeyTableName, connection, preassignedValues, columnCreationStrategy).initialize());
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

    public Map<String, Table> populate(final boolean onlyPopulateParentTables) throws SQLException {

        final Map<String, Table> tables = new HashMap<String, Table>();
        insertDefaultValuesIntoParentTables(tables);

        if (onlyPopulateParentTables) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not populating table " + name + " as onlyPopulateParentTables = " + onlyPopulateParentTables);
            }
            return tables;
        }

        insertDefaultValuesIntoCurrentTable();
        tables.put(name, this);
        return tables;
    }

    private void insertDefaultValuesIntoParentTables(final Map<String, Table> tables) throws SQLException {
        for (Map.Entry<String, Table> entry : parentTables.entrySet()) {

            final Table parentTable = entry.getValue();
            final Map<String, Table> parentTables = parentTable.populate(false);

            tables.putAll(parentTables);
        }
    }

    private String insertDefaultValuesIntoCurrentTable() throws SQLException {
        final String insertSQL = generateInsertQuery();

        if (LOG.isDebugEnabled()) {
            LOG.debug("insert query: " + insertSQL);
        }

        final Statement statement = connection.createStatement();
//        statement.executeUpdate(insertSQL, Statement.RETURN_GENERATED_KEYS);
        statement.execute(insertSQL);

//        final ResultSet generatedKeys = statement.getGeneratedKeys();
        final ResultSet generatedKeys = statement.getResultSet();
        setGeneratedValuesOnColumns(generatedKeys, getColumnsWithGeneratedValues());
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
        final HashMap<String, Column> columnsWithGeneratedValues = new HashMap<String, Column>();
        for (Map.Entry<String, Column> entrySet : columns.entrySet()) {

            if (entrySet.getValue().isAutoIncrement()) {
                columnsWithGeneratedValues.put(entrySet.getKey(), entrySet.getValue());
            }
        }

        return columnsWithGeneratedValues;
    }

    private String generateInsertQuery() throws SQLException {
        final Set<Map.Entry<String, Column>> entrySet = columns.entrySet();

        final StringBuilder fullQuery = new StringBuilder(String.format("insert into \"%s\" ", name));
        StringBuilder columnNamesPartOfQuery = new StringBuilder("(");
        final StringBuilder valuesPartOfQuery = new StringBuilder("(");

        for (Map.Entry<String, Column> stringColumnEntry : entrySet) {

            final Column column = stringColumnEntry.getValue();
            if (column.isAutoIncrement()) {
                continue;
            }

            final NameValue nameValue = column.nameValue(this.preassignedValues);

            columnNamesPartOfQuery.append(nameValue.formattedName());
            valuesPartOfQuery.append(nameValue.formattedValue());
        }

        columnNamesPartOfQuery.deleteCharAt(columnNamesPartOfQuery.length() - 1);
        valuesPartOfQuery.deleteCharAt(valuesPartOfQuery.length() - 1);

        columnNamesPartOfQuery.append(")");
        valuesPartOfQuery.append(")");

        fullQuery.append(columnNamesPartOfQuery.toString());
        fullQuery.append(" values ");
        fullQuery.append(valuesPartOfQuery.toString());
        //TODO: add a formatted query string method for primary keys
        fullQuery.append(" returning \"" + getPrimaryKeys().get(0) + "\"");

        final String query = fullQuery.toString();
        return query;
    }

    public String name() {
        return name;
    }

    public List<String> getPrimaryKeys() throws SQLException {
        final ResultSet primaryKeysResultSet = connection.getMetaData().getPrimaryKeys(null, null, name);

        final List<String> primaryKeys = new ArrayList<String>();
        while (primaryKeysResultSet.next()) {
            final String primaryKey = primaryKeysResultSet.getString(Table.COLUMN_NAME);
            primaryKeys.add(primaryKey);
        }

        return primaryKeys;

    }
}