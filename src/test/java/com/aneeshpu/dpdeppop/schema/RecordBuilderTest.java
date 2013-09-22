package com.aneeshpu.dpdeppop.schema;

import org.junit.Test;

import java.sql.Connection;

public class RecordBuilderTest {

    @Test(expected = InvalidRecordException.class)
    public void throws_exception_if_name_is_not_set(){

        final Connection connection = new ConnectionFactory().getConnection();
        new RecordBuilder(connection).createRecord();
    }
}
