package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.query.Query;
import com.aneeshpu.dpdeppop.schema.query.QueryFactory;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Record {
    public static final Logger LOG = Logger.getLogger(Record.class);

    public static final String PRIMARY_KEY_TABLE_NAME = "pktable_name";
    public static final String PRIMARY_KEY_COLUMN_NAME = "pkcolumn_name";
    public static final String FOREIGN_KEY_COLUMN_NAME = "fkcolumn_name";
    public static final String COLUMN_NAME = "column_name";

    private final String name;
    private final Connection connection;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final QueryFactory queryFactory;
    private Map<String, Record> parentTables;
    private final Map<String, Column> columns;
    private final ColumnCreationStrategy columnCreationStrategy;

    public Record(String name, final Connection connection, final Map<String, Map<String, Object>> preassignedValues, final ColumnCreationStrategy columnCreationStrategy, final QueryFactory queryFactory) {
        this.name = name;
        this.connection = connection;
        this.preassignedValues = preassignedValues;
        this.queryFactory = queryFactory;
        this.columnCreationStrategy = columnCreationStrategy;

        columns = new HashMap<String, Column>();
    }

    private Record initialize(final Map<String, Record> parentTables) throws SQLException {

        try {
            this.parentTables = populateParents(parentTables);
            final Map<String, Column> stringColumnHashMap = columnCreationStrategy.populateColumns(this);
            columns.putAll(stringColumnHashMap);

            return this;

        } catch (SQLException e) {
            LOG.error(e);

            throw e;
        }
    }

    public String tableName() {
        return name;
    }

    public Column getColumn(final String columnName) {
        return columns.get(columnName);
    }

    private Map<String, Record> populateParents(final Map<String, Record> parentTables) throws SQLException {
        final ResultSet importedKeysResultSet = connection.getMetaData().getImportedKeys(null, null, name);

        while (importedKeysResultSet.next()) {
            final String primaryKeyTableName = importedKeysResultSet.getString(PRIMARY_KEY_TABLE_NAME);
            final String foreignKeyColumnName = importedKeysResultSet.getString(FOREIGN_KEY_COLUMN_NAME);

            if (parentTableIsPreassigned(foreignKeyColumnName) || parentTables.containsKey(primaryKeyTableName)) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(foreignKeyColumnName + " is either pre-assigned or " + primaryKeyTableName + " has already been initialized");
                }
                continue;
            }

            parentTables.put(primaryKeyTableName, new RecordBuilder().withQueryFactory(connection).setName(primaryKeyTableName).setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(columnCreationStrategy).createRecord().initialize(parentTables));
        }

        return parentTables;

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

    private Map<String, Record> parents() {
        return parentTables;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Record record = (Record) o;

        if (!name.equals(record.name)) return false;

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

    public Map<String, Record> populate(final boolean onlyPopulateParentTables) throws SQLException {

        initialize(new LinkedHashMap<String, Record>());

        final Map<String, Record> tables = new HashMap<String, Record>();
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

    private void insertDefaultValuesIntoParentTables(final Map<String, Record> tables) throws SQLException {

        for (Map.Entry<String, Record> entry : parentTables.entrySet()) {

            final Record parentRecord = entry.getValue();
            parentRecord.insertDefaultValuesIntoCurrentTable();
            tables.put(parentRecord.name, parentRecord);

            tables.putAll(parentTables);
        }
    }

    private String insertDefaultValuesIntoCurrentTable() throws SQLException {

        final ResultSet generatedKeys = queryFactory.generateInsertQuery(columns, this.preassignedValues, this).execute();
        setGeneratedValuesOnColumns(generatedKeys, getColumnsWithGeneratedValues());

        return queryFactory.generateInsertQuery(columns, this.preassignedValues, this).toString();
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
        final Map<String, Column> columnsWithGeneratedValues = new HashMap<String, Column>();
        for (Map.Entry<String, Column> entrySet : columns.entrySet()) {

            if (entrySet.getValue().isAutoIncrement()) {
                columnsWithGeneratedValues.put(entrySet.getKey(), entrySet.getValue());
            }
        }

        return columnsWithGeneratedValues;
    }

    public String name() {
        return name;
    }

    public List<String> getPrimaryKeys() throws SQLException {
        final ResultSet primaryKeysResultSet = connection.getMetaData().getPrimaryKeys(null, null, name);

        final List<String> primaryKeys = new ArrayList<String>();
        while (primaryKeysResultSet.next()) {
            final String primaryKey = primaryKeysResultSet.getString(Record.COLUMN_NAME);
            primaryKeys.add(primaryKey);
        }

        return primaryKeys;

    }

    public void delete() throws SQLException {
        deleteSelf();

        deleteParents();
    }

    private void deleteSelf() throws SQLException {
        final Query deleteQueryQuery = queryFactory.generateDeleteQuery(getPrimaryKeyColumn(), this.preassignedValues, this);
        deleteQueryQuery.execute();
    }

    private void deleteParents() throws SQLException {
        final ListIterator<Map.Entry<String, Record>> entryListIterator = new ArrayList<Map.Entry<String, Record>>(parentTables.entrySet()).listIterator(parentTables.size());
        while (entryListIterator.hasPrevious()) {
            final Map.Entry<String, Record> parentTableEntrySet = entryListIterator.previous();
            parentTableEntrySet.getValue().deleteSelf();
        }
    }

    private Column getPrimaryKeyColumn() {
        for (Map.Entry<String, Column> stringColumnEntry : columns.entrySet()) {
            final Column column = stringColumnEntry.getValue();
            if (column.isPrimaryKey()) return column;
        }

        throw new NoPrimaryKeyFoundException();

    }

    public boolean isPrimaryKey(final String columnName) throws SQLException {
        final List<String> primaryKeys = getPrimaryKeys();
        return primaryKeys.contains(columnName);
    }

    Map<String, ColumnTable> foreignKeyTableMap() throws SQLException {

        final ResultSet crossReference = connection.getMetaData().getCrossReference(null, null, null, null, null, tableName());

        final Map<String, ColumnTable> foreignKeys = new HashMap<String, ColumnTable>();
        while (crossReference.next()) {

            final String primaryKeyTableName = crossReference.getString(PRIMARY_KEY_TABLE_NAME);
            final String primaryKeyColName = crossReference.getString(PRIMARY_KEY_COLUMN_NAME);
            final String foreignKeyColumnName = crossReference.getString(FOREIGN_KEY_COLUMN_NAME);


            final Record primaryRecord = this.parentTables.get(primaryKeyTableName);

            foreignKeys.put(foreignKeyColumnName, new ColumnTable(primaryKeyColName, primaryRecord));
        }


        return foreignKeys;
    }
}