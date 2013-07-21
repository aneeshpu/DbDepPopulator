package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.datatypes.DataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumnTest {

    private Connection connection = new ConnectionFactory().invoke();


    @Test
    public void considers_two_columns_from_the_same_table_with_same_names_are_equal(){

        final Column idColumn = Column.buildColumn().withName("id").withTable(new Table("payment", connection)).create();
        final Column anotherIdColumn = Column.buildColumn().withName("id").withTable(new Table("payment", connection)).create();

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

        final Column amount = Column.buildColumn().withName("amount").withDataType(dataType).create();
        final NameValue nameValue = amount.nameValue();

        assertThat(nameValue, is(equalTo(new NameValue("amount", 10f))));

        verify(dataType).generateDefaultValue();
    }

    @Test
    public void a_foreign_key_column_uses_the_default_value_generated_by_the_referenced_column(){

        final DataType<Float> dataType = mock(DataType.class);
        when(dataType.generateDefaultValue()).thenReturn(321f);

        final Column idColumnInInvoiceTable = mock(Column.class);
        final int primaryKeyInInvoiceTable = 123;

        when(idColumnInInvoiceTable.nameValue()).thenReturn(new NameValue("id", primaryKeyInInvoiceTable));

        final Column invoiceIdColumn = Column.buildColumn().withName("invoice_id").withReferencingColumn(idColumnInInvoiceTable).withDataType(dataType).create();
        assertThat(invoiceIdColumn.nameValue(), is(equalTo(new NameValue("invoice_id", primaryKeyInInvoiceTable))));

        verify(dataType, never()).generateDefaultValue();
        verify(idColumnInInvoiceTable).nameValue();
    }
}
