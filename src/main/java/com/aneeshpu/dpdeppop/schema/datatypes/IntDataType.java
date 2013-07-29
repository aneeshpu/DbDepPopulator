package com.aneeshpu.dpdeppop.schema.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

class IntDataType implements DataType<Integer> {
    private final String name;

    public IntDataType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.valueOf(new Random().nextInt());
    }

    @Override
    public Integer generateDefaultValue() {
        return Integer.parseInt(toString());
    }

    @Override
    public Integer getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {

        return generatedKeys.getInt(columnName);
    }
}
