package com.aneeshpu.dpdeppop.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteQueryTest {

    @Mock
    private Record record;

    @Mock
    private Column column;

    @Mock
    private NameValue nameValue;

    @Test
    public void generates_a_delete_query_string() throws SQLException {


        Connection connection = null;
        try {
            connection = new ConnectionFactory().getConnection();
            connection.setAutoCommit(false);
            final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();

            when(column.nameValue(any(Map.class))).thenReturn(nameValue);
            when(nameValue.formattedNameWithoutTrailingComma()).thenReturn("\"id\"");
            when(nameValue.formattedValueWithoutTrailingComma()).thenReturn("10");

            when(record.tableName()).thenReturn("account");
            final DeleteQuery deleteQueryQuery = new DeleteQuery(column, preassignedValues, record, connection);

            assertThat(deleteQueryQuery.toString(), is(equalTo("delete from \"account\" where \"id\"=10")));

            verify(column).nameValue(any(Map.class));
            verify(nameValue).formattedNameWithoutTrailingComma();
            verify(nameValue).formattedValueWithoutTrailingComma();

            verify(record).tableName();
        } finally {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        }
    }

    @Test
    public void deletes_a_given_record() throws SQLException {

        Connection connection = null;
        try {
            connection = new ConnectionFactory().getConnection();
            connection.setAutoCommit(false);

            final PreparedStatement statement = connection.prepareStatement("insert into account (name) values ('datapopulator')", Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();

            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                fail("Nothing got inserted into the account table");
            }

            final int generatedAccountId = generatedKeys.getInt("id");
            System.out.println("generated account id to be deleted:" + generatedAccountId);

            final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();

            when(column.nameValue(any(Map.class))).thenReturn(nameValue);
            when(nameValue.formattedNameWithoutTrailingComma()).thenReturn("\"id\"");
            when(nameValue.formattedValueWithoutTrailingComma()).thenReturn(String.valueOf(generatedAccountId));

            when(record.tableName()).thenReturn("account");
            final DeleteQuery deleteQueryQuery = new DeleteQuery(column, preassignedValues, record, connection);

            deleteQueryQuery.execute();

            final Statement regularStatement = connection.createStatement();
            regularStatement.execute("Select count(*) from account where id = " + generatedAccountId);

            final ResultSet resultSet = regularStatement.getResultSet();
            if (!resultSet.next()) {
                fail("Failed to select account records");
            }

            final int numberOfRecordsRemaining = resultSet.getInt(1);
            System.out.println(numberOfRecordsRemaining);

            assertThat(numberOfRecordsRemaining, is(equalTo(0)));

        } finally {

            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        }

    }
}