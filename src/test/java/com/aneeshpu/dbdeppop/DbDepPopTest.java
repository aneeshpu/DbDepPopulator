package com.aneeshpu.dbdeppop;

import com.aneeshpu.dpdeppop.schema.Record;
import com.aneeshpu.dpdeppop.schema.RecordFactory;
import com.aneeshpu.dpdeppop.utils.DBDepPopUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DbDepPopTest {

    private String hostname;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    @Before
    public void setup() throws IOException {
        final Properties properties = new Properties();
        final InputStream resourceAsStream = this.getClass().getResourceAsStream("db.credentials.properties");
        properties.load(resourceAsStream);

        hostname = properties.getProperty("db.hostname");
        dbName = properties.getProperty("db.name");
        dbUsername = properties.getProperty("db.username");
        dbPassword = properties.getProperty("db.password");

    }

    @Test
    @Ignore
    public void what_is_wrong_with_populating_bil_invoice_table() throws SQLException, IOException {


        final Connection connection = DriverManager.getConnection("jdbc:postgresql://" + hostname + "/" + dbName, dbUsername, dbPassword);
//        final Connection connection = DriverManager.getConnection("jdbc:postgresql://384070-hmdb-n01.dev1.ord1.us.ci.rackspace.net/hostingmatrix", "smix_user", "password");
        connection.setAutoCommit(false);

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Map<String, Object> columnValues = new HashMap<String, Object>();

        columnValues.put("InvoiceItemTypeID", 8);
        columnValues.put("BillableChargeStatusID", 1);

        preassignedValues.put("BIL_BillableTax", columnValues);
        Record invoiceTable = null;
        try {
            invoiceTable = RecordFactory.createRecordDontAssignPrimaryKeys("BIL_Invoice", connection, preassignedValues);
            final Map<String, Record> populatedTables = invoiceTable.populate(true, connection);
        } finally {
            invoiceTable.delete(connection);
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        }

    }

    @Test
    @Ignore
    public void gets_metadata() throws SQLException {

//        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/datadeppop", "datapopulator", "letmein");
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://" + hostname + "/" + dbName, dbUsername, dbPassword);
        final DatabaseMetaData metaData = connection.getMetaData();
        final ResultSet paymentTableResultSet = metaData.getColumns(null, null, "BIL_CreditCard", null);
        DBDepPopUtil.printCols(paymentTableResultSet.getMetaData(), "PTRS");

        final ResultSet creditCardPrimaryKeys = metaData.getPrimaryKeys(null, null, "BIL_CreditCard");
        DBDepPopUtil.printCols(creditCardPrimaryKeys.getMetaData(), "getPrimaryKeys():");
        DBDepPopUtil.printValues(creditCardPrimaryKeys);

        final ResultSet creditCardVersionedCols = metaData.getVersionColumns(null, null, "BIL_CreditCard");
        DBDepPopUtil.printCols(creditCardVersionedCols.getMetaData(), "getVersionColumns():");

        DBDepPopUtil.printValues(creditCardVersionedCols);
//        final ResultSet paymentTableResultSet = metaData.getColumns(null, null, "payment", null);

        final ResultSetMetaData paymentTableResultSetMetaData = paymentTableResultSet.getMetaData();

        System.out.println(paymentTableResultSetMetaData.getColumnCount());

        DBDepPopUtil.printCols(paymentTableResultSetMetaData, "default");

        System.out.println("\n");

        while (paymentTableResultSet.next()) {
            for (int i = 1; i <= paymentTableResultSetMetaData.getColumnCount(); i++) {
//                System.out.print(paymentTableResultSet.getString(i) + "\t");
//            System.out.println(paymentTableResultSetMetaData.getColumnType(i) + "," + paymentTableResultSetMetaData.getColumnTypeName(i) + "," + paymentTableResultSetMetaData.isNullable(i));
            }

            System.out.println("\n");
        }

        System.out.println("\n");

        while (paymentTableResultSet.next()) {
            for (int i = 1; i <= paymentTableResultSetMetaData.getColumnCount(); i++) {

                System.out.print(paymentTableResultSet.getString(i) + "\t");
            }

            System.out.println("\n");
        }

        final ResultSet paymentCrossReference = metaData.getImportedKeys(null, null, "payment");

//        final ResultSet paymentCrossReference = metaData.getCrossReference(null, null, null, null, null, "payment");

        DBDepPopUtil.printCols(paymentCrossReference.getMetaData(), "imported_keys");

        while (paymentCrossReference.next()) {
            System.out.print(paymentCrossReference.getString("pktable_name") + "\t");
            System.out.print(paymentCrossReference.getString("pkcolumn_name") + "\t");
            System.out.print(paymentCrossReference.getString("fktable_name") + "\t");
            System.out.print(paymentCrossReference.getString("fkcolumn_name") + "\t");

            System.out.println("\n");
        }

        /*paymentCrossReference.next();
        System.out.print(paymentCrossReference.getString("pktable_name") + "\t");
        System.out.print(paymentCrossReference.getString("pkcolumn_name") + "\t");
        System.out.print(paymentCrossReference.getString("fktable_name") + "\t");
        System.out.print(paymentCrossReference.getString("fkcolumn_name") + "\t");

        for (int i = 1; i <= paymentCrossReference.getMetaData().getColumnCount(); i++) {
                System.out.print(paymentCrossReference.getString(i) + "\t");

            while (paymentCrossReference.next()) {


                System.out.println("\n");
            }
        }*/

        final String insertSQL = "insert into account (name) values ('c')";

        final Statement statement = connection.createStatement();
        statement.executeUpdate(insertSQL, Statement.RETURN_GENERATED_KEYS);

        final ResultSet generatedKeys = statement.getGeneratedKeys();

        DBDepPopUtil.printCols(generatedKeys.getMetaData(), "insert into account");

        while (generatedKeys.next()) {
            System.out.println(generatedKeys.getString("name"));
        }

    }

}
