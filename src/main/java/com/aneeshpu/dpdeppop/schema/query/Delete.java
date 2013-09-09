package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.NameValue;
import com.aneeshpu.dpdeppop.schema.Record;

import java.util.Map;

public class Delete {
    private final Column primaryKeyColumn;
    private final Map<String, Map<String, Object>> preassignedValues;
    private final Record record;

    public Delete(final Column primaryKeyColumn, final Map<String, Map<String, Object>> preassignedValues, final Record record) {
        this.primaryKeyColumn = primaryKeyColumn;
        this.preassignedValues = preassignedValues;
        this.record = record;
    }

    @Override
    public String toString() {
        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("deleting record from " + this.record);
        }

        if (Record.LOG.isDebugEnabled()) {
            Record.LOG.debug("deleting record id " + this.primaryKeyColumn.value() + " from table " + this.record);
        }

        //TODO:push formattedName and formattedValue into Column
        final NameValue nameValue = this.primaryKeyColumn.nameValue(this.preassignedValues);
        final String deleteQuery = String.format("delete from \"%s\" where %s=%s", this.record.tableName(), nameValue.formattedNameWithoutTrailingComma(), nameValue.formattedValueWithoutTrailingComma());

        if (Record.LOG.isInfoEnabled()) {
            Record.LOG.info("delete query: " + deleteQuery);
        }
        return deleteQuery;
    }
}