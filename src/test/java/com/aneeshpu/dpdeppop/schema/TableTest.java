package com.aneeshpu.dpdeppop.schema;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aneeshpu.dpdeppop.schema.Matchers.*;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TableTest {

    private final Connection connection = new ConnectionFactory().invoke();
    private Table paymentTable;

    @Before
    public void setup() throws SQLException {
        paymentTable = new Table("payment", connection, new HashMap<String, Map<String, Object>>(), new AutoIncrementBasedCreation(connection));
    }

    @Test
    public void populates_columns() throws SQLException {
        paymentTable.populate(false);
        assertNotNull(paymentTable.columns().get("id"));
    }

    @Test
    public void foreign_keys_are_populated_with_their_referencing_tables() throws SQLException {
        paymentTable.populate(false);
        final Column invoiceId = paymentTable.columns().get("invoice_id");
        assertThat(invoiceId.getReferencingColumn(), is(equalTo(Column.buildColumn().withTable(new Table("invoice", connection, new HashMap<String, Map<String, Object>>(), new AutoIncrementBasedCreation(connection))).withName("id").create())));
    }

    @Test
    public void creates_a_string_representation_of_table() {

        assertThat(new Table("payment", null, new HashMap<String, Map<String, Object>>(), new AutoIncrementBasedCreation(connection)).toString(), is(equalTo("payment")));
    }

    @Test
    public void generates_an_insert_query() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Table accountTable = new Table("account", connection, preassignedValues, new AutoIncrementBasedCreation(connection));

        final Map<String, Table> tables = accountTable.populate(false);
        final Table generatedAccountTable = tables.get("account");
        System.out.println(generatedAccountTable);

        assertThat(generatedAccountTable.getColumn("id").value(), is(aNumber()));
    }

    @Test
    public void generates_insert_sql_for_parent_tables_and_itself() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", "2");

        preassignedValues.put("payment", values);

        Table paymentTable = new Table("payment", connection, preassignedValues, new AutoIncrementBasedCreation(connection));

        final boolean onlyPopulateParentTables = false;
        final Map<String, Table> generatedTables = paymentTable.populate(onlyPopulateParentTables);

        final Table account = generatedTables.get("account");
        assertThat(account.getColumn("name").value(), is(aString()));

        final Table invoice = generatedTables.get("invoice");
        assertThat(invoice.getColumn("amount").value(), is(aNumber()));
        assertThat(invoice.getColumn("account_id").value(), is(aNumber()));

        final Table payment = generatedTables.get("payment");
        assertThat(payment.getColumn("amount").value(), is(aNumber()));
        assertThat(Integer.parseInt(String.valueOf(payment.getColumn("status").value())), is(equalTo(2)));
        assertThat(payment.getColumn("invoice_id").value(), is(aNumber()));
    }

    @Test
    public void generates_insert_sql_for_parent_and_itself_with_primary_key_based_col_creation_strategy() throws SQLException {
        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", "2");

        preassignedValues.put("payment", values);

        Table paymentTable = new Table("payment", connection, preassignedValues, new DoNotGeneratePrimaryKeys(connection));

        final boolean onlyPopulateParentTables = false;
        final Map<String, Table> generatedTables = paymentTable.populate(onlyPopulateParentTables);

        final Table account = generatedTables.get("account");
        assertThat(account.getColumn("name").value(), is(aString()));

        final Table invoice = generatedTables.get("invoice");
        assertThat(invoice.getColumn("amount").value(), is(aNumber()));
        assertThat(invoice.getColumn("account_id").value(), is(aNumber()));

        final Table payment = generatedTables.get("payment");
        assertThat(payment.getColumn("amount").value(), is(aNumber()));
        assertThat(Integer.parseInt(String.valueOf(payment.getColumn("status").value())), is(equalTo(2)));
        assertThat(payment.getColumn("invoice_id").value(), is(aNumber()));

    }

    @Test
    public void generates_sqls_only_for_parent_tables() throws SQLException {
        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", "2");

        preassignedValues.put("payment", values);

        Table paymentTable = new Table("payment", connection, preassignedValues, new AutoIncrementBasedCreation(connection));

        final boolean onlyPopulateParentTables = true;
        final Map<String, Table> populatedTables = paymentTable.populate(onlyPopulateParentTables);

        assertThat(populatedTables.get("account").getColumn("name").value(), is(aString()));
        assertThat(populatedTables.get("invoice").getColumn("amount").value(), is(aNumber()));
        assertThat(populatedTables.get("invoice").getColumn("account_id").value(), is(aNumber()));
    }

    @Test
    public void identifies_its_own_primary_keys() throws SQLException {
        final List<String> primaryKeys = paymentTable.getPrimaryKeys();
        assertThat(primaryKeys, contains("id"));
    }

    @Test
    public void keeps_track_of_ancestry() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Table refundTable = new Table("refund", connection, preassignedValues, new AutoIncrementBasedCreation(connection));
        final Map<String, Table> populatedTables = refundTable.populate(false);

        final Table populatedRefundTable = populatedTables.get("refund");
        final Column accountAssociatedWithRefund = populatedRefundTable.getColumn("account_id");

        final Table populatedInvoiceTable = populatedTables.get("invoice");
        final Column accountAssociatedWithInvoice = populatedInvoiceTable.getColumn("account_id");

        System.out.println("Account associated with invoice:" + accountAssociatedWithInvoice.value());
        System.out.println("Account associated with refund:" + accountAssociatedWithRefund.value());

        //TODO: Fix Column.toString()
        assertThat(accountAssociatedWithInvoice.value(), is(equalTo(accountAssociatedWithRefund.value())));
    }

    @Test
    public void deletes_generated_records() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Table refundTable = new Table("refund", connection, preassignedValues, new AutoIncrementBasedCreation(connection));
        final Map<String, Table> populatedTables = refundTable.populate(false);

        refundTable.delete();

        final Statement statement = connection.createStatement();
        for (Map.Entry<String, Table> entrySet : populatedTables.entrySet()) {
            final String query = String.format("select count(*) from %s where id=%s", entrySet.getKey(), entrySet.getValue().getColumn("id").value());
            System.out.println("query:" + query);
            statement.execute(query);
            assertNoRows(statement.getResultSet());
        }

        statement.close();
    }

    private void assertNoRows(final ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            final int count = resultSet.getInt(1);
            assertThat(count, is(equalTo(0)));
        }
    }

    @Test
    public void primary_keys_are_marked() throws SQLException {

        final Table accountTable = new Table("account", connection, Collections.<String, Map<String, Object>>emptyMap(), new DoNotGeneratePrimaryKeys(connection));
        final Map<String, Table> populatedTables = accountTable.populate(false);

        assertTrue(populatedTables.get("account").getColumn("id").isPrimaryKey());
    }
}