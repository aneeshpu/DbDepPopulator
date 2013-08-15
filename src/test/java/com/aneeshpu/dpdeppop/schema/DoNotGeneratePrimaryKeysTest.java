package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.utils.DBDepPopUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DoNotGeneratePrimaryKeysTest {

    public static final Connection CONNECTION = new ConnectionFactory().invoke();

    @Test
    public void does_not_generate_values_for_primary_keys() throws SQLException {

        final DoNotGeneratePrimaryKeys doNotGeneratePrimaryKeys = new DoNotGeneratePrimaryKeys(CONNECTION);

        final Table accountTable = new Table("account", CONNECTION, Collections.<String, Map<String, Object>>emptyMap(), doNotGeneratePrimaryKeys);
        final Map<String,Column> populatedColumns = doNotGeneratePrimaryKeys.populateColumns(accountTable, Collections.<String, Table>emptyMap());

        final Column id = populatedColumns.get("id");
        assertThat(id, is(equalTo(Column.buildColumn().withTable(accountTable).withName("id").create())));
        assertThat(id.isAutoIncrement(), is(true));
    }

    @Test
    public void test_primary_keys() throws SQLException {

        final ResultSet accountResultSet = CONNECTION.getMetaData().getPrimaryKeys(null, null, "account");

        DBDepPopUtil.printCols(accountResultSet.getMetaData(), "accountPrimaryKeys");
    }
}
