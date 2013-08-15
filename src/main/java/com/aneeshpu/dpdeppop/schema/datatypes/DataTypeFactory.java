package com.aneeshpu.dpdeppop.schema.datatypes;

public class DataTypeFactory {
    public static DataType create(final String dataType) {

        final String dataTypeInLowerCase = dataType.toLowerCase();
        if (dataTypeInLowerCase.startsWith("int")) {
            return new IntDataType(dataType);

        } else if (dataTypeInLowerCase.startsWith("float") || dataTypeInLowerCase.startsWith("money") || dataTypeInLowerCase.startsWith("numeric")) {
            return new FloatDataType(dataType);

        } else if (dataTypeInLowerCase.startsWith("char") || dataTypeInLowerCase.startsWith("varchar") || dataTypeInLowerCase.startsWith("text") || dataTypeInLowerCase.startsWith("bpchar")) {
            return new CharacterDataType(dataType);

        } else if (dataTypeInLowerCase.equalsIgnoreCase("serial")) {
            return new SerialDataType(dataType);

        } else if (dataTypeInLowerCase.startsWith("bool")) {
            return new BoolDataType();

        } else if (dataTypeInLowerCase.startsWith("time")) {
            return new DateDataType();
        }

        throw new UnknownDataTypeException(dataType + " is not a known datatype");
    }
}
