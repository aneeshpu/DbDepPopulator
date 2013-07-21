package com.aneeshpu.dpdeppop.schema.datatypes;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataTypeFactory {
    public static DataType create(final String dataType) {

        final String dataTypeInLowerCase = dataType.toLowerCase();
        if (dataTypeInLowerCase.startsWith("int")) {
            return new IntDataType(dataType);

        } else if (dataTypeInLowerCase.startsWith("float") || dataTypeInLowerCase.startsWith("money")) {
            return new FloatDataType(dataType);

        } else if (dataTypeInLowerCase.startsWith("char") || dataTypeInLowerCase.startsWith("varchar") || dataTypeInLowerCase.startsWith("text")) {
            return new CharacterDataType(dataType);

        } else if (dataTypeInLowerCase.equalsIgnoreCase("serial")){
            return new SerialDataType(dataType);
        }

        throw new UnknownDataTypeException(dataType + " is not a known datatype");
    }
}
