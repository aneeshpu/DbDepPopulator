package com.aneeshpu.dpdeppop.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;

class CharacterDataType implements DataType<String>{
    public static final String DEFAULT_VALUE = "t";
    private final String dataType;

    public CharacterDataType(final String dataType) {

        this.dataType = dataType;
    }

/*
    @Override
    public String toString() {
        //TODO:A better way of generating random Strings. It is set to one character now, so that I don't
        //have to worry about column lengths
        return DEFAULT_VALUE;
    }
*/

    @Override
    public String generateDefaultValue() {
        return String.format("'%s'",DEFAULT_VALUE);
    }

    @Override
    public String getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {

        return generatedKeys.getString(columnName);
    }
}
