package com.aneeshpu.dpdeppop.schema.datatypes;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class FloatDataTypeTest {
    
    @Test
    public void generates_a_float_value(){

        final FloatDataType floatDataType = new FloatDataType("float");
        assertNotNull(floatDataType.generateDefaultValue());
    }

}
