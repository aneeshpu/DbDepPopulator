package com.aneeshpu.dpdeppop.query;

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
        final Connection connection = new ConnectionFactory().getConnection();
        when(record.getPrimaryKeys(connection)).thenReturn(primaryKeys);

        final InsertQuery insertQueryQuery = new InsertQuery(columns(), preassignedValues(), record, connection);
        final String queryString = insertQueryQuery.toString();

        assertThat(queryString, is(equalTo("insert into \"payment\" (\"invoice_id\",\"amount\",\"status\") values (1,10,2) returning \"id\"")));
    }

    @Test
    public void executes_insert_query() throws SQLException {

        when(record.tableName()).thenReturn("account");
        final ArrayList<String> primaryKeys = new ArrayList<String>();
        primaryKeys.add("id");

        final Connection connection = new ConnectionFactory().getConnection();
        when(record.getPrimaryKeys(connection)).thenReturn(primaryKeys);
        connection.setAutoCommit(false);
        final InsertQuery insertQueryQuery = new InsertQuery(accountColumns(), preassignedValues(), record, connection);

        final ResultSet resultSet = insertQueryQuery.execute();

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

        when(accountNameColumn.formattedName(any(Map.class))).thenReturn("\"name\",");
        when(accountNameColumn.formattedValue(any(Map.class))).thenReturn("'dbdeppop',");
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

        when(invoiceColumn.formattedName(any(Map.class))).thenReturn("\"invoice_id\",");
        when(invoiceColumn.formattedValue(any(Map.class))).thenReturn("1,");

        when(amountColumn.formattedName(any(Map.class))).thenReturn("\"amount\",");
        when(amountColumn.formattedValue(any(Map.class))).thenReturn("10,");

        when(statusColumn.formattedName(any(Map.class))).thenReturn("\"status\",");
        when(statusColumn.formattedValue(any(Map.class))).thenReturn("2,");

        return columns;
    }
}
