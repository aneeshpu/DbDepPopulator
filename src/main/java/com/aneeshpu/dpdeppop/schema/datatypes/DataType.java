package com.aneeshpu.dpdeppop.schema.datatypes;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 16/7/13
 * Time: 2:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DataType<T> {

    T generateDefaultValue();

}
