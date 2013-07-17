package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 16/7/13
 * Time: 2:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataTypeTest {

    @Test
    public void considers_equal_if_names_are_same(){

        assertThat(new DataType("serial"), is(equalTo(new DataType("serial"))));
    }
}
