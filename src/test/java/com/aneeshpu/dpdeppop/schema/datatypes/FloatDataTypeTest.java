package com.aneeshpu.dpdeppop.schema.datatypes;

import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class FloatDataTypeTest {

    private Connection connection = new ConnectionFactory().invoke();

    @Test
    public void generates_a_float_value() {

        final FloatDataType floatDataType = new FloatDataType("float");
        assertNotNull(floatDataType.generateDefaultValue());
    }

    @Test
    @Ignore("need to implement money data type instead of float")
    public void retrieves_generated_string_value_from_result_set() throws SQLException {

        final Statement statement = connection.createStatement();
        connection.setAutoCommit(false);

        final String accountInsertQuery = "insert into account (name) values ('aneesh')";
        statement.executeUpdate(accountInsertQuery, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        final int accountId = generatedKeys.getInt("id");

        System.out.println("inserted account id:" + accountId);

        final String invoiceInsertQuery = "insert into invoice (amount, account_id) values (100," + accountId + ")";
        statement.executeUpdate(invoiceInsertQuery, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeysForInvoiceTable = statement.getGeneratedKeys();
        generatedKeysForInvoiceTable.next();

        final int invoiceId = generatedKeysForInvoiceTable.getInt("id");

        final String paymentInsertQuery = "insert into payment(invoice_id, amount, status) values (" + invoiceId + ",132,2)";
        statement.executeUpdate(paymentInsertQuery, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeysResultSet = statement.getGeneratedKeys();
        generatedKeysResultSet.next();

        final FloatDataType floatDataType = new FloatDataType("float");

        assertThat(floatDataType.getGeneratedValue(generatedKeysResultSet, "amount"), is(equalTo(132f)));

        connection.rollback();

    }

}
