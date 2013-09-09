package com.aneeshpu.dpdeppop.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SerialDataType implements DataType<Integer>{
    private final String dataType;

    public SerialDataType(final String dataType) {

        this.dataType = dataType;
    }

    @Override
    public Integer generateDefaultValue() {
        return null;
    }

    @Override
    public Integer getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {
        return generatedKeys.getInt(columnName);
    }
}
