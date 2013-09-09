package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.Record;

import java.sql.SQLException;
import java.util.Map;

public class QueryFactory {

    private final Insert insert = new Insert();

    public QueryFactory() {
    }

    public String generateInsertQuery(final Map<String, Column> columns, final Map<String, Map<String, Object>> preassignedValues, final Record record) throws SQLException {
        return insert.generateInsertQuery(columns, preassignedValues, record);
    }
}