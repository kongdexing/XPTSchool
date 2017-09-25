package com.shuhai.anfang.report.http;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class HttpAction {

    private HttpAction() {
    }

    public static final int SUCCESS = 1;
    public static final int FAILED = 0;

    private static String SERVER_URL = "http://school.xinpingtai.com/index.php/Api/";

    public static String STU_CARD_PIE = SERVER_URL + "Counts/stuCardInfo";

}
