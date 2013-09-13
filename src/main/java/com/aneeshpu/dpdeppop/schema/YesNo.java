package com.aneeshpu.dpdeppop.schema;

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
