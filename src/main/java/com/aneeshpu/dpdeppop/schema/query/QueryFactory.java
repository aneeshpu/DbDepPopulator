package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.Record;

import java.sql.SQLException;
import java.util.Map;

public class QueryFactory {


    public QueryFactory() {
    }

    public static Insert generateInsertQuery(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record) throws SQLException {

        return new Insert(columns, preassignedValues, record);
//        return new Insert(columns,preassignedValues,record).generateInsertQuery();
    }
}