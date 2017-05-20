package com.xptschool.parent.http;

import com.xptschool.parent.BuildConfig;

/**
 * Created by Administrator on 2016/11/2.
 */
public class HttpAction {

    public static final int SUCCESS = 1;
    public static final int FAILED = 0;
    public static final String ACTION_LOGIN = "用户未登录请先登录";
    public static final String TOKEN_LOSE = "接口访问不合法";

    public static String Index = BuildConfig.SERVICE_URL + "/index.php";
    public static String HEAD = BuildConfig.SERVICE_URL + "/index.php/Api/";
    public static String LOGIN = HEAD + "Login";
    public static String UPDATE_PASSWORD = LOGIN + "/edit_password";

    public static String GetClass = HEAD + "Comment/get_class";
    public static String GetCourse = HEAD + "Comment/get_course";
    public static String GETExams = HEAD + "Comment/get_exam_name";
    //家庭作业
    public static String HOMEWORK_QUERY = HEAD + "Homework/query";
    public static String HOMEWORK_DEL = HEAD + "Homework/delete";
    public static String HOMEWORK_ADD = HEAD + "Homework/add";
    //班级公告
    public static String NOTICE_QUERY = HEAD + "Notice/query";
    public static String NOTICE_DEL = HEAD + "Notice/delete";
    public static String NOTICE_ADD = HEAD + "Notice/add";
    //在线提问
    public static String GET_TEACHER_BYCID = HEAD + "Comment/get_teacher_list";
    public static String QUESTION_QUERY = HEAD + "Question/query";
    public static String QUESTION_VIEW = HEAD + "Question/view";
    public static String QUESTION_DEL = HEAD + "Question/delete";
    public static String QUESTION_NEWADD = HEAD + "Question/new_add";
    public static String QUESTION_ADD = HEAD + "Question/add";
    //考试名称查询
    public static String EXAM_QUERY = HEAD + "Exam/getScoresForParent";
    //考勤管理
    public static String Attendance_QUERY = HEAD + "Attendance/query_v1_0_1";
    //请假管理
    public static String Leave_QUERY = HEAD + "Leave/query";
    public static String Leave_Add = HEAD + "Leave/add_leave";
    public static String Leave_Del = HEAD + "Leave/del";
    //课程表
    public static String Timetable_QUERY = HEAD + "Timetable/query";
    //报警记录
    public static String Track_alarm = HEAD + "Track/alarmRecord_v1_0_1";
    //我的班级
    public static String MyClass_QUERY = HEAD + "MyClass/query";
    //我的学生
    public static String MyStudent_QUERY = HEAD + "MyStudent/query";
    //学生详情
    public static String MyStudent_Detail = HEAD + "MyStudent/view";
    //通讯录
    public static String MyContacts_QUERY = HEAD + "Contacts/getContactsForParent";
    //位置
    public static String Track_RealTime = HEAD + "Track/realtimeLocation";
    public static String Track_HistoryTrack = HEAD + "Track/historyTrack";
    //围栏
    public static String Track_StudentFence = HEAD + "Track/studentFence";
    public static String Track_addStudentFence = HEAD + "Track/addStudentFence";
    public static String Track_deleteStudentFence = HEAD + "Track/deleteStudentFence";

    //报警处理
    public static String Track_Alarm_edit = HEAD + "Track/alarm_edit";

    public static String GetCard_Phone = HEAD + "StudentCard/get_cardphone";
    public static String SetCard_Phone = HEAD + "StudentCard/set_cardphone";

    //上传upush devices_token
    public static String Push_Token = BuildConfig.SERVICE_URL + "/parenttoken.php";
    public static String Push_Token_ForChat = BuildConfig.SERVICE_URL + "/chattoken.php";

    //广告位
    public static String HOME_Banner = HEAD + "Banner/query";
    public static String SHOW_Banner = BuildConfig.SERVICE_URL + "/getadstatics.php";

    //添加监护人
    public static String ADD_TUTELAGE = HEAD + "AddGuardian/add";

    //钱包
    public static String Learning_Server = HEAD + "Learning/query";
    //零钱
    public static String POCKET_BALANCE = HEAD + "OrderBalance/queryAccount";
    //零钱明细记录
    public static String POCKET_BILLS = HEAD + "Order/getBillList";
    public static String POCKET_BILL_DETAIL = HEAD + "Order/getBillDetail";

    //提现
    public static String REFUND_ADD = HEAD + "UserRefund/addRefund";
    public static String REFUND_Query = HEAD + "UserRefund/query";

    public static String STU_CARD_ORDERID = HEAD + "OrderBalance/getCardOrderId";
    //学生卡充值
    public static String STU_CARD_RECHARGE = HEAD + "OrderBalance/onCardRecharge";

    //学生卡冻结/解冻
    public static String STU_CARD_FREEZE = HEAD + "OrderBalance/onFreezeChange";
    //学生卡消费记录
    public static String STU_CARD_BILL = HEAD + "OrderBalance/getStuCardBill";

    //获取订单信息
    public static String GET_OrderInfo = HEAD + "Order/getOrder";

    //银行卡
    public static String GET_BankCards = HEAD + "BankCard/query";
    public static String Add_BankCard = HEAD + "BankCard/add";
    public static String Delete_BankCard = HEAD + "BankCard/del";

    //手机话费充值
    public static String GET_TEL_RECHARGE_ORDER = HEAD + "TelCharge/getTelCharge";
    public static String TEL_RECHARGE = HEAD + "TelCharge/telTopUp";

}
