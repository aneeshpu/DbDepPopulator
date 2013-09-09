package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DoNotGeneratePrimaryKeysTest {

    public static final Connection CONNECTION = new ConnectionFactory().invoke();

    @Test
    public void does_not_generate_values_for_primary_keys() throws SQLException {

        final DoNotGeneratePrimaryKeys doNotGeneratePrimaryKeys = new DoNotGeneratePrimaryKeys(CONNECTION);

        final Record accountRecord = new RecordBuilder().setName("account").setConnection(CONNECTION).setPreassignedValues(Collections.<String, Map<String, Object>>emptyMap()).setColumnCreationStrategy(doNotGeneratePrimaryKeys).createRecord();
        final Map<String,Column> populatedColumns = doNotGeneratePrimaryKeys.populateColumns(accountRecord);

        final Column id = populatedColumns.get("id");
        assertThat(id, is(equalTo(Column.buildColumn().withTable(accountRecord).withName("id").create())));
        assertThat(id.isAutoIncrement(), is(true));
    }
}
