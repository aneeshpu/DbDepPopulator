package com.aneeshpu.dpdeppop.schema;

import com.aneeshpu.dpdeppop.datatypes.DataTypeFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RecordBuilder.class})
public class RecordBuilderTest {

    @Mock
    private Connection connection;

    @Mock
    private DataTypeFactory datatypeFactory;

    @Mock
    private DoNotGeneratePrimaryKeys doNotGeneratePrimaryKeys;

    @Test(expected = InvalidRecordException.class)
    public void throws_exception_if_name_is_not_set(){

//        final Connection connection = new ConnectionFactory().getConnection();
        new RecordBuilder(connection).createRecord();
    }

    @Test
    public void creates_a_column_creation_strategy_with_injected_dataTypeFactory() throws Exception {

        PowerMockito.whenNew(DoNotGeneratePrimaryKeys.class).withArguments(connection, datatypeFactory).thenReturn(doNotGeneratePrimaryKeys);

        new RecordBuilder(connection).withDataTypeFactory(datatypeFactory);

        PowerMockito.verifyNew(DoNotGeneratePrimaryKeys.class).withArguments(connection, datatypeFactory);
    }

}
