package com.xptschool.parent.common;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class StringUtils {

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static String getParentPath(String path) {
        String parentPath = "";
        String[] paths = path.split("/");
        if (paths.length > 1) {
            parentPath = paths[paths.length - 2];
        }
        return parentPath;
    }

}
