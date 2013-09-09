package com.aneeshpu.dpdeppop.schema.query;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Query {
    ResultSet execute() throws SQLException;
}
