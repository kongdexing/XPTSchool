package com.shuhai.anfang.report.http;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class HttpAction {

    private HttpAction() {
    }

    public static final String TIMER_RELOAD = "com.shuhai.report.reload";

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
    public static String APP_COUNT_INFO = SERVER_URL + "Counts/getAppCounts";
    //APP调用功能模块统计
    public static String APP_MODULE_COUNT_INFO = SERVER_URL + "Counts/getAppModuleCounts";
    //家长-老师聊天次数统计
    public static String APP_CHAT_INFO = SERVER_URL + "Counts/getChatCounts";


}
