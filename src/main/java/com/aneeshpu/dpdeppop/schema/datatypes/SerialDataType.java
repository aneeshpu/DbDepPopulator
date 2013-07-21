package com.aneeshpu.dpdeppop.schema.datatypes;

public class SerialDataType implements DataType<Integer>{
    private final String dataType;

    public SerialDataType(final String dataType) {

        this.dataType = dataType;
    }

    @Override
    public Integer generateDefaultValue() {
        return null;
    }
}
