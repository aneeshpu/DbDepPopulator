package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.ConnectionFactory;
import com.aneeshpu.dpdeppop.schema.Record;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class QueryFactoryTest {


    @Mock
    private Record record;

    @Test
    public void creates_instances_of_insert_query() throws SQLException {

        final Connection connection = new ConnectionFactory().getConnection();
        final Query insertQuery = new QueryFactory(connection).generateInsertQuery(Collections.<String, Column>emptyMap(), Collections.<String, Map<String, Object>>emptyMap(), record);
        assertNotNull(insertQuery);
    }
}
