package com.aneeshpu.dpdeppop.schema;

/**
 * Created with IntelliJ IDEA.
 * User: aneeshpu
 * Date: 16/7/13
 * Time: 2:45 AM
 * To change this template use File | Settings | File Templates.
 */
class YesNo {
    public static final String YES = "YES";
    public static final String NO = "NO";

    private final String yesNo;

    public YesNo(final String yesNo) {
        this.yesNo = yesNo;
    }

    public boolean isTrue() {
        return "YES".equalsIgnoreCase(yesNo);
    }

    public static YesNo Yes() {
        return new YesNo("YES");
    }

    public static YesNo No() {
        return new YesNo("NO");
    }
}
