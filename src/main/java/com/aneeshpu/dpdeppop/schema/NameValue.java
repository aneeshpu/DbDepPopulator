package com.aneeshpu.dpdeppop.schema;


public class NameValue {
    private final String columnName;
    private final Object columnValue;

    NameValue(final String columnName, final Object columnValue) {
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NameValue other = (NameValue) o;

        if (columnName != null ? !columnName.equals(other.columnName) : other.columnName != null) return false;
        if (columnValue != null ? !columnValue.equals(other.columnValue) : other.columnValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + (columnValue != null ? columnValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s=%s", columnName, columnValue);
    }

    public Object value() {
        return columnValue;
    }

    public String name() {
        return String.format("%s,", columnName);
    }

    public static NameValue createAutoIncrement() {
        return new NameValue("", "");
    }

    public String formattedValue() {
        return String.format("%s,", columnValue);
    }

    public String formattedValueWithoutTrailingComma() {
        final String formattedValue = formattedValue();
        return formattedValue.substring(0, formattedValue.length() - 1);
    }

    public String formattedName() {
        return String.format("\"%s\",", columnName);
    }

    public String formattedNameWithoutTrailingComma() {
        final String formattedName = formattedName();
        return formattedName.substring(0, formattedName.length() - 1);
    }

    public boolean isAssigned() {
        return columnName != null && columnValue != null;
    }
}
