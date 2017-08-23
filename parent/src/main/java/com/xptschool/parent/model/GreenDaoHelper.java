package com.xptschool.parent.model;

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
    private BeanParent currentParent;

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
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "xpt_parent", null);

        writeDB = helper.getWritableDatabase();
        readDB = helper.getReadableDatabase();

        writeDaoMaster = new DaoMaster(writeDB);
        readDaoMaster = new DaoMaster(readDB);
    }

    public void clearData() {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanParentDao().deleteAll();
            writeDaoSession.getContactSchoolDao().deleteAll();
            writeDaoSession.getContactTeacherDao().deleteAll();
            writeDaoSession.getBeanStudentDao().deleteAll();
            writeDaoSession.getContactParentDao().deleteAll();
        }
    }

    /**
     * 删除后再插入
     */
    public void insertParent(BeanParent parent) {
        currentParent = parent;
        if (writeDaoSession != null) {
            writeDaoSession.getBeanParentDao().deleteAll();
            writeDaoSession.getBeanParentDao().insert(parent);
        }
    }

    public void insertStudent(List<BeanStudent> students) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanStudentDao().deleteAll();
            for (int i = 0; i < students.size(); i++) {
                Log.i(TAG, "insertStudent: " + students.get(i).toString());
            }

            writeDaoSession.getBeanStudentDao().insertInTx(students);
        }
    }

    public BeanParent getCurrentParent() {
        if (currentParent == null) {
            if (readDaoSession != null) {
                List<BeanParent> beanParents = readDaoSession.getBeanParentDao().loadAll();
                if (beanParents.size() > 0) {
                    currentParent = beanParents.get(0);
                }
            }
        }
        return currentParent;
    }

    public List<BeanStudent> getStudents() {
        if (readDaoSession != null) {
            return readDaoSession.getBeanStudentDao().queryBuilder()
                    .orderAsc(BeanStudentDao.Properties.Stu_name).list();
        }
        return new ArrayList<BeanStudent>();
    }

    public BeanStudent getStudentByStuId(String stuId) {
        return readDaoSession.getBeanStudentDao().queryBuilder().where(BeanStudentDao.Properties.Stu_id.eq(stuId)).limit(1).unique();
    }

    public void updateStudent(BeanStudent student) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanStudentDao().update(student);
        }
    }

    //联系人
    public void insertContactTeacher(List<ContactTeacher> teachers) {
        if (writeDaoSession != null) {
            writeDaoSession.getContactTeacherDao().deleteAll();
            writeDaoSession.getContactTeacherDao().insertOrReplaceInTx(teachers);
        }
    }

    public void deleteContact() {
        if (writeDaoSession != null) {
            writeDaoSession.getContactTeacherDao().deleteAll();
            writeDaoSession.getContactSchoolDao().deleteAll();
        }
    }

    public List<ContactTeacher> getContactTeacher() {
        if (readDaoSession != null) {
            return readDaoSession.getContactTeacherDao().loadAll();
        }
        return new ArrayList<ContactTeacher>();
    }

    public ContactTeacher getContactByTeacher(String t_u_id) {
        if (readDaoSession != null) {
            return readDaoSession.getContactTeacherDao().queryBuilder()
                    .where(ContactTeacherDao.Properties.U_id.eq(t_u_id)).limit(1).unique();
        }
        return null;
    }

    public void insertSchoolInfo(List<ContactSchool> schools) {
        if (writeDaoSession != null) {
            writeDaoSession.getContactSchoolDao().deleteAll();
            writeDaoSession.getContactSchoolDao().insertOrReplaceInTx(schools);
        }
    }

    public List<ContactSchool> getSchoolInfo() {
        if (readDaoSession != null) {
            return readDaoSession.getContactSchoolDao().loadAll();
        }
        return new ArrayList<ContactSchool>();
    }

    public void insertBanner(List<BeanBanner> banners) {
        if (writeDaoSession != null) {
            writeDaoSession.getBeanBannerDao().deleteAll();
            writeDaoSession.getBeanBannerDao().insertOrReplaceInTx(banners);
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

    /**
     * 根据老师id，获取跟此老师的聊天记录
     *
     * @param teacherId
     * @return
     */
    public List<BeanChat> getChatsByTeacherId(String teacherId) {
        List<BeanChat> chats = null;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.TeacherId.eq(teacherId),
                            BeanChatDao.Properties.ParentId.eq(currentParent == null ? "" : currentParent.getU_id())).list();
        }
        if (chats == null) {
            chats = new ArrayList<BeanChat>();
        }
        return chats;
    }

    public List<BeanChat> getPageChatsByTeacherId(String teacherId, int offset) {
        List<BeanChat> chats = null;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.TeacherId.eq(teacherId),
                            BeanChatDao.Properties.ParentId.eq(currentParent == null ? "" : currentParent.getU_id()))
                    .orderDesc(BeanChatDao.Properties.Time)
                    .offset(offset)
                    .limit(15).list();
        }
        if (chats == null) {
            chats = new ArrayList<BeanChat>();
        }
        return chats;
    }

    public int getChatCountByChatId(String chatId) {
        long chats = 0;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.ChatId.eq(chatId)).count();
        }
        return (int) chats;
    }

    public List<BeanChat> getUnReadChats() {
        List<BeanChat> chats = null;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.HasRead.eq(false),
                            BeanChatDao.Properties.ParentId.eq(currentParent == null ? "" : currentParent.getU_id())).list();
        }
        if (chats == null) {
            chats = new ArrayList<BeanChat>();
        }
        return chats;
    }

    public int getUnReadNumByTeacherId(String tuId) {
        long chats = 0;
        if (readDaoSession != null) {
            chats = readDaoSession.getBeanChatDao().queryBuilder()
                    .where(BeanChatDao.Properties.HasRead.eq(false),
                            BeanChatDao.Properties.ParentId.eq(currentParent == null ? "" : currentParent.getU_id()),
                            BeanChatDao.Properties.TeacherId.eq(tuId)).count();
        }
        return (int) chats;
    }

}