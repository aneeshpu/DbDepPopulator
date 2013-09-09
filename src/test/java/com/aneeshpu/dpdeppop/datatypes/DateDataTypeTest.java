package com.aneeshpu.dpdeppop.datatypes;

import org.junit.Test;

import static com.aneeshpu.dpdeppop.schema.Matchers.date;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DateDataTypeTest {

    @Test
    public void generates_a_date_in_string_form() {
        final DateDataType dateDataType = new DateDataType();
        final String generatedDate = dateDataType.generateDefaultValue();

        assertThat(generatedDate, is(date()));
    }

}
