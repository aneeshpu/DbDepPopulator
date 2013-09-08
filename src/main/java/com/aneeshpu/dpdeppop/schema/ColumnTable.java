package com.aneeshpu.dpdeppop.schema;

class ColumnTable {
    private final String primaryKeyColName;

    private final Record primaryRecord;

    public ColumnTable(final String primaryKeyColName, final Record primaryRecord) {
        this.primaryKeyColName = primaryKeyColName;
        this.primaryRecord = primaryRecord;
    }

    Record getPrimaryRecord() {
        return primaryRecord;
    }

    String getPrimaryKeyColName() {
        return primaryKeyColName;
    }
}
