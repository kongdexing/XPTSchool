package com.xptschool.parent.model;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.xptschool.parent.model.BeanBanner;
import com.xptschool.parent.model.BeanDeviceToken;
import com.xptschool.parent.model.BeanLearningModule;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.ContactParent;
import com.xptschool.parent.model.ContactSchool;
import com.xptschool.parent.model.ContactTeacher;

import com.xptschool.parent.model.BeanBannerDao;
import com.xptschool.parent.model.BeanDeviceTokenDao;
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
    private final DaoConfig beanDeviceTokenDaoConfig;
    private final DaoConfig beanLearningModuleDaoConfig;
    private final DaoConfig beanParentDaoConfig;
    private final DaoConfig beanStudentDaoConfig;
    private final DaoConfig contactParentDaoConfig;
    private final DaoConfig contactSchoolDaoConfig;
    private final DaoConfig contactTeacherDaoConfig;

    private final BeanBannerDao beanBannerDao;
    private final BeanDeviceTokenDao beanDeviceTokenDao;
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

        beanDeviceTokenDaoConfig = daoConfigMap.get(BeanDeviceTokenDao.class).clone();
        beanDeviceTokenDaoConfig.initIdentityScope(type);

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
        beanDeviceTokenDao = new BeanDeviceTokenDao(beanDeviceTokenDaoConfig, this);
        beanLearningModuleDao = new BeanLearningModuleDao(beanLearningModuleDaoConfig, this);
        beanParentDao = new BeanParentDao(beanParentDaoConfig, this);
        beanStudentDao = new BeanStudentDao(beanStudentDaoConfig, this);
        contactParentDao = new ContactParentDao(contactParentDaoConfig, this);
        contactSchoolDao = new ContactSchoolDao(contactSchoolDaoConfig, this);
        contactTeacherDao = new ContactTeacherDao(contactTeacherDaoConfig, this);

        registerDao(BeanBanner.class, beanBannerDao);
        registerDao(BeanDeviceToken.class, beanDeviceTokenDao);
        registerDao(BeanLearningModule.class, beanLearningModuleDao);
        registerDao(BeanParent.class, beanParentDao);
        registerDao(BeanStudent.class, beanStudentDao);
        registerDao(ContactParent.class, contactParentDao);
        registerDao(ContactSchool.class, contactSchoolDao);
        registerDao(ContactTeacher.class, contactTeacherDao);
    }
    
    public void clear() {
        beanBannerDaoConfig.getIdentityScope().clear();
        beanDeviceTokenDaoConfig.getIdentityScope().clear();
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

    public BeanDeviceTokenDao getBeanDeviceTokenDao() {
        return beanDeviceTokenDao;
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
