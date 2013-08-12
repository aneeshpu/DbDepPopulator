package com.aneeshpu.dpdeppop;

import com.aneeshpu.dpdeppop.utils.DBDepPopUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;

public class DbDepPopTest {

    @Test
    @Ignore
    public void gets_metadata() throws SQLException {

//        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/datadeppop", "datapopulator", "letmein");
        final Connection connection = DriverManager.getConnection("jdbc:postgresql://384070-hmdb-n01.dev1.ord1.us.ci.rackspace.net/hostingmatrix", "smix_user", "password");
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

        while(generatedKeys.next()){
            System.out.println(generatedKeys.getString("name"));
        }

    }

}
