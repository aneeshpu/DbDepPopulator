package com.aneeshpu.dpdeppop.schema;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

public class RecordFactoryTest {

    private Connection connection;
    private HashMap<String,Map<String,Object>> preassignedValues;

    @Before
    public void setup() throws SQLException {
        preassignedValues = new HashMap<String,Map<String,Object>>();
        final Map<String, Object> columnValues = new HashMap<String, Object>();
        columnValues.put("status", "2");
        preassignedValues.put("payment", columnValues);
    }

    @Test
    public void creates_an_instance_of_a_record(){
        final Record record = RecordFactory.createRecordWithAutoIncrementBasedCreation("account", connection, preassignedValues);
        assertNotNull(record);
    }
}
