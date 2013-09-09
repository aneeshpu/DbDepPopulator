package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class QueryFactoryTest {

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


    @Test
    public void generates_insert_query() throws SQLException {
        final QueryFactory queryFactory = new QueryFactory();
        when(record.tableName()).thenReturn("payment");

        final ArrayList<String> primaryKeys = new ArrayList<String>();
        primaryKeys.add("id");

        when(record.getPrimaryKeys()).thenReturn(primaryKeys);
        final String insertQuery = queryFactory.generateInsertQuery(columns(), preassignedValues(), record);

        assertThat(insertQuery,is(equalTo("insert into \"payment\" (\"invoice_id\",\"amount\",\"status\") values (1,10,2) returning \"id\"")));
    }

    private Map<String, Map<String, Object>> preassignedValues() {
        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        /*final Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.put("invoice_id", 1);
        columnValues.put("status", 2);
        preassignedValues.put("payment", columnValues);*/
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
