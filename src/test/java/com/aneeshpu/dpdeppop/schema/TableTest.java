package com.aneeshpu.dpdeppop.schema;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

        final List<Table> parents = paymentTable.parents();

        assertThat(parents, contains(new Table("invoice", connection)));
        assertThat(parents, contains(new Table("payment_status", connection)));
    }

    @Test
    public void parents_are_initialized_with_their_parents() throws SQLException {

        final Table invoiceTable = get(paymentTable.parents(), new Table("invoice", connection));
        assertThat(invoiceTable.parents(), contains(new Table("account", connection)));
    }

    @Test
    public void populates_columns(){

        assertThat(paymentTable.columns(),contains(new Column("id", null)));

    }

    private Table get(final List<Table> parents, final Table table) {
        for (Table parent : parents) {

            if(table.equals(parent)){
                return parent;
            }

        }
        return null;
    }

    @Test
    public void creates_a_string_representation_of_table(){

        assertThat(new Table("payment", null).toString(), is(equalTo("payment")));
    }

    private Matcher<? super List<? extends Object>> contains(final Object expectedObject) {
        return new BaseMatcher<List<? extends Object>>() {
            @Override
            public boolean matches(final Object o) {
                if(!(o instanceof List)){

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
