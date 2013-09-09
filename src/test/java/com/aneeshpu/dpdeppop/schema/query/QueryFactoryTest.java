package com.aneeshpu.dpdeppop.schema.query;

import com.aneeshpu.dpdeppop.schema.Column;
import com.aneeshpu.dpdeppop.schema.Record;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class QueryFactoryTest {

    @Mock
    private Record record;

    @Mock
    private Column primaryKeyColumn;

    @Test
    public void creates_instances_of_insert_query() throws SQLException {
        final Query insertQuery = new QueryFactory(null).generateInsertQuery(Collections.<String, Column>emptyMap(), Collections.<String, Map<String, Object>>emptyMap(), record);
        assertNotNull(insertQuery);
    }

    @Test
    public void creates_instances_of_delete_query() throws SQLException {

        final Map<String, Map<String, Object>> preassignedValues = new HashMap<String, Map<String, Object>>();
        final Query deleteQueryQuery = new QueryFactory(null).generateDeleteQuery(primaryKeyColumn, preassignedValues, record);

        Assert.assertNotNull(deleteQueryQuery);
    }
}
