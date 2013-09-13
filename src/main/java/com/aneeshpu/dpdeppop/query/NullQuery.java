package com.aneeshpu.dpdeppop.query;

import java.sql.ResultSet;
import java.sql.SQLException;

class NullQuery implements Query{
    @Override
    public ResultSet execute() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
