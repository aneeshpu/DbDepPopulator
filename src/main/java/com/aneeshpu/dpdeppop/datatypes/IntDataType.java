package com.aneeshpu.dpdeppop.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

class IntDataType implements DataType<Integer> {
    public static final int MAX_VALUE = 7;
    public static final int MIN_VALUE = 0;
    private final String name;
    private final Random random;

    public IntDataType(final String name) {
        this.name = name;
        random = new Random();
    }

    @Override
    public String toString() {
        return String.valueOf(generateDefaultValue());
    }

    @Override
    public Integer generateDefaultValue() {
        final int numberLessThanOrEqualToMaxValue = random.nextInt(MAX_VALUE) + 1;
        final int range = numberLessThanOrEqualToMaxValue - MIN_VALUE;
        final int numberWithinRange = range + MIN_VALUE;
        return numberWithinRange;
    }

    @Override
    public Integer getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {
        return generatedKeys.getInt(columnName);
    }
}
