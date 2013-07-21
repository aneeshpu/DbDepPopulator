package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class YesNoTest {

    @Test
    public void returns_true_for_yes(){
        assertThat(new YesNo("YES").isTrue(), is(equalTo(true)));
    }

    @Test
    public void returns_false_for_no(){
        assertThat(new YesNo("NO").isTrue(), is(equalTo(false)));
    }
}