package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.datatypes.DataType;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Column {
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String COLUMN_SIZE = "COLUMN_SIZE";
    public static final String IS_NULLABLE = "IS_NULLABLE";
    public static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private String name;
    private Record record;
    private DataType dataType;
    private double columnSize;
    private Record referencingRecord;
    private Column referencingColumn;
    private YesNo autoIncrement = new YesNo("NO");

    private YesNo isNullable = new YesNo("NO");
    private NameValue nameValue;

    private static final Logger LOG = Logger.getLogger(Column.class);
    private boolean isPrimaryKey;

    private Column() {
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Column column = (Column) o;

        if (name != null ? !name.equals(column.name) : column.name != null) return false;
        if (record != null ? !record.equals(column.record) : column.record != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (record != null ? record.hashCode() : 0);
        return result;
    }

    public DataType getDataType() {
        return dataType;
    }

    public static ColumnBuilder buildColumn() {
        return new ColumnBuilder();
    }


    private void setName(final String columnName) {
        this.name = columnName;
    }

    private void setDataType(final DataType dataType1) {
        this.dataType = dataType1;

    }

    private void setSize(final double columnSize) {
        this.columnSize = columnSize;
    }

    private void setNullable(final YesNo yesNo) {
        this.isNullable = yesNo;
    }

    private void setAutoIncrement(final YesNo yesNo) {
        this.autoIncrement = yesNo;
    }

    private void setRecord(final Record record) {
        this.record = record;
    }

    public Record getRecord() {
        return record;
    }

    public void setReferencingRecord(final Record referencingRecord) {
        this.referencingRecord = referencingRecord;
    }

    public Record getReferencingRecord() {
        return referencingRecord;
    }

    public void setReferencingColumn(final Column referencingColumn) {
        this.referencingColumn = referencingColumn;
    }

    public Column getReferencingColumn() {
        return referencingColumn;
    }

    public NameValue nameValue(final Map<String, Map<String, Object>> preassignedValues) {
        if (nameValue != null) {
            return nameValue;

        } else if (isPreAssigned(preassignedValues)) {
            nameValue = getPreassignedNameValue(preassignedValues);

        } else if (autoIncrement.isTrue()) {
            nameValue = NameValue.createAutoIncrement();

        } else {nameValue = isForeignKey() ? new NameValue(name, referencingColumn.nameValue(preassignedValues).value()) : new NameValue(name, dataType.generateDefaultValue());
        }
        return nameValue;
    }

    private NameValue getPreassignedNameValue(final Map<String, Map<String, Object>> preassignedValues) {
        final Object preassignedValue = preassignedValues.get(record.name()).get(name);

        return new NameValue(name, preassignedValue);
    }

    private boolean isPreAssigned(final Map<String, Map<String, Object>> preassignedValues) {
        if (!preassignedValues.containsKey(record.name())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No preassigned values for " + record.name());
            }
            return false;
        }

        return preassignedValues.get(record.name()).containsKey(name);
    }

    private boolean isForeignKey() {
        return referencingColumn != null;
    }

    public boolean isAutoIncrement() {
        return autoIncrement.isTrue();
    }

    public void populateGeneratedValue(final ResultSet generatedKeys) throws SQLException {

        final Object generatedValue = dataType.getGeneratedValue(generatedKeys, name);
        this.nameValue = new NameValue(name, generatedValue);
    }

    public Object value() {
        return nameValue == null ? null : nameValue.value();
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isAssigned() {
        return nameValue != null && nameValue.isAssigned();
    }

    public static class ColumnBuilder {

        private final Column column;

        ColumnBuilder() {
            column = new Column();
        }

        public ColumnBuilder withName(final String columnName) {
            column.setName(columnName);
            return this;
        }

        public Column create() {
            return column;
        }

        public ColumnBuilder withDataType(final DataType dataType) {
            column.setDataType(dataType);
            return this;
        }

        public ColumnBuilder withSize(final Double columnSize) {
            column.setSize(columnSize);
            return this;
        }

        public ColumnBuilder withIsNullable(final String nullable) {
            column.setNullable(new YesNo(nullable));
            return this;
        }

        public ColumnBuilder withIsAutoIncrement(final String autoIncrement) {
            column.setAutoIncrement(new YesNo(autoIncrement));
            return this;
        }

        public ColumnBuilder withTable(final Record record) {
            column.setRecord(record);
            return this;
        }

        public ColumnBuilder withReferencingTable(final Record referencingRecord) {
            column.setReferencingRecord(referencingRecord);
            return this;
        }

        public ColumnBuilder withReferencingColumn(final Column referencingColumn) {
            column.setReferencingColumn(referencingColumn);
            return this;
        }

        public ColumnBuilder asPrimaryKey(final boolean isPrimaryKey) {
            column.isPrimaryKey = isPrimaryKey;
            return this;
        }
    }
}
