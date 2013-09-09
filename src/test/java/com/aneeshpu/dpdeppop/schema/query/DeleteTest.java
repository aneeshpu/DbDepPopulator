package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteTest {

    @Mock
    private Record record;

    @Mock
    private Column column;

    @Mock
    private NameValue nameValue;

    @Test
    public void generates_a_delete_query_string() {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();

        when(column.nameValue(any(Map.class))).thenReturn(nameValue);
        when(nameValue.formattedNameWithoutTrailingComma()).thenReturn("\"id\"");
        when(nameValue.formattedValueWithoutTrailingComma()).thenReturn("10");

        when(record.tableName()).thenReturn("account");
        final Delete deleteQuery = new Delete(column, preassignedValues, record);

        assertThat(deleteQuery.toString(), is(equalTo("delete from \"account\" where \"id\"=10")));

        verify(column).nameValue(any(Map.class));
        verify(nameValue).formattedNameWithoutTrailingComma();
        verify(nameValue).formattedValueWithoutTrailingComma();

        verify(record).tableName();
    }
}