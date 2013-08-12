package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumnTest {

    private Connection connection = new ConnectionFactory().invoke();

    @Mock
    private Table table;


    @Test
    public void considers_two_columns_from_the_same_table_with_same_names_are_equal(){

        final Column idColumn = Column.buildColumn().withName("id").withTable(new Table("payment", connection, new HashMap<String, Map<String, Object>>())).create();
        final Column anotherIdColumn = Column.buildColumn().withName("id").withTable(new Table("payment", connection, new HashMap<String, Map<String, Object>>())).create();

        assertThat(idColumn, is(equalTo(anotherIdColumn)));
    }

    @Test
    public void prints_to_string_with_column_name(){
        assertThat(Column.buildColumn().withName("id").create().toString(), is(equalTo("id")));
    }

    @Test
    public void generates_a_name_value_pair_to_be_inserted_into_sql_query(){

        final DataType<Float> dataType = mock(DataType.class);
        when(dataType.generateDefaultValue()).thenReturn(10f);

        final Column amount = Column.buildColumn()
                .withName("amount")
                .withDataType(dataType)
                .withTable(table)
                .create();
        final NameValue nameValue = amount.nameValue(new java.util.HashMap<String, java.util.Map<String, Object>>());

        assertThat(nameValue, is(equalTo(new NameValue("amount", 10f))));

        verify(dataType).generateDefaultValue();
    }

    @Test
    public void a_foreign_key_column_uses_the_default_value_generated_by_the_referenced_column(){

        final DataType<Float> dataType = mock(DataType.class);
        when(dataType.generateDefaultValue()).thenReturn(321f);

        final Column idColumnInInvoiceTable = mock(Column.class);
        final int primaryKeyInInvoiceTable = 123;

        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        when(idColumnInInvoiceTable.nameValue(preassignedValues)).thenReturn(new NameValue("id", primaryKeyInInvoiceTable));

        final Column invoiceIdColumn = Column.buildColumn()
                                    .withName("invoice_id")
                                    .withReferencingColumn(idColumnInInvoiceTable)
                                    .withDataType(dataType)
                                    .withTable(table)
                                    .create();
        assertThat(invoiceIdColumn.nameValue(preassignedValues), is(equalTo(new NameValue("invoice_id", primaryKeyInInvoiceTable))));

        verify(dataType, never()).generateDefaultValue();
        verify(idColumnInInvoiceTable).nameValue(preassignedValues);
    }

    @Test
    public void creates_a_formatted_query_string(){

        final DataType<String> dataType = mock(DataType.class);
        when(dataType.generateDefaultValue()).thenReturn("'c'");

        final Column amount = Column.buildColumn()
                .withName("amount")
                .withDataType(dataType)
                .withTable(table)
                .create();
        final NameValue nameValue = amount.nameValue(new java.util.HashMap<String, java.util.Map<String, Object>>());

        assertThat(nameValue.formattedValue(), is(equalTo("'c',")));

        verify(dataType).generateDefaultValue();

    }

    @Test
    public void uses_a_preassigned_value_if_passed_in(){
        final DataType<Float> dataType = mock(DataType.class);
        when(dataType.generateDefaultValue()).thenReturn(10f);

        final Table table = mock(Table.class);
        when(table.name()).thenReturn("payment");

        final Column amount = Column.buildColumn().withTable(table).withName("status").withDataType(dataType).create();
        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("status", 2);

        preassignedValues.put("payment", values);

        final NameValue nameValue = amount.nameValue(preassignedValues);

        assertThat(nameValue, is(equalTo(new NameValue("status", 2))));

        verify(table, atLeastOnce()).name();
        verify(dataType, never()).generateDefaultValue();

    }
}
