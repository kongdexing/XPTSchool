package com.xptschool.teacher.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2016/11/2.
 */
public class GreenDaoHelper {

    private String TAG = GreenDaoHelper.class.getSimpleName();
    private static GreenDaoHelper mInstance = null;
    private SQLiteDatabase writeDB, readDB;
    private static DaoMaster writeDaoMaster, readDaoMaster;
    private static DaoSession writeDaoSession, readDaoSession;
    private BeanTeacher currentTeacher;

    private GreenDaoHelper() {
    }

    public static GreenDaoHelper getInstance() {
        synchronized (GreenDaoHelper.class) {
            if (mInstance == null) {
                mInstance = new GreenDaoHelper();
            }
        }
        if (writeDaoMaster != null) {
            writeDaoSession = writeDaoMaster.newSession();
        } else {
            writeDaoSession = null;
        }

        if (readDaoMaster != null) {
            readDaoSession = readDaoMaster.newSession();
        } else {
            readDaoSession = null;
        }

        return mInstance;
    }

    public void initGreenDao(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "xpt_teacher", null);

        writeDB = helper.getWritableDatabase();
        readDB = helper.getReadableDatabase();

        writeDaoMaster = new DaoMaster(writeDB);
        readDaoMaster = new DaoMaster(readDB);

    }

    public void clearData() {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanTeacherDao().deleteAll();
        }
    }

    /**
     * 删除后再插入
     */
    public void insertTeacher(BeanTeacher teacher) {
        currentTeacher = teacher;
        if (writeDaoSession != null) {
            writeDaoSession.getBeanTeacherDao().deleteAll();
            writeDaoSession.getBeanTeacherDao().insert(teacher);
        }
    }

    public BeanTeacher getCurrentTeacher() {
        if (currentTeacher == null) {
            if (readDaoSession != null) {
                List<BeanTeacher> listTeachers = readDaoSession.getBeanTeacherDao().loadAll();
                if (listTeachers.size() > 0) {
                    currentTeacher = listTeachers.get(0);
                }
            }
        }
        return currentTeacher;
    }

    /**
     * 执教班级写入数据库
     */
    public void insertClass(List<BeanClass> listClass) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanClassDao().deleteAll();
            writeDaoSession.getBeanClassDao().insertInTx(listClass);
        }
    }

    public List<BeanClass> getClassListByPId(String pId) {
        List<BeanClass> listClass = new ArrayList<BeanClass>();
        if (readDaoSession != null) {
            return readDaoSession.getBeanClassDao().queryBuilder().where(BeanClassDao.Properties.G_id.eq(pId)).build().list();
        }
        return listClass;
    }

    public BeanClass getClassById(String Id) {
        if (readDaoSession != null) {
            return readDaoSession.getBeanClassDao().queryBuilder().where(BeanClassDao.Properties.C_id.eq(Id)).build().unique();
        }
        return null;
    }

    /**
     * 获取全部班级(包含【全部】选项)
     *
     * @return
     */
    public List<BeanClass> getAllClassNameAppend() {
        List<BeanClass> classes = new ArrayList<>();
        BeanClass all = new BeanClass();
        all.setName("全部");
        all.setC_id("");
        all.setG_id("");
        classes.add(all);
        classes.addAll(getAllClass());
        return classes;
    }

    /**
     * 获取全部班级
     *
     * @return
     */
    public List<BeanClass> getAllClass() {
        List<BeanClass> classes = new ArrayList<>();
        if (readDaoSession != null) {
            classes = readDaoSession.getBeanClassDao().queryBuilder().orderAsc(BeanClassDao.Properties.G_id).list();
        }
        return classes;
    }

    public int getClassIndexByCId(String cId) {
        List<BeanClass> classes = getAllClass();
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getC_id().equals(cId)) {
                return i;
            }
        }
        return 0;
    }

    public String getClassNameById(String id) {
        if (readDaoSession != null) {
            BeanClass _class = readDaoSession.getBeanClassDao().queryBuilder().where(BeanClassDao.Properties.C_id.eq(id)).unique();
            if (_class != null) {
                return _class.getName();
            }
        }
        return "";
    }

    /**
     * 执教课程写入数据库
     */
    public void insertCourse(List<BeanCourse> listCourse) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanCourseDao().deleteAll();
            writeDaoSession.getBeanCourseDao().insertInTx(listCourse);
        }
    }

    /**
     * 获取全部课程(包含【全部】选项)
     *
     * @return
     */
    public List<BeanCourse> getAllCourseNameAppend() {
        List<BeanCourse> courses = new ArrayList<>();
        BeanCourse all = new BeanCourse();
        all.setId("");
        all.setName("全部");
        courses.add(all);
        courses.addAll(getAllCourse());
        return courses;
    }

    /**
     * 获取全部课程
     *
     * @return
     */
    public List<BeanCourse> getAllCourse() {
        List<BeanCourse> courses = new ArrayList<>();
        if (readDaoSession != null) {
            courses = readDaoSession.getBeanCourseDao().loadAll();
        }
        return courses;
    }

    public List<BeanCourse> getCourseByGId(String g_id) {
        List<BeanCourse> courses = new ArrayList<>();
        if (readDaoSession != null) {
            if (g_id.isEmpty() || g_id == null) {
                courses = readDaoSession.getBeanCourseDao().queryBuilder().list();
            } else {
                courses = readDaoSession.getBeanCourseDao().queryBuilder().where(BeanCourseDao.Properties.G_id.eq(g_id)).list();
            }

        }
        return courses;
    }

    public String getCourseNameById(String id) {
        if (readDaoSession != null) {
            BeanCourse course = readDaoSession.getBeanCourseDao().queryBuilder().where(BeanCourseDao.Properties.Id.eq(id)).unique();
            if (course != null) {
                return course.getName();
            }
        }
        return "";
    }

    public void deleteContacts() {
        if (writeDaoSession != null) {
            writeDaoSession.getContactTeacherDao().deleteAll();
            writeDaoSession.getContactStudentDao().deleteAll();
            writeDaoSession.getContactParentDao().deleteAll();
        }
    }

    //联系人
    public void insertContactTeacher(List<ContactTeacher> teachers) {
        if (writeDaoSession != null) {
            writeDaoSession.getContactTeacherDao().deleteAll();
            writeDaoSession.getContactTeacherDao().insertOrReplaceInTx(teachers);
        }
    }

    public void insertContactStudent(List<ContactStudent> students) {
        try {
            if (writeDaoSession != null) {
                writeDaoSession.getContactStudentDao().deleteAll();
                writeDaoSession.getContactStudentDao().insertOrReplaceInTx(students);
            }
        } catch (Exception ex) {
            Log.i(TAG, "insertContactStudent error: " + ex.getMessage());
        }
    }

    public void deleteParentData() {
        if (writeDaoSession != null) {
            writeDaoSession.getContactParentDao().deleteAll();
        }
    }

    public void insertContactParent(List<ContactParent> parents) {
        if (writeDaoSession != null) {
            writeDaoSession.getContactParentDao().insertOrReplaceInTx(parents);
        }
    }

    public List<ContactTeacher> getContactTeacher() {
        if (readDaoSession != null) {
            return readDaoSession.getContactTeacherDao().loadAll();
        }
        return new ArrayList<ContactTeacher>();
    }

    public List<ContactStudent> getContactStudent() {
        if (readDaoSession != null) {
            return readDaoSession.getContactStudentDao().loadAll();
        }
        return new ArrayList<ContactStudent>();
    }

    public List<ContactParent> getStudentParentBySId(String stu_id) {
        if (readDaoSession != null) {
            return readDaoSession.getContactParentDao().queryBuilder()
                    .where(ContactParentDao.Properties.Stu_id.eq(stu_id)).list();
        }
        return new ArrayList<ContactParent>();
    }

    public ContactParent getStudentParentByPUId(String pu_id) {
        ContactParent parent = null;
        if (readDaoSession != null) {
            parent = readDaoSession.getContactParentDao().queryBuilder()
                    .where(ContactParentDao.Properties.User_id.eq(pu_id)).limit(1).unique();
        }
        return parent;
    }

    public void insertBanner(List<BeanBanner> banners) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanBannerDao().deleteAll();
            writeDaoSession.getBeanBannerDao().insertInTx(banners);
        }
    }

    public List<BeanBanner> getBanners() {
        List<BeanBanner> banners = null;
        if (readDaoSession != null) {
            banners = readDaoSession.getBeanBannerDao().loadAll();
        }
        if (banners == null) {
            banners = new ArrayList<BeanBanner>();
        }
        return banners;
    }

    public boolean isExistChat(String chatId) {
        List<BeanChat> chats = new ArrayList<>();
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.ChatId.eq(chatId)).list();
        }
        return chats.size() > 0 ? true : false;
    }

    //聊天记录
    public void insertChat(BeanChat chat) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanChatDao().insertOrReplace(chat);
        }
    }

    public void updateChat(BeanChat chat) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanChatDao().update(chat);
        }
    }

    public void deleteChatByChatId(String chatId) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanChatDao().deleteByKey(chatId);
        }
    }

    public BeanChat getChatByChatId(String chatId) {
        if (readDaoSession != null) {
            return readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.ChatId.eq(chatId)).unique();
        }
        return null;
    }

    public void deleteChatByChat(BeanChat chat) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanChatDao().delete(chat);
        }
    }

    public int getChatCountByChatId(String chatId) {
        long chats = 0;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.ChatId.eq(chatId)).count();
        }
        return (int) chats;
    }

    /**
     * 根据家长id，获取跟此家长的聊天记录
     *
     * @param parentId
     * @return
     */
    public List<BeanChat> getChatsByParentId(String parentId) {
        List<BeanChat> chats = null;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.ParentId.eq(parentId),
                            BeanChatDao.Properties.TeacherId.eq(currentTeacher == null ? "" : currentTeacher.getU_id())).list();
        }
        if (chats == null) {
            chats = new ArrayList<BeanChat>();
        }
        return chats;
    }

    public List<BeanChat> getPageChatsByParentId(String parentId, int offset) {
        List<BeanChat> chats = null;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.ParentId.eq(parentId),
                            BeanChatDao.Properties.TeacherId.eq(currentTeacher == null ? "" : currentTeacher.getU_id()))
                    .orderDesc(BeanChatDao.Properties.MsgId)
                    .offset(offset)
                    .limit(15).list();
        }
        if (chats == null) {
            chats = new ArrayList<BeanChat>();
        }
        return chats;
    }

    /**
     * 读取当前账号未读消息个数
     *
     * @return
     */
    public List<BeanChat> getUnReadChats() {
        List<BeanChat> chats = null;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.HasRead.eq(false),
                            BeanChatDao.Properties.TeacherId.eq(currentTeacher == null ? "" : currentTeacher.getU_id())).list();
        }
        if (chats == null) {
            chats = new ArrayList<BeanChat>();
        }
        return chats;
    }

    public int getUnReadNumByParentId(String uId) {
        long chats = 0;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.HasRead.eq(false),
                            BeanChatDao.Properties.TeacherId.eq(currentTeacher == null ? "" : currentTeacher.getU_id()),
                            BeanChatDao.Properties.ParentId.eq(uId)).count();
        }
        return (int) chats;
    }
}
