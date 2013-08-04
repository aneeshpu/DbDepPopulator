package com.aneeshpu.dpdeppop.schema;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.aneeshpu.dpdeppop.schema.Matchers.aNumber;
import static com.aneeshpu.dpdeppop.schema.Matchers.aString;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TableTest {

    private static final Logger LOG = Logger.getLogger(TableTest.class);
    private final Connection connection = new ConnectionFactory().invoke();
    private Table paymentTable;

    @Before
    public void setup() throws SQLException {
        paymentTable = new Table("payment", connection, new HashMap<String, Map<String, Object>>());
        paymentTable.initialize();
    }

    @Test
    public void gets_initialized_with_parents() throws SQLException {

        final Map<String, Table> parents = paymentTable.parents();

        assertNotNull(parents.get("invoice"));
        assertNotNull(parents.get("payment_status"));
    }

    @Test
    public void parents_are_initialized_with_their_parents() throws SQLException {

        final Table invoiceTable = get(paymentTable.parents(), new Table("invoice", connection, new HashMap<String, Map<String, Object>>()));
        assertNotNull(invoiceTable.parents().get("account"));
    }

    @Test
    public void populates_columns() {
        assertNotNull(paymentTable.columns().get("id"));
    }

    @Test
    public void foreign_keys_are_populated_with_their_referencing_tables() {
        final Column invoiceId = paymentTable.columns().get("invoice_id");
        assertThat(invoiceId.getReferencingColumn(), is(equalTo(Column.buildColumn().withTable(new Table("invoice", connection, new HashMap<String, Map<String, Object>>())).withName("id").create())));
    }

    private Table get(final Map<String, Table> parents, final Table table) {
        return parents.get(table.toString());
    }

    @Test
    public void creates_a_string_representation_of_table() {

        assertThat(new Table("payment", null, new HashMap<String, Map<String, Object>>()).toString(), is(equalTo("payment")));
    }

    @Test
    public void generates_an_insert_query() throws SQLException {

        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<>();
        final Table accountTable = new Table("account", connection, preassignedValues);
        accountTable.initialize();

        final Map<String, Table> tables = accountTable.populate(false);
        final Table generatedAccountTable = tables.get("account");
        System.out.println(generatedAccountTable);

        assertThat(generatedAccountTable.getColumn("id").value(), is(aNumber()));
    }

    @Test
    public void generates_insert_sql_for_parent_tables_and_itself() throws SQLException {

        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<>();
        final HashMap<String, Object> values = new HashMap<>();
        values.put("status", "2");

        preassignedValues.put("payment", values);

        Table paymentTable = new Table("payment", connection, preassignedValues);
        paymentTable.initialize();

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
        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<>();
        final HashMap<String, Object> values = new HashMap<>();
        values.put("status", "2");

        preassignedValues.put("payment", values);

        Table paymentTable = new Table("payment", connection, preassignedValues);
        paymentTable.initialize();

        final boolean onlyPopulateParentTables = true;
        final Map<String, Table> generatedSqls = paymentTable.populate(onlyPopulateParentTables);

        assertThat(generatedSqls.get("account").getColumn("name").value(), is(aString()));
        assertThat(generatedSqls.get("invoice").getColumn("amount").value(), is(aNumber()));
        assertThat(generatedSqls.get("invoice").getColumn("account_id").value(), is(aNumber()));
    }
}