package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 8:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class NameValueTest {

    @Test
    public void two_name_values_with_same_name_and_values_are_equal(){

        final NameValue amount = new NameValue("amount", "10");
        final NameValue anotherNameValue = new NameValue("amount", "10");

        assertThat(amount, is(equalTo(anotherNameValue)));

    }

    @Test
    public void string_representation_contains_both_name_and_value(){
        final NameValue amount = new NameValue("amount", "10");
        assertThat(amount.toString(), is(equalTo("amount=10")));
    }
}
