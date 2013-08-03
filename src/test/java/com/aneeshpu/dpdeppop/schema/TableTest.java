package com.aneeshpu.dpdeppop.schema;

import org.apache.log4j.Logger;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

        final List<String> sql = accountTable.insertDefaultValues();
        System.out.println(sql.get(0));

        final Pattern insertIntoAccountQueryPattern = Pattern.compile("insert into account \\(name\\) values \\('\\w'\\)");

        assertThat(insertIntoAccountQueryPattern.matcher(sql.get(0)).matches(), is(true));
    }

    @Test
    public void generates_insert_sql_for_parent_tables_and_itself() throws SQLException {

        final HashMap<String, Map<String, Object>> preassignedValues = new HashMap<>();
        final HashMap<String, Object> values = new HashMap<>();
        values.put("status", "2");
        preassignedValues.put("payment", values);

        Table paymentTable = new Table("payment", connection, preassignedValues);
        paymentTable.initialize();

        final List<String> generatedSqls = paymentTable.insertDefaultValues();

        final List<Pattern> patterns = new ArrayList<>();

        patterns.add(Pattern.compile("insert into account \\(name\\) values \\('\\w'\\)"));
        patterns.add(Pattern.compile("insert into invoice \\(amount,account_id\\) values \\([-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+\\)"));
        patterns.add(Pattern.compile("insert into payment_status \\(id,description,name\\) values \\([-+]?[0-9]*\\.?[0-9]+,'\\w','\\w'\\)"));
        patterns.add(Pattern.compile("insert into payment \\(amount,status,invoice_id\\) values \\([-+]?[0-9]*\\.?[0-9]+,2,[-+]?[0-9]*\\.?[0-9]+\\)"));

        assertThat(generatedSqls.size(), is(equalTo(4)));

        for (int i = 0; i < patterns.size(); i++) {
            final String generatedSql = generatedSqls.get(i);
            final Pattern pattern = patterns.get(i);

            assertThat(pattern + " does not match " + generatedSql, pattern.matcher(generatedSql).matches(), is(true));
        }
    }

    private Matcher<? super List<? extends Object>> contains(final Object expectedObject) {
        return new BaseMatcher<List<? extends Object>>() {
            @Override
            public boolean matches(final Object o) {
                if (!(o instanceof List)) {

                    return false;
                }
                List<Object> objects = (List<Object>) o;
                return objects.contains(expectedObject);
            }

            @Override
            public void describeTo(final Description description) {
            }
        };
    }

}
