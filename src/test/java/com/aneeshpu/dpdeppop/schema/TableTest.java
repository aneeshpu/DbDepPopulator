package com.aneeshpu.dpdeppop.schema;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TableTest {

    private final Connection connection = new ConnectionFactory().invoke();
    private Table paymentTable;

    @Before
    public void setup() throws SQLException {
        paymentTable = new Table("payment", connection);
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

        final Table invoiceTable = get(paymentTable.parents(), new Table("invoice", connection));
        assertNotNull(invoiceTable.parents().get("account"));
    }

    @Test
    public void populates_columns() {
        assertNotNull(paymentTable.columns().get("id"));
    }

    @Test
    public void foreign_keys_are_populated_with_their_referencing_tables() {
        final Column invoiceId = paymentTable.columns().get("invoice_id");
        assertThat(invoiceId.getReferencingColumn(), is(equalTo(Column.buildColumn().withName("id").create())));
    }

    private Table get(final Map<String, Table> parents, final Table table) {
        return parents.get(table.toString());
    }

    @Test
    public void creates_a_string_representation_of_table() {

        assertThat(new Table("payment", null).toString(), is(equalTo("payment")));
    }

    @Test
    public void generates_an_insert_query() throws SQLException {

        final Table accountTable = new Table("account", connection);
        accountTable.initialize();

        final List<String> sql = accountTable.insertDefaultValues();
        System.out.println(sql.get(0));

        final Pattern insertIntoAccountQueryPattern = Pattern.compile("insert into account \\(name\\) values \\('\\w'\\)");

        assertThat(insertIntoAccountQueryPattern.matcher(sql.get(0)).matches(), is(true));
    }

    @Test
    public void generates_insert_sql_for_parent_tables_and_itself() throws SQLException {

        paymentTable.initialize();

        final List<String> generatedSqls = paymentTable.insertDefaultValues();

        for (String generatedSql : generatedSqls) {
            System.out.println(generatedSql);
        }

        final List<Pattern> patterns = new ArrayList<>();

        patterns.add(Pattern.compile("insert into account \\(name\\) values \\('\\w'\\)"));
        patterns.add(Pattern.compile("insert into invoice \\(amount,account_id\\) values \\([-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+\\)"));
        patterns.add(Pattern.compile("insert into payment_status \\(id,description,name\\) values \\([-+]?[0-9]*\\.?[0-9]+,'\\w','\\w'\\)"));
        patterns.add(Pattern.compile("insert into payment \\(amount,status,invoice_id\\) values \\([-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+\\)"));

        assertThat(generatedSqls.size(), is(equalTo(4)));

        for (int i = 0; i < patterns.size(); i++) {
            final String generatedSql = generatedSqls.get(i);
            final Pattern pattern = patterns.get(i);

            assertThat(pattern + " does not match " + generatedSql,pattern.matcher(generatedSql).matches(), is(true));
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
