package com.aneeshpu.dpdeppop.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class FloatDataType implements DataType<Float>{

    private final String dataType;

    public FloatDataType(final String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return String.valueOf(generateDefaultValue());
    }

    @Override
    public Float generateDefaultValue() {
        return new Random().nextFloat();
    }

    @Override
    public Float getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {
        return generatedKeys.getFloat(columnName);
    }
}
