package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class InsertQueryTest {

    @Mock
    private Record record;

    @Mock
    private Column invoiceColumn;

    @Mock
    private Column amountColumn;

    @Mock
    private Column statusColumn;

    @Mock
    private NameValue amountColumnNameValue;

    @Mock
    private NameValue invoiceColumnNameValue;

    @Mock
    private NameValue statusColumnNameValue;

    @Mock
    private Column accountNameColumn;

    @Mock
    private NameValue accountTableNameColumnNameValue;


    @Test
    public void generates_insert_query() throws SQLException {
        when(record.tableName()).thenReturn("payment");
        final ArrayList<String> primaryKeys = new ArrayList<String>();
        primaryKeys.add("id");
        when(record.getPrimaryKeys()).thenReturn(primaryKeys);

        final Connection connection = new ConnectionFactory().getConnection();
        final Insert insertQuery = new Insert(columns(), preassignedValues(), record, connection);
        final String queryString = insertQuery.toString();

        assertThat(queryString, is(equalTo("insert into \"payment\" (\"invoice_id\",\"amount\",\"status\") values (1,10,2) returning \"id\"")));
    }

    @Test
    public void executes_insert_query() throws SQLException {

        when(record.tableName()).thenReturn("account");
        final ArrayList<String> primaryKeys = new ArrayList<String>();
        primaryKeys.add("id");
        when(record.getPrimaryKeys()).thenReturn(primaryKeys);

        final Connection connection = new ConnectionFactory().getConnection();
        connection.setAutoCommit(false);
        final Insert insertQuery = new Insert(accountColumns(), preassignedValues(), record, connection);

        final ResultSet resultSet = insertQuery.execute();

        if (!resultSet.next()) {
            connection.rollback();
            fail("nothing got inserted");
        }

        final int id = resultSet.getInt("id");
        System.out.println("generated id:" + id);
        final Statement statement = connection.createStatement();
        statement.execute("select count(*) from account where id = " + id);

        final ResultSet selectResultSet = statement.getResultSet();
        if (!selectResultSet.next()) {
            connection.rollback();
            fail("Failed to select the inserted rows");
        }

        final int numberOfRowsInserted = selectResultSet.getInt(1);
        assertThat(numberOfRowsInserted, is(equalTo(1)));

        connection.rollback();
    }

    private Map<String, Column> accountColumns() {
        final LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>();
        columns.put("name", accountNameColumn);

        when(accountNameColumn.nameValue(any(Map.class))).thenReturn(accountTableNameColumnNameValue);
        when(accountTableNameColumnNameValue.formattedName()).thenReturn("\"name\",");
        when(accountTableNameColumnNameValue.formattedValue()).thenReturn("'dbdeppop',");
        return columns;
    }

    private Map<String, Map<String, Object>> preassignedValues() {
        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        return preassignedValues;
    }

    private Map<String, Column> columns() {
        final Map<String, Column> columns = new LinkedHashMap<String, Column>();
        columns.put("invoice_id", invoiceColumn);
        columns.put("amount", amountColumn);
        columns.put("status", statusColumn);

        when(invoiceColumnNameValue.formattedName()).thenReturn("\"invoice_id\",");
        when(invoiceColumnNameValue.formattedValue()).thenReturn("1,");
        when(invoiceColumn.nameValue(any(Map.class))).thenReturn(invoiceColumnNameValue);

        when(amountColumnNameValue.formattedName()).thenReturn("\"amount\",");
        when(amountColumnNameValue.formattedValue()).thenReturn("10,");
        when(amountColumn.nameValue(any(Map.class))).thenReturn(amountColumnNameValue);

        when(statusColumnNameValue.formattedName()).thenReturn("\"status\",");
        when(statusColumnNameValue.formattedValue()).thenReturn("2,");
        when(statusColumn.nameValue(any(Map.class))).thenReturn(statusColumnNameValue);

        return columns;
    }
}
