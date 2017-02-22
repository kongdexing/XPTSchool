package com.xptschool.parent.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONTACT_SCHOOL".
*/
public class ContactSchoolDao extends AbstractDao<ContactSchool, Void> {

    public static final String TABLENAME = "CONTACT_SCHOOL";

    /**
     * Properties of entity ContactSchool.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property S_id = new Property(0, String.class, "s_id", false, "S_ID");
        public final static Property S_name = new Property(1, String.class, "s_name", false, "S_NAME");
        public final static Property A_id = new Property(2, String.class, "a_id", false, "A_ID");
        public final static Property A_name = new Property(3, String.class, "a_name", false, "A_NAME");
        public final static Property Main_zrr = new Property(4, String.class, "main_zrr", false, "MAIN_ZRR");
        public final static Property Main_phone = new Property(5, String.class, "main_phone", false, "MAIN_PHONE");
        public final static Property Sub_zzr = new Property(6, String.class, "sub_zzr", false, "SUB_ZZR");
        public final static Property Sub_phone = new Property(7, String.class, "sub_phone", false, "SUB_PHONE");
        public final static Property Address = new Property(8, String.class, "address", false, "ADDRESS");
        public final static Property Tel = new Property(9, String.class, "tel", false, "TEL");
    };


    public ContactSchoolDao(DaoConfig config) {
        super(config);
    }
    
    public ContactSchoolDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONTACT_SCHOOL\" (" + //
                "\"S_ID\" TEXT," + // 0: s_id
                "\"S_NAME\" TEXT," + // 1: s_name
                "\"A_ID\" TEXT," + // 2: a_id
                "\"A_NAME\" TEXT," + // 3: a_name
                "\"MAIN_ZRR\" TEXT," + // 4: main_zrr
                "\"MAIN_PHONE\" TEXT," + // 5: main_phone
                "\"SUB_ZZR\" TEXT," + // 6: sub_zzr
                "\"SUB_PHONE\" TEXT," + // 7: sub_phone
                "\"ADDRESS\" TEXT," + // 8: address
                "\"TEL\" TEXT);"); // 9: tel
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONTACT_SCHOOL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ContactSchool entity) {
        stmt.clearBindings();
 
        String s_id = entity.getS_id();
        if (s_id != null) {
            stmt.bindString(1, s_id);
        }
 
        String s_name = entity.getS_name();
        if (s_name != null) {
            stmt.bindString(2, s_name);
        }
 
        String a_id = entity.getA_id();
        if (a_id != null) {
            stmt.bindString(3, a_id);
        }
 
        String a_name = entity.getA_name();
        if (a_name != null) {
            stmt.bindString(4, a_name);
        }
 
        String main_zrr = entity.getMain_zrr();
        if (main_zrr != null) {
            stmt.bindString(5, main_zrr);
        }
 
        String main_phone = entity.getMain_phone();
        if (main_phone != null) {
            stmt.bindString(6, main_phone);
        }
 
        String sub_zzr = entity.getSub_zzr();
        if (sub_zzr != null) {
            stmt.bindString(7, sub_zzr);
        }
 
        String sub_phone = entity.getSub_phone();
        if (sub_phone != null) {
            stmt.bindString(8, sub_phone);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(9, address);
        }
 
        String tel = entity.getTel();
        if (tel != null) {
            stmt.bindString(10, tel);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ContactSchool entity) {
        stmt.clearBindings();
 
        String s_id = entity.getS_id();
        if (s_id != null) {
            stmt.bindString(1, s_id);
        }
 
        String s_name = entity.getS_name();
        if (s_name != null) {
            stmt.bindString(2, s_name);
        }
 
        String a_id = entity.getA_id();
        if (a_id != null) {
            stmt.bindString(3, a_id);
        }
 
        String a_name = entity.getA_name();
        if (a_name != null) {
            stmt.bindString(4, a_name);
        }
 
        String main_zrr = entity.getMain_zrr();
        if (main_zrr != null) {
            stmt.bindString(5, main_zrr);
        }
 
        String main_phone = entity.getMain_phone();
        if (main_phone != null) {
            stmt.bindString(6, main_phone);
        }
 
        String sub_zzr = entity.getSub_zzr();
        if (sub_zzr != null) {
            stmt.bindString(7, sub_zzr);
        }
 
        String sub_phone = entity.getSub_phone();
        if (sub_phone != null) {
            stmt.bindString(8, sub_phone);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(9, address);
        }
 
        String tel = entity.getTel();
        if (tel != null) {
            stmt.bindString(10, tel);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public ContactSchool readEntity(Cursor cursor, int offset) {
        ContactSchool entity = new ContactSchool( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // s_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // s_name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // a_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // a_name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // main_zrr
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // main_phone
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // sub_zzr
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // sub_phone
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // address
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // tel
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ContactSchool entity, int offset) {
        entity.setS_id(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setS_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setA_id(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setA_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMain_zrr(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMain_phone(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSub_zzr(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSub_phone(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAddress(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTel(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(ContactSchool entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(ContactSchool entity) {
        return null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
