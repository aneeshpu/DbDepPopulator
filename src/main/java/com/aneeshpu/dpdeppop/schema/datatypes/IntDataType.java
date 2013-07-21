package com.aneeshpu.dpdeppop.schema.datatypes;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
class IntDataType implements DataType<Integer> {
    private final String name;

    public IntDataType(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.valueOf(new Random().nextInt());
    }

    @Override
    public Integer generateDefaultValue() {
        return Integer.parseInt(toString());
    }
}
