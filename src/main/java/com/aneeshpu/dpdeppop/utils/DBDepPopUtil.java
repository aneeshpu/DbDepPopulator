package com.aneeshpu.dpdeppop.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBDepPopUtil {
    public static void printCols(final ResultSetMetaData paymentTableResultSetMetaData, final String logging_prefix) throws SQLException {
        System.out.println(logging_prefix);
        for (int i = 1; i <= paymentTableResultSetMetaData.getColumnCount(); i++) {
//            System.out.print(paymentTableResultSet.getString(i) + "\t");
//            System.out.println(paymentTableResultSetMetaData.getColumnType(i) + "," + paymentTableResultSetMetaData.getColumnTypeName(i) + "," + paymentTableResultSetMetaData.isNullable(i));
            System.out.print(paymentTableResultSetMetaData.getColumnName(i) + "\t");
        }

        System.out.println("\n");
    }

    public static void printValues(final ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {

            for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {

                System.out.print(resultSet.getString(i));
                System.out.print("\t");
            }

            System.out.println("\n");
        }
    }
}
