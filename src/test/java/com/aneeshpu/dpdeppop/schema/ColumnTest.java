package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import java.sql.Connection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 16/7/13
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ColumnTest {

    private Connection connection = new ConnectionFactory().invoke();


    @Test
    public void considers_two_columns_from_the_same_table_with_same_names_are_equal(){

        final Column idColumn = new Column("id", new Table("payment", connection));
        final Column anotherIdColumn = new Column("id", new Table("payment", connection));

        assertThat(idColumn, is(equalTo(anotherIdColumn)));
    }

    @Test
    public void prints_to_string_with_column_name(){

        assertThat(new Column("id", null).toString(), is(equalTo("id")));
    }
}
