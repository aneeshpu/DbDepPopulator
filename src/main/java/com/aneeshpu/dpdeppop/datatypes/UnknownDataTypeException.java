package com.aneeshpu.dpdeppop.datatypes;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 21/7/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnknownDataTypeException extends RuntimeException{
    public UnknownDataTypeException(final String message) {
        super(message);
    }
}
