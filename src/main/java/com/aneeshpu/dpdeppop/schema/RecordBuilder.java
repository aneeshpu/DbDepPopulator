package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.schema.query.QueryFactory;

import java.sql.Connection;
import java.util.Map;

public class RecordBuilder {
    private String name;
    private Connection connection;
    private Map<String, Map<String, Object>> preassignedValues;
    private ColumnCreationStrategy columnCreationStrategy;
    private QueryFactory queryFactory;

    public RecordBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public RecordBuilder setConnection(final Connection connection) {
        this.connection = connection;
        return this;
    }

    public RecordBuilder setPreassignedValues(final Map<String, Map<String, Object>> preassignedValues) {
        this.preassignedValues = preassignedValues;
        return this;
    }

    public RecordBuilder setColumnCreationStrategy(final ColumnCreationStrategy columnCreationStrategy) {
        this.columnCreationStrategy = columnCreationStrategy;
        return this;
    }

    public RecordBuilder withQueryFactory(final Connection connection) {

        this.queryFactory = new QueryFactory(connection);
        return this;
    }

    public Record createRecord() {
        return new Record(name, connection, preassignedValues, columnCreationStrategy, queryFactory);
    }
}