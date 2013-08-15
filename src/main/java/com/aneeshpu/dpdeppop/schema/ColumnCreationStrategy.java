package com.aneeshpu.dpdeppop.schema;

import java.sql.SQLException;
import java.util.Map;

public interface ColumnCreationStrategy {
    Map<String, Column> populateColumns(Table table, Map<String, Table> parentTables) throws SQLException;
}
