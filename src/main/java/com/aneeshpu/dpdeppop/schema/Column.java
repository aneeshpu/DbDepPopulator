package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Column {
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String COLUMN_SIZE = "COLUMN_SIZE";
    public static final String IS_NULLABLE = "IS_NULLABLE";
    public static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private String name;
    private Table table;
    private DataType dataType;
    private double columnSize;
    private Table referencingTable;
    private Column referencingColumn;

    private YesNo autoIncrement = new YesNo("NO");
    private YesNo isNullable = new YesNo("NO");
    private NameValue nameValue;

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
        if (table != null ? !table.equals(column.table) : column.table != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
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

    private void setTable(final Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setReferencingTable(final Table referencingTable) {
        this.referencingTable = referencingTable;
    }

    public Table getReferencingTable() {
        return referencingTable;
    }

    public void setReferencingColumn(final Column referencingColumn) {
        this.referencingColumn = referencingColumn;
    }

    public Column getReferencingColumn() {
        return referencingColumn;
    }

    public NameValue nameValue() {
        if (nameValue != null) {
            return nameValue;
        }

        if (autoIncrement.isTrue()) {
            nameValue = NameValue.createAutoIncrement();
        }

        nameValue = isForeignKey() ? new NameValue(name, referencingColumn.nameValue().value()) : new NameValue(name, dataType.generateDefaultValue());
        return nameValue;
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

    static class ColumnBuilder {

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

        public ColumnBuilder withTable(final Table table) {
            column.setTable(table);
            return this;
        }

        public ColumnBuilder withReferencingTable(final Table referencingTable) {
            column.setReferencingTable(referencingTable);
            return this;
        }

        public ColumnBuilder withReferencingColumn(final Column referencingColumn) {
            column.setReferencingColumn(referencingColumn);
            return this;
        }
    }
}
