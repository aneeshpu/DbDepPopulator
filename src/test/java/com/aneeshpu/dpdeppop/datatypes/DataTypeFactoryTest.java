package com.aneeshpu.dpdeppop.datatypes;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class DataTypeFactoryTest {
    @Test
    public void creates_serial_type(){

        final DataType anInt = new DataTypeFactory().create("int");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<IntDataType> obj = IntDataType.class;

        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_float_type(){
        final DataType anInt = new DataTypeFactory().create("float");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<FloatDataType> obj = FloatDataType.class;

        assertTrue(aClass.equals(obj));

    }

    @Test
    public void creates_a_float_type_for_money(){
        final DataType anInt = new DataTypeFactory().create("money");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<FloatDataType> obj = FloatDataType.class;

        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_an_int_type_for_integer(){

        final DataType anInt = new DataTypeFactory().create("integer");
        assertNotNull(anInt);
        final Class<? extends DataType> aClass = anInt.getClass();
        final Class<IntDataType> obj = IntDataType.class;
        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_char_type_for_character(){
        final DataType character = new DataTypeFactory().create("character");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<CharacterDataType> obj = CharacterDataType.class;
        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_char_type_for_varchar(){
        final DataType character = new DataTypeFactory().create("varchar");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<CharacterDataType> obj = CharacterDataType.class;
        assertTrue(aClass.equals(obj));
    }

    @Test
    public void creates_a_char_type_for_text(){
        final DataType character = new DataTypeFactory().create("text");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<CharacterDataType> obj = CharacterDataType.class;
        assertTrue(aClass.equals(obj));

    }

    @Test
    public void creates_a_serial_type_for_serial(){
        final DataType character = new DataTypeFactory().create("serial");
        assertNotNull(character);
        final Class<? extends DataType> aClass = character.getClass();
        final Class<SerialDataType> obj = SerialDataType.class;
        assertTrue(aClass.equals(obj));

    }

    @Test
    public void creates_bool_data_type(){
        final DataType bool = new DataTypeFactory().create("bool");
        assertNotNull(bool);
        final Class<? extends DataType> aClass = bool.getClass();
        final Class<BoolDataType> obj = BoolDataType.class;
        assertTrue(aClass.equals(obj));

    }

    @Test(expected = UnknownDataTypeException.class)
    public void throws_exception_for_unknown_data_types(){
        new DataTypeFactory().create("foo");
    }


}
