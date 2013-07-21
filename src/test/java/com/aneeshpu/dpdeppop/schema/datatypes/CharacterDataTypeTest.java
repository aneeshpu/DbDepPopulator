package com.aneeshpu.dpdeppop.schema.datatypes;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 7:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class CharacterDataTypeTest {

    @Test
    public void generates_a_default_value(){

        final CharacterDataType characterDataType = new CharacterDataType("character");
        final String defaultValue = characterDataType.generateDefaultValue();
        assertNotNull(defaultValue);
        assertThat(defaultValue, is(equalTo("'c'")));
    }
}
