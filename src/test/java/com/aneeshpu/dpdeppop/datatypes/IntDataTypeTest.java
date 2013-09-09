package com.aneeshpu.dpdeppop.datatypes;

import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.aneeshpu.dpdeppop.schema.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class IntDataTypeTest {

    private Connection connection = new ConnectionFactory().getConnection();

    @Test
    public void generated_value_is_less_than_7() {

        final IntDataType anInt = new IntDataType("int");
        final Integer generatedInt = anInt.generateDefaultValue();
        assertNotNull(generatedInt);
        System.out.println(generatedInt);
        assertThat(generatedInt, is(lessThanOrEqualTo(IntDataType.MAX_VALUE)));
    }

    @Test
    public void generated_value_is_greaer_than_0() {

        final IntDataType anInt = new IntDataType("int");
        final Integer generatedInt = anInt.generateDefaultValue();
        assertNotNull(generatedInt);
        System.out.println(generatedInt);
        assertThat(generatedInt, is(greaterThan(0)));
    }

    @Test
    public void retrieves_generated_int_value_from_result_set() throws SQLException {

        final Statement statement = connection.createStatement();
        connection.setAutoCommit(false);

        final String accountInsertQuery = "insert into account (name) values ('aneesh')";
        statement.executeUpdate(accountInsertQuery, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();

        final IntDataType anInt = new IntDataType("int");
        assertThat(anInt.getGeneratedValue(generatedKeys, "id"), is(aNumber()));
    }
}