package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.datatypes.DataTypeFactory;
import com.aneeshpu.dpdeppop.query.QueryFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class RecordBuilder {
    private final Connection connection;
    private String name;
    private Map<String, Map<String, Object>> preassignedValues;
    private ColumnCreationStrategy columnCreationStrategy;
    private QueryFactory queryFactory;
    private Map<String, List<Tuple>> parentTableMetadata;


    public RecordBuilder(final Connection connection) {
        this.connection = connection;
        columnCreationStrategy = new DoNotGeneratePrimaryKeys(connection, new DataTypeFactory());
        queryFactory = new QueryFactory();
    }

    public RecordBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public RecordBuilder withDataTypeFactory(DataTypeFactory dataTypeFactory){
        columnCreationStrategy = new DoNotGeneratePrimaryKeys(connection, dataTypeFactory);
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

    public RecordBuilder withQueryFactory() {
        this.queryFactory = new QueryFactory();
        return this;
    }

    public RecordBuilder withParentMetaData(final Map<String, List<Tuple>> parentTableMetadata) {
        this.parentTableMetadata = parentTableMetadata;
        return this;
    }

    public Record createRecord() {
        if (name == null) {
            throw new InvalidRecordException("Record cannot be created without a table name");
        }
        return new Record(name, preassignedValues, columnCreationStrategy, queryFactory, parentTableMetadata);
    }
}