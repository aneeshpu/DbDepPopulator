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

public class RecordTest {

    private final Connection connection = new ConnectionFactory().getConnection();
    private Record paymentRecord;

    @Before
    public void setup() throws SQLException {
        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<String,Map<String,Object>>();
        final Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.put("status", "2");
        preassignedValues.put("payment", columnValues);
        paymentRecord = new RecordBuilder()
                .withQueryFactory(connection)
                .setName("payment")
                .setConnection(connection)
                .setPreassignedValues(preassignedValues)
                .setColumnCreationStrategy(new AutoIncrementBasedCreation(connection))
                .createRecord();
    }

    @Test
    public void populates_columns() throws SQLException {
        paymentRecord.populate(false);
        assertNotNull(paymentRecord.columns().get("id"));
    }

    @Test
    public void foreign_keys_are_populated_with_their_referencing_tables() throws SQLException {
        paymentRecord.populate(false);
        final Column invoiceId = paymentRecord.columns().get("invoice_id");
        assertThat(invoiceId.getReferencingColumn(), is(equalTo(Column.buildColumn().withTable(new RecordBuilder().setName("invoice").setConnection(connection).setPreassignedValues(new HashMap<String, Map<String, Object>>()).setColumnCreationStrategy(new AutoIncrementBasedCreation(connection)).createRecord()).withName("id").create())));
    }

    @Test
    public void creates_a_string_representation_of_table() {

        assertThat(new RecordBuilder().setName("payment").setConnection(null).setPreassignedValues(new HashMap<String, Map<String, Object>>()).setColumnCreationStrategy(new AutoIncrementBasedCreation(connection)).createRecord().toString(), is(equalTo("payment")));
    }

    @Test
    public void generates_an_insert_query() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Record accountRecord = new RecordBuilder().withQueryFactory(connection).setName("account").setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(new AutoIncrementBasedCreation(connection)).createRecord();

        final Map<String, Record> tables = accountRecord.populate(false);
        final Record generatedAccountRecord = tables.get("account");
        System.out.println(generatedAccountRecord);

        assertThat(generatedAccountRecord.getColumn("id").value(), is(aNumber()));
    }

    @Test
    public void generates_insert_sql_for_parent_tables_and_itself() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", "2");

        preassignedValues.put("payment", values);

        Record paymentRecord = new RecordBuilder().withQueryFactory(connection).setName("payment").setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(new AutoIncrementBasedCreation(connection)).createRecord();

        final boolean onlyPopulateParentTables = false;
        final Map<String, Record> generatedTables = paymentRecord.populate(onlyPopulateParentTables);

        final Record account = generatedTables.get("account");
        assertThat(account.getColumn("name").value(), is(aString()));

        final Record invoice = generatedTables.get("invoice");
        assertThat(invoice.getColumn("amount").value(), is(aNumber()));
        assertThat(invoice.getColumn("account_id").value(), is(aNumber()));

        final Record payment = generatedTables.get("payment");
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

        Record paymentRecord = new RecordBuilder()
                .withQueryFactory(connection)
                .setName("payment")
                .setConnection(connection)
                .setPreassignedValues(preassignedValues)
                .setColumnCreationStrategy(new DoNotGeneratePrimaryKeys(connection))
                .createRecord();

        final boolean onlyPopulateParentTables = false;
        final Map<String, Record> generatedTables = paymentRecord.populate(onlyPopulateParentTables);

        final Record account = generatedTables.get("account");
        assertThat(account.getColumn("name").value(), is(aString()));

        final Record invoice = generatedTables.get("invoice");
        assertThat(invoice.getColumn("amount").value(), is(aNumber()));
        assertThat(invoice.getColumn("account_id").value(), is(aNumber()));

        final Record payment = generatedTables.get("payment");
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

        Record paymentRecord = new RecordBuilder().setName("payment").setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(new AutoIncrementBasedCreation(connection)).createRecord();

        final boolean onlyPopulateParentTables = true;
        final Map<String, Record> populatedTables = paymentRecord.populate(onlyPopulateParentTables);

        assertThat(populatedTables.get("account").getColumn("name").value(), is(aString()));
        assertThat(populatedTables.get("invoice").getColumn("amount").value(), is(aNumber()));
        assertThat(populatedTables.get("invoice").getColumn("account_id").value(), is(aNumber()));
    }

    @Test
    public void identifies_its_own_primary_keys() throws SQLException {
        final List<String> primaryKeys = paymentRecord.getPrimaryKeys();
        assertThat(primaryKeys, contains("id"));
    }

    @Test
    public void keeps_track_of_ancestry() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.put("status", "2");
        preassignedValues.put("payment", columnValues);
        final Record refundRecord = new RecordBuilder().withQueryFactory(connection).setName("refund").setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(new AutoIncrementBasedCreation(connection)).createRecord();
        final Map<String, Record> populatedTables = refundRecord.populate(false);

        final Record populatedRefundRecord = populatedTables.get("refund");
        final Column accountAssociatedWithRefund = populatedRefundRecord.getColumn("account_id");

        final Record populatedInvoiceRecord = populatedTables.get("invoice");
        final Column accountAssociatedWithInvoice = populatedInvoiceRecord.getColumn("account_id");

        System.out.println("Account associated with invoice:" + accountAssociatedWithInvoice.value());
        System.out.println("Account associated with refund:" + accountAssociatedWithRefund.value());

        //TODO: Fix Column.toString()
        assertThat(accountAssociatedWithInvoice.value(), is(equalTo(accountAssociatedWithRefund.value())));
    }

    @Test
    public void deletes_generated_records() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.put("status","2");
        preassignedValues.put("payment", columnValues);
        final Record refundRecord = new RecordBuilder().withQueryFactory(connection).setName("refund").setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(new DoNotGeneratePrimaryKeys(connection)).createRecord();
        final Map<String, Record> populatedTables = refundRecord.populate(false);

        refundRecord.delete();

        final Statement statement = connection.createStatement();
        for (Map.Entry<String, Record> entrySet : populatedTables.entrySet()) {
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

        final Record accountRecord = new RecordBuilder().withQueryFactory(connection).setName("account").setConnection(connection).setPreassignedValues(Collections.<String, Map<String, Object>>emptyMap()).setColumnCreationStrategy(new DoNotGeneratePrimaryKeys(connection)).createRecord();
        final Map<String, Record> populatedTables = accountRecord.populate(false);

        assertTrue(populatedTables.get("account").getColumn("id").isPrimaryKey());
    }

    @Test
    public void delete_does_not_fail_if_only_parent_tables_are_populated() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.put("status","2");
        preassignedValues.put("payment", columnValues);
        final Record refundRecord = new RecordBuilder().withQueryFactory(connection).setName("refund").setConnection(connection).setPreassignedValues(preassignedValues).setColumnCreationStrategy(new DoNotGeneratePrimaryKeys(connection)).createRecord();
        final Map<String, Record> populatedTables = refundRecord.populate(true);

        refundRecord.delete();

        final Statement statement = connection.createStatement();
        for (Map.Entry<String, Record> entrySet : populatedTables.entrySet()) {
            final String query = String.format("select count(*) from %s where id=%s", entrySet.getKey(), entrySet.getValue().getColumn("id").value());
            System.out.println("query:" + query);
            statement.execute(query);
            assertNoRows(statement.getResultSet());
        }

        statement.close();

    }
}