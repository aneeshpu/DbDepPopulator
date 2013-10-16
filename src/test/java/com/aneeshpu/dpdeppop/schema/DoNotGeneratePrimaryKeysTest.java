package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.datatypes.DataTypeFactory;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DoNotGeneratePrimaryKeysTest {

    public static final Connection connection = new ConnectionFactory().getConnection();

    @Test
    public void does_not_generate_values_for_primary_keys() throws SQLException {

        final DoNotGeneratePrimaryKeys doNotGeneratePrimaryKeys = new DoNotGeneratePrimaryKeys(connection, new DataTypeFactory());

        final Record accountRecord = new RecordBuilder(connection).withName("account").withPreassignedValues(Collections.<String, Map<String, Object>>emptyMap()).setColumnCreationStrategy(doNotGeneratePrimaryKeys).createRecord();
        final Map<String,Column> populatedColumns = doNotGeneratePrimaryKeys.populateColumns(accountRecord, connection);

        final Column id = populatedColumns.get("id");
        assertThat(id, is(equalTo(Column.buildColumn().withTable(accountRecord).withName("id").create())));
        assertThat(id.isAutoIncrement(), is(true));
    }
}
