package com.aneeshpu.dpdeppop.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

interface ColumnCreationStrategy {
    Map<String, Column> populateColumns(Record record, final Connection connection) throws SQLException;
}
