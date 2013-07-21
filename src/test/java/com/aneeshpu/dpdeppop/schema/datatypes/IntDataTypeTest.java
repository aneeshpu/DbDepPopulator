package com.aneeshpu.dpdeppop.schema.datatypes;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IntDataTypeTest {

    @Test
    public void generates_an_int_value() {

        final IntDataType anInt = new IntDataType("int");
        assertNotNull(anInt.generateDefaultValue());
    }

}
