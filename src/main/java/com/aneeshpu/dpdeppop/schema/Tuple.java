package com.aneeshpu.dpdeppop.schema;

public class Tuple {
    private final String foreignKeyColumnName;
    private final String primaryKeyColumnName;
    private final String primaryKeyTableName;

    public Tuple(final String foreignKeyColumnName, final String primaryKeyColumnName, final String primaryKeyTableName) {
        this.foreignKeyColumnName = foreignKeyColumnName;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.primaryKeyTableName = primaryKeyTableName;
    }

    public String getForeignKeyColumnName() {
        return foreignKeyColumnName;
    }

    public String getPrimaryKeyTableName() {
        return primaryKeyTableName;
    }

    public String getPrimaryKeyColumnName() {
        return primaryKeyColumnName;
    }
}
