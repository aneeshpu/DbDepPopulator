package com.aneeshpu.dpdeppop.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataType<T> {

    T generateDefaultValue();

    T getGeneratedValue(ResultSet generatedKeys, final String columnName) throws SQLException;
}
