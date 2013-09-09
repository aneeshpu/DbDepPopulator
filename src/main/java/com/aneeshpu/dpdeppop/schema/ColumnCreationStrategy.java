package com.aneeshpu.dpdeppop.schema;

import java.sql.SQLException;
import java.util.Map;

interface ColumnCreationStrategy {
    Map<String, Column> populateColumns(Record record) throws SQLException;
}
