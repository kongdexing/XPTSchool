package com.xptschool.teacher.bean;

/**
 * Created by dexing on 2017/1/13.
 * No1
 */

public enum ExamType {

    //1 单考,2统考
    SINGLE("1"),
    ALL("2");

    private final String text;

    /**
     * @param text
     */
    private ExamType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
