package com.aneeshpu.dpdeppop.schema.datatypes;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 7:21 AM
 * To change this template use File | Settings | File Templates.
 */
class CharacterDataType implements DataType<String>{
    private final String dataType;

    public CharacterDataType(final String dataType) {

        this.dataType = dataType;
    }

    @Override
    public String toString() {
        //TODO:A better way of generating random Strings. It is set to one character now, so that I don't
        //have to worry about column lengths
        return "c";
    }

    @Override
    public String generateDefaultValue() {
        return String.format("'%s'",toString());
    }

    @Override
    public String getGeneratedValue(final ResultSet generatedKeys, final String columnName) throws SQLException {

        return generatedKeys.getString(columnName);
    }
}
