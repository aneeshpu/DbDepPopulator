package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void generates_formatted_column_name_string(){
        final NameValue amount = new NameValue("amount", "10");
        assertThat(amount.formattedName(), is(equalTo("\"amount\",")));
    }


    @Test
    public void is_not_assigned_if_value_is_not_set(){
        final NameValue amount = new NameValue("amount", null);
        assertFalse(amount.isAssigned());

    }

    @Test
    public void is_not_assigned_if_name_is_not_set(){
        final NameValue amount = new NameValue(null, 10);
        assertFalse(amount.isAssigned());
    }

    @Test
    public void is_assigned_if_name_and_value_are_set(){
        final NameValue amount = new NameValue("amount", 10);
        assertTrue(amount.isAssigned());

    }
}