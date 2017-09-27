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

    //学生卡使用情况饼状图，柱状图
    public static String STU_CARD_PIE = SERVER_URL + "Counts/stuCardInfo";
    public static String STU_CARD_BAR = SERVER_URL + "Counts/stuCardAreaInfo";

    //进出校考勤
    public static String ATTENDANCE_INFO = SERVER_URL + "Counts/attendanceInfo";

    //用户数量统计
    public static String USER_COUNT_INFO = SERVER_URL + "Counts/userInfo";

}
