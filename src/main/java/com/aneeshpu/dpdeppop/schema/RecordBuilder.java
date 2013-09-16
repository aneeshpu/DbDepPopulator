package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.query.QueryFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class RecordBuilder {
    private String name;
    private Map<String, Map<String, Object>> preassignedValues;
    private ColumnCreationStrategy columnCreationStrategy;
    private QueryFactory queryFactory;
    private Map<String, List<Tuple>> parentTableMetadata;

    public RecordBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public RecordBuilder setConnection(final Connection connection) {
        return this;
    }

    public RecordBuilder withPreassignedValues(final Map<String, Map<String, Object>> preassignedValues) {
        this.preassignedValues = preassignedValues;
        return this;
    }

    public RecordBuilder setColumnCreationStrategy(final ColumnCreationStrategy columnCreationStrategy) {
        this.columnCreationStrategy = columnCreationStrategy;
        return this;
    }

    public RecordBuilder withQueryFactory(final Connection connection) {
        this.queryFactory = new QueryFactory();
        return this;
    }

    public RecordBuilder withParentMetaData(final Map<String,List<Tuple>> parentTableMetadata) {
        this.parentTableMetadata = parentTableMetadata;
        return this;
    }

    public Record createRecord() {
        return new Record(name, preassignedValues, columnCreationStrategy, queryFactory, parentTableMetadata);
    }
}