package com.aneeshpu.dpdeppop;

import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;

public class DbDepPopTest {

    @Test
    @Ignore
    public void gets_metadata() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/datadeppop", "datapopulator", "letmein");
        final DatabaseMetaData metaData = connection.getMetaData();
        final ResultSet paymentTableResultSet = metaData.getColumns(null, null, "payment", null);

        final ResultSetMetaData paymentTableResultSetMetaData = paymentTableResultSet.getMetaData();
        System.out.println(paymentTableResultSetMetaData.getColumnCount());

        while (paymentTableResultSet.next()) {
            for (int i = 1; i <= paymentTableResultSetMetaData.getColumnCount(); i++) {

//                System.out.print(paymentTableResultSet.getString(i) + "\t");
            }
        }

        final ResultSet paymentCrossReference = metaData.getImportedKeys(null, null, "payment");

//        final ResultSet paymentCrossReference = metaData.getCrossReference(null, null, null, null, null, "payment");
        paymentCrossReference.next();
        System.out.print(paymentCrossReference.getString("pktable_name") + "\t");
        System.out.print(paymentCrossReference.getString("pkcolumn_name") + "\t");
        System.out.print(paymentCrossReference.getString("fktable_name") + "\t");
        System.out.print(paymentCrossReference.getString("fkcolumn_name") + "\t");

        for (int i = 1; i <= paymentCrossReference.getMetaData().getColumnCount(); i++) {
//                System.out.print(paymentCrossReference.getString(i) + "\t");

            while (paymentCrossReference.next()) {


                System.out.println("\n");
            }
        }
    }
    

}
