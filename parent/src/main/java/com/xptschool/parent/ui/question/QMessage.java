package com.xptschool.parent.ui.question;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by dexing on 2017/5/4.
 * No1
 */

public class QMessage implements Serializable {

    private char type;
    private int size;
    private int parentId;
    private int teacherId;
    private char[] filename;
    private char[] zero = new char[2];
    private char[] filebuf;

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public char[] getFilename() {
        return filename;
    }

    public void setFilename(char[] filename) {
        this.filename = filename;
    }

    public char[] getZero() {
        return zero;
    }

    public void setZero(char[] zero) {
        this.zero = zero;
    }

    public char[] getFilebuf() {
        return filebuf;
    }

    public void setFilebuf(char[] filebuf) {
        this.filebuf = filebuf;
    }

    @Override
    public String toString() {
        return "QMessage{" +
                "type=" + type +
                ", size=" + size +
                ", parentId=" + parentId +
                ", teacherId=" + teacherId +
                ", filename=" + Arrays.toString(filename) +
                ", zero=" + Arrays.toString(zero) +
                ", filebuf=" + Arrays.toString(filebuf) +
                '}';
    }
}
