package com.aneeshpu.dpdeppop.schema;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 16/7/13
 * Time: 2:45 AM
 * To change this template use File | Settings | File Templates.
 */
class YesNo {
    private final String yesNo;

    public YesNo(final String yesNo) {
        this.yesNo = yesNo;
    }

    public boolean isTrue() {
        return "YES".equalsIgnoreCase(yesNo);
    }
}
