package com.aneeshpu.dpdeppop.schema.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BoolDataType implements DataType<Boolean> {
    @Override
    public Boolean generateDefaultValue() {
        return false;
    }

    @Override
    public Boolean getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {
        return generatedKeys.getBoolean(columnName);
    }
}
