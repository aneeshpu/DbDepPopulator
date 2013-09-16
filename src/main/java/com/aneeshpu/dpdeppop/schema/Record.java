package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.query.Query;
import com.aneeshpu.dpdeppop.query.QueryFactory;
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

    private final String tableName;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final QueryFactory queryFactory;
    private final Map<String, Column> columns;
    private final ColumnCreationStrategy columnCreationStrategy;
    private final Map<String, List<Tuple>> parentTableMetadata;

    private Map<String, Record> parentTables;
    private final Map<String, ColumnTable> foreignKeys;

    public Record(String tableName, final Map<String, Map<String, Object>> preassignedValues, final ColumnCreationStrategy columnCreationStrategy,
                  final QueryFactory queryFactory, final Map<String, List<Tuple>> parentTableMetadata) {
        this.tableName = tableName;
        this.preassignedValues = preassignedValues;
        this.queryFactory = queryFactory;
        this.columnCreationStrategy = columnCreationStrategy;
        this.parentTableMetadata = parentTableMetadata;

        columns = new HashMap<String, Column>();
        foreignKeys = new HashMap<String, ColumnTable>();
    }

    private Record initialize(final Map<String, Record> parentTables, final Connection connection) throws SQLException {

        try {
            this.parentTables = populateParents(parentTables, connection);
            final Map<String, Column> stringColumnHashMap = columnCreationStrategy.populateColumns(this, connection);
            columns.putAll(stringColumnHashMap);

            return this;

        } catch (SQLException e) {
            LOG.error(e);

            throw e;
        }
    }

    public String tableName() {
        return tableName;
    }

    public Column getColumn(final String columnName) {
        return columns.get(columnName);
    }

    private Map<String, Record> populateParents(final Map<String, Record> parentTables, final Connection connection) throws SQLException {

        populateParentsFromProvidedMetadata(parentTables, connection);

        populateParentsFromJDBCMetadata(parentTables, connection);

        return parentTables;

    }

    private void populateParentsFromJDBCMetadata(final Map<String, Record> parentTables, final Connection connection) throws SQLException {
        final ResultSet importedKeysResultSet = connection.getMetaData().getImportedKeys(null, null, tableName);

        while (importedKeysResultSet.next()) {
            final String primaryKeyTableName = importedKeysResultSet.getString(PRIMARY_KEY_TABLE_NAME);
            final String foreignKeyColumnName = importedKeysResultSet.getString(FOREIGN_KEY_COLUMN_NAME);
            final String primaryKeyColumnName = importedKeysResultSet.getString(PRIMARY_KEY_COLUMN_NAME);

            addParentTable(parentTables, connection, primaryKeyTableName, foreignKeyColumnName, primaryKeyColumnName);
        }
    }

    private void populateParentsFromProvidedMetadata(final Map<String, Record> parentTables, final Connection connection) throws SQLException {
        if (parentTableMetadata == null || !parentTableMetadata.containsKey(tableName)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("no parent meta data provided for table:" + tableName);
            }
            return;
        }

        final List<Tuple> parentTableColumn = parentTableMetadata.get(tableName());
        for (Tuple parentTableFromMetaData : parentTableColumn) {
            final String foreignKeyColumnName = parentTableFromMetaData.getForeignKeyColumnName();
            final String primaryKeyTableName = parentTableFromMetaData.getPrimaryKeyTableName();
            final String primaryKeyColumnName = parentTableFromMetaData.getPrimaryKeyColumnName();

            addParentTable(parentTables, connection, primaryKeyTableName, foreignKeyColumnName, primaryKeyColumnName);

        }
    }

    private void addParentTable(final Map<String, Record> parentTables, final Connection connection, final String primaryKeyTableName,
                                final String foreignKeyColumnName, final String primaryKeyColumnName) throws SQLException {

        final Record record = new RecordBuilder().withQueryFactory(connection).setName(primaryKeyTableName).setConnection(connection).withPreassignedValues
                (preassignedValues).withParentMetaData(parentTableMetadata).setColumnCreationStrategy(columnCreationStrategy).createRecord().initialize(parentTables,
                connection);

        //setting it up for use later. Might get rid of it altogether later.
        foreignKeys.put(foreignKeyColumnName, new ColumnTable(primaryKeyColumnName, parentTables.containsKey(primaryKeyTableName) ? parentTables.get(primaryKeyTableName) : record));

        if (parentTableIsPreassigned(foreignKeyColumnName) || parentTables.containsKey(primaryKeyTableName)) {
            if (LOG.isInfoEnabled()) {
                LOG.info(foreignKeyColumnName + " is either pre-assigned or " + primaryKeyTableName + " has already " +
                        "been initialized");
            }

            return;
        }

        parentTables.put(primaryKeyTableName, record);
    }

    private boolean parentTableIsPreassigned(final String foreignKeyColumnName) {
        final Map<String, Object> preassignedCols = preassignedValues.get(tableName);

        if (preassignedCols == null || preassignedCols.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("table " + tableName + " has no preassigned keys.");
            }

            return false;
        }

        return preassignedCols.containsKey(foreignKeyColumnName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Record record = (Record) o;

        if (!tableName.equals(record.tableName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }

    @Override
    public String toString() {
        return tableName;
    }

    public Map<String, Column> columns() {
        return columns;
    }

    public Map<String, Record> populate(final boolean onlyPopulateParentTables, final Connection connection) throws SQLException {

        initialize(new LinkedHashMap<String, Record>(), connection);

        final Map<String, Record> tables = new HashMap<String, Record>();
        insertDefaultValuesIntoParentTables(tables, connection);

        if (onlyPopulateParentTables) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Not populating table " + tableName + " as onlyPopulateParentTables = " +
                        onlyPopulateParentTables);
            }
            return tables;
        }

        insertDefaultValuesIntoCurrentTable(connection);

        tables.put(tableName, this);
        return tables;
    }

    private void insertDefaultValuesIntoParentTables(final Map<String, Record> tables, final Connection connection) throws SQLException {

        for (Map.Entry<String, Record> entry : parentTables.entrySet()) {

            final Record parentRecord = entry.getValue();
            parentRecord.insertDefaultValuesIntoCurrentTable(connection);
            tables.put(parentRecord.tableName, parentRecord);

            tables.putAll(parentTables);
        }
    }

    private String insertDefaultValuesIntoCurrentTable(final Connection connection) throws SQLException {

        final ResultSet generatedKeys = queryFactory.generateInsertQuery(columns, this.preassignedValues, this, connection).execute();
        setGeneratedValuesOnColumns(generatedKeys, getColumnsWithGeneratedValues());

        return queryFactory.generateInsertQuery(columns, this.preassignedValues, this, connection).toString();
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

    public List<String> getPrimaryKeys(final Connection connection) throws SQLException {
        final ResultSet primaryKeysResultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName);

        final List<String> primaryKeys = new ArrayList<String>();
        while (primaryKeysResultSet.next()) {
            final String primaryKey = primaryKeysResultSet.getString(Record.COLUMN_NAME);
            primaryKeys.add(primaryKey);
        }

        return primaryKeys;

    }

    public void delete(final Connection connection) throws SQLException {
        deleteSelf(connection);

        deleteParents(connection);
    }

    private void deleteSelf(final Connection connection) throws SQLException {
        final Query deleteQueryQuery = queryFactory.generateDeleteQuery(getPrimaryKeyColumn(), this.preassignedValues, this, connection);
        deleteQueryQuery.execute();
    }

    private void deleteParents(final Connection connection) throws SQLException {
        final ListIterator<Map.Entry<String, Record>> entryListIterator = new ArrayList<Map.Entry<String, Record>>(parentTables.entrySet()).listIterator(parentTables
                .size());

        while (entryListIterator.hasPrevious()) {
            final Map.Entry<String, Record> parentTableEntrySet = entryListIterator.previous();
            parentTableEntrySet.getValue().deleteSelf(connection);
        }
    }

    private Column getPrimaryKeyColumn() {
        for (Map.Entry<String, Column> stringColumnEntry : columns.entrySet()) {
            final Column column = stringColumnEntry.getValue();
            if (column.isPrimaryKey()) return column;
        }

        throw new NoPrimaryKeyFoundException();

    }

    boolean isPrimaryKey(final String columnName, final Connection connection) throws SQLException {
        final List<String> primaryKeys = getPrimaryKeys(connection);
        return primaryKeys.contains(columnName);
    }

    Map<String, ColumnTable> foreignKeyTableMap() throws SQLException {
        return foreignKeys;
    }
}