package com.xptschool.parent.model;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.xptschool.parent.model.BeanBanner;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanLearningModule;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.ContactParent;
import com.xptschool.parent.model.ContactSchool;
import com.xptschool.parent.model.ContactTeacher;

import com.xptschool.parent.model.BeanBannerDao;
import com.xptschool.parent.model.BeanChatDao;
import com.xptschool.parent.model.BeanLearningModuleDao;
import com.xptschool.parent.model.BeanParentDao;
import com.xptschool.parent.model.BeanStudentDao;
import com.xptschool.parent.model.ContactParentDao;
import com.xptschool.parent.model.ContactSchoolDao;
import com.xptschool.parent.model.ContactTeacherDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig beanBannerDaoConfig;
    private final DaoConfig beanChatDaoConfig;
    private final DaoConfig beanLearningModuleDaoConfig;
    private final DaoConfig beanParentDaoConfig;
    private final DaoConfig beanStudentDaoConfig;
    private final DaoConfig contactParentDaoConfig;
    private final DaoConfig contactSchoolDaoConfig;
    private final DaoConfig contactTeacherDaoConfig;

    private final BeanBannerDao beanBannerDao;
    private final BeanChatDao beanChatDao;
    private final BeanLearningModuleDao beanLearningModuleDao;
    private final BeanParentDao beanParentDao;
    private final BeanStudentDao beanStudentDao;
    private final ContactParentDao contactParentDao;
    private final ContactSchoolDao contactSchoolDao;
    private final ContactTeacherDao contactTeacherDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        beanBannerDaoConfig = daoConfigMap.get(BeanBannerDao.class).clone();
        beanBannerDaoConfig.initIdentityScope(type);

        beanChatDaoConfig = daoConfigMap.get(BeanChatDao.class).clone();
        beanChatDaoConfig.initIdentityScope(type);

        beanLearningModuleDaoConfig = daoConfigMap.get(BeanLearningModuleDao.class).clone();
        beanLearningModuleDaoConfig.initIdentityScope(type);

        beanParentDaoConfig = daoConfigMap.get(BeanParentDao.class).clone();
        beanParentDaoConfig.initIdentityScope(type);

        beanStudentDaoConfig = daoConfigMap.get(BeanStudentDao.class).clone();
        beanStudentDaoConfig.initIdentityScope(type);

        contactParentDaoConfig = daoConfigMap.get(ContactParentDao.class).clone();
        contactParentDaoConfig.initIdentityScope(type);

        contactSchoolDaoConfig = daoConfigMap.get(ContactSchoolDao.class).clone();
        contactSchoolDaoConfig.initIdentityScope(type);

        contactTeacherDaoConfig = daoConfigMap.get(ContactTeacherDao.class).clone();
        contactTeacherDaoConfig.initIdentityScope(type);

        beanBannerDao = new BeanBannerDao(beanBannerDaoConfig, this);
        beanChatDao = new BeanChatDao(beanChatDaoConfig, this);
        beanLearningModuleDao = new BeanLearningModuleDao(beanLearningModuleDaoConfig, this);
        beanParentDao = new BeanParentDao(beanParentDaoConfig, this);
        beanStudentDao = new BeanStudentDao(beanStudentDaoConfig, this);
        contactParentDao = new ContactParentDao(contactParentDaoConfig, this);
        contactSchoolDao = new ContactSchoolDao(contactSchoolDaoConfig, this);
        contactTeacherDao = new ContactTeacherDao(contactTeacherDaoConfig, this);

        registerDao(BeanBanner.class, beanBannerDao);
        registerDao(BeanChat.class, beanChatDao);
        registerDao(BeanLearningModule.class, beanLearningModuleDao);
        registerDao(BeanParent.class, beanParentDao);
        registerDao(BeanStudent.class, beanStudentDao);
        registerDao(ContactParent.class, contactParentDao);
        registerDao(ContactSchool.class, contactSchoolDao);
        registerDao(ContactTeacher.class, contactTeacherDao);
    }
    
    public void clear() {
        beanBannerDaoConfig.getIdentityScope().clear();
        beanChatDaoConfig.getIdentityScope().clear();
        beanLearningModuleDaoConfig.getIdentityScope().clear();
        beanParentDaoConfig.getIdentityScope().clear();
        beanStudentDaoConfig.getIdentityScope().clear();
        contactParentDaoConfig.getIdentityScope().clear();
        contactSchoolDaoConfig.getIdentityScope().clear();
        contactTeacherDaoConfig.getIdentityScope().clear();
    }

    public BeanBannerDao getBeanBannerDao() {
        return beanBannerDao;
    }

    public BeanChatDao getBeanChatDao() {
        return beanChatDao;
    }

    public BeanLearningModuleDao getBeanLearningModuleDao() {
        return beanLearningModuleDao;
    }

    public BeanParentDao getBeanParentDao() {
        return beanParentDao;
    }

    public BeanStudentDao getBeanStudentDao() {
        return beanStudentDao;
    }

    public ContactParentDao getContactParentDao() {
        return contactParentDao;
    }

    public ContactSchoolDao getContactSchoolDao() {
        return contactSchoolDao;
    }

    public ContactTeacherDao getContactTeacherDao() {
        return contactTeacherDao;
    }

}
