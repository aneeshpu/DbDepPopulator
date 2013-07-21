package com.aneeshpu.dpdeppop.schema.datatypes;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataTypeFactoryTest {
    @Test
    public void creates_serial_type(){

        final DataType anInt = DataTypeFactory.create("int");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<IntDataType> obj = IntDataType.class;

        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_float_type(){
        final DataType anInt = DataTypeFactory.create("float");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<FloatDataType> obj = FloatDataType.class;

        assertTrue(aClass.equals(obj));

    }

    @Test
    public void creates_a_float_type_for_money(){
        final DataType anInt = DataTypeFactory.create("money");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<FloatDataType> obj = FloatDataType.class;

        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_an_int_type_for_integer(){

        final DataType anInt = DataTypeFactory.create("integer");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<IntDataType> obj = IntDataType.class;
        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_char_type_for_character(){
        final DataType character = DataTypeFactory.create("character");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<CharacterDataType> obj = CharacterDataType.class;
        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_char_type_for_varchar(){
        final DataType character = DataTypeFactory.create("varchar");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<CharacterDataType> obj = CharacterDataType.class;
        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_char_type_for_text(){
        final DataType character = DataTypeFactory.create("text");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<CharacterDataType> obj = CharacterDataType.class;
        assertTrue(aClass.equals(obj));

    }

    @Test
    public void creates_a_serial_type_for_serial(){
        final DataType character = DataTypeFactory.create("serial");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<SerialDataType> obj = SerialDataType.class;
        assertTrue(aClass.equals(obj));

    }

    @Test(expected = UnknownDataTypeException.class)
    public void throws_exception_for_unknown_data_types(){
        DataTypeFactory.create("foo");
    }


}
