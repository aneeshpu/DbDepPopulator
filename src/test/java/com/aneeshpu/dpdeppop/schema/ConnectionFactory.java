package com.aneeshpu.dpdeppop.schema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
* Created with IntelliJ IDEA.
* User: aneeshpu
* Date: 16/7/13
* Time: 1:56 AM
* To change this template use File | Settings | File Templates.
*/
public class ConnectionFactory {
    public Connection invoke() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost/datadeppop", "datapopulator", "letmein");
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }
}
