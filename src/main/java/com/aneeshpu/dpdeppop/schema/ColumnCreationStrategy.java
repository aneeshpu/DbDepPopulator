package com.aneeshpu.dpdeppop.schema;

import java.sql.SQLException;
import java.util.Map;

public interface ColumnCreationStrategy {
    Map<String, Column> populateColumns(Record record, Map<String, Record> parentTables) throws SQLException;
}
