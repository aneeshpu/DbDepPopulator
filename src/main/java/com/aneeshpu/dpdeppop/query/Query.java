package com.aneeshpu.dpdeppop.query;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Query {
    Logger QUERYLOG = Logger.getLogger(Query.class);

    ResultSet execute() throws SQLException;
}
