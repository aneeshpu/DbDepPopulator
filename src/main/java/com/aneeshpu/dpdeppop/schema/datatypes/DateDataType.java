package com.aneeshpu.dpdeppop.schema.datatypes;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateDataType implements DataType<Date> {
    @Override
    public Date generateDefaultValue() {
        return new Date(new java.util.Date().getTime());
    }

    @Override
    public Date getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {
        return generatedKeys.getDate(columnName);
    }
}
