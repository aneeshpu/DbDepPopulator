package com.aneeshpu.dpdeppop.schema.datatypes;

import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.aneeshpu.dpdeppop.schema.Matchers.aNumber;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SerialDataTypeTest {

    private Connection connection = new ConnectionFactory().invoke();


    @Test
    public void retrieves_generated_int_value_from_result_set() throws SQLException {

        final Statement statement = connection.createStatement();
        connection.setAutoCommit(false);

        final String accountInsertQuery = "insert into account (name) values ('aneesh')";
        statement.executeUpdate(accountInsertQuery, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();

        final SerialDataType aSerialType = new SerialDataType("int");
        assertThat(aSerialType.getGeneratedValue(generatedKeys, "id"), is(aNumber()));
    }
}