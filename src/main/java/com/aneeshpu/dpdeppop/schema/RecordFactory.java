package com.aneeshpu.dpdeppop.schema;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class RecordFactory {
    public static Record createRecordWithAutoIncrementBasedCreation(final String account, final Connection connection, final HashMap<String, Map<String, Object>> preassignedValues) {
        return new Record("payment", connection, preassignedValues, new AutoIncrementBasedCreation(connection));
    }
}
