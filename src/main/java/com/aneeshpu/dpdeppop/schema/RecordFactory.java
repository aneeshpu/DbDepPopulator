package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.datatypes.DataTypeFactory;

import java.sql.Connection;
import java.util.Map;

public class RecordFactory {
    public static Record createRecordWithAutoIncrementBasedCreation(final String tableName, final Connection connection, final Map<String, Map<String, Object>> preassignedValues) {
        return new RecordBuilder(connection)
                .setName(tableName)
                .withPreassignedValues(preassignedValues)
                .setColumnCreationStrategy(new AutoIncrementBasedCreation())
                .withQueryFactory()
                .createRecord();
    }

    public static Record createRecordDontAssignPrimaryKeys(final String tableName, final Connection connection, final Map<String, Map<String, Object>> preassignedValues) {
        return new RecordBuilder(connection)
                .setName(tableName)
                .withPreassignedValues(preassignedValues)
                .setColumnCreationStrategy(new DoNotGeneratePrimaryKeys(connection, new DataTypeFactory()))
                .withQueryFactory()
                .createRecord();

    }
}
