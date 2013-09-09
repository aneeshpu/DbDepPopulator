package com.aneeshpu.dpdeppop.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Query {
    ResultSet execute() throws SQLException;
}
