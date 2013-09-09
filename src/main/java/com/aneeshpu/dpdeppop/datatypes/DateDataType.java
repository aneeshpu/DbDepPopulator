package com.aneeshpu.dpdeppop.datatypes;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateDataType implements DataType<String> {
    @Override
    public String generateDefaultValue() {
        return String.format("'%s'",new Date(new java.util.Date().getTime()));
    }

    @Override
    public String getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {
        return generatedKeys.getString(columnName);
    }
}
