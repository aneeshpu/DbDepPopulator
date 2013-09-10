package com.aneeshpu.dpdeppop.schema;

import java.sql.Connection;
import java.util.Map;

public class RecordFactory {
    public static Record createRecordWithAutoIncrementBasedCreation(final String tableName, final Connection connection, final Map<String, Map<String, Object>> preassignedValues) {
        return new RecordBuilder()
                .setName(tableName)
                .setConnection(connection)
                .setPreassignedValues(preassignedValues)
                .setColumnCreationStrategy(new AutoIncrementBasedCreation(connection))
                .withQueryFactory(connection)
                .createRecord();
    }

    public static Record createRecordDontAssignPrimaryKeys(final String tableName, final Connection connection, final Map<String, Map<String, Object>> preassignedValues) {
        return new RecordBuilder()
                .setName(tableName)
                .setConnection(connection)
                .setPreassignedValues(preassignedValues)
                .setColumnCreationStrategy(new DoNotGeneratePrimaryKeys(connection))
                .withQueryFactory(connection)
                .createRecord();

    }
}
