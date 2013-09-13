package com.aneeshpu.dpdeppop.query;

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
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryFactoryTest {

    @Mock
    private Record record;

    @Mock
    private Column primaryKeyColumn;

    @Test
    public void creates_instances_of_insert_query() throws SQLException {
        final Connection connection = new ConnectionFactory().getConnection();
        final Query insertQuery = new QueryFactory().generateInsertQuery(Collections.<String, Column>emptyMap(), Collections.<String, Map<String, Object>>emptyMap(), record, connection);
        assertNotNull(insertQuery);
    }

    @Test
    public void creates_instances_of_delete_query() throws SQLException {
        final Connection connection = new ConnectionFactory().getConnection();

        when(primaryKeyColumn.isAssigned()).thenReturn(true);

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Query deleteQueryQuery = new QueryFactory().generateDeleteQuery(primaryKeyColumn, preassignedValues, record, connection);

        assertNotNull(deleteQueryQuery);

        verify(primaryKeyColumn).isAssigned();
    }

    @Test
    public void creates_null_query_if_column_is_not_assigned() throws SQLException {

        final Connection connection = new ConnectionFactory().getConnection();
        when(primaryKeyColumn.isAssigned()).thenReturn(false);

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Query deleteQueryQuery = new QueryFactory().generateDeleteQuery(primaryKeyColumn, preassignedValues, record, connection);

        assertTrue(deleteQueryQuery.getClass().equals(NullQuery.class));

        verify(primaryKeyColumn).isAssigned();
    }
}
