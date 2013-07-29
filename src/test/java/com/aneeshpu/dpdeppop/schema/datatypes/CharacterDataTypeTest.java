package com.aneeshpu.dpdeppop.schema.datatypes;

import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CharacterDataTypeTest {

    private final Connection connection = new ConnectionFactory().invoke();

    @Test
    public void generates_a_default_value(){

        final CharacterDataType characterDataType = new CharacterDataType("character");
        final String defaultValue = characterDataType.generateDefaultValue();
        assertNotNull(defaultValue);
        assertThat(defaultValue, is(equalTo("'c'")));
    }

    @Test
    public void retrieves_generated_string_value_from_result_set() throws SQLException {

        final String insertQuery = "insert into account (name) values ('c')";

        final Statement statement = connection.createStatement();
        statement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeysResultSet = statement.getGeneratedKeys();
        generatedKeysResultSet.next();

        final CharacterDataType character = new CharacterDataType("character");

        assertThat(character.getGeneratedValue(generatedKeysResultSet, "name"), is(equalTo("c")));

    }
}
