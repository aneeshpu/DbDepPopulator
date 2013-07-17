package com.aneeshpu.dpdeppop.schema;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 16/7/13
 * Time: 2:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataType {
    private final String name;

    public DataType(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DataType dataType = (DataType) o;

        if (name != null ? !name.equals(dataType.name) : dataType.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
