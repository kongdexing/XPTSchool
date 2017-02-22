package com.xptschool.teacher.bean;

/**
 * Created by dexing on 2016/12/12.
 * No1
 */

public enum ContactType {
    TEACHER("老师"),
    STUDENT("学生家长");

    private final String text;

    /**
     * @param text
     */
    private ContactType(final String text) {
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
