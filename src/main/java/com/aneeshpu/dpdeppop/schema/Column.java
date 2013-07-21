package com.aneeshpu.dpdeppop.schema;

public class Column {
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String COLUMN_SIZE = "COLUMN_SIZE";
    public static final String IS_NULLABLE = "IS_NULLABLE";
    public static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private String name;
    private Table table;
    private DataType dataType;
    private double columnSize;
    private YesNo yesNo;
    private Table referencingTable;
    private Column referencingColumn;

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

    private void setDataType(final String dataType) {
        this.dataType = new DataType(dataType);

    }

    private void setSize(final double columnSize) {
        this.columnSize = columnSize;
    }

    private void setNullable(final YesNo yesNo) {
        this.yesNo = yesNo;
    }

    private void setAutoIncrement(final YesNo yesNo) {

        this.yesNo = yesNo;
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

        public ColumnBuilder withDataType(final String dataType) {
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
