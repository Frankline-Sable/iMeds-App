package com.fsdev.imeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Frankline Sable on 07/11/2017.
 */

public class Db_Forums extends DatabaseManager{
    private Context mContext;
    private Boolean deleteDb = false;

    public Db_Forums(Context mContext) {
        this.mContext = mContext;
    }

    public static abstract class struct_forums implements BaseColumns {
        public static final String TABLE_NAME = "imeds_forums";
        public static final String KEY_PID = "_id";
        public static final String KEY_TITLE = "forums_title";
        public static final String KEY_DESCRIPTION = "forums_description";
        public static final String KEY_SCHEDULED = "forum_schedule";
        public static final String KEY_IMAGE = "forum_image";
        public static final String KEY_UNIQUE = "forum_unique";
        public static final String KEY_ViewTime = "forum_viewTime";
        public static final String KEY_USERNAME= "forum_username";
        public static final String KEY_DELETED = "user_deleted";
        public static final String KEY_EMAIL= "forum_email";
        public static final String KEY_ISCOMMENT="is_comment";
        public static final String KEY_PREV_ID="prev_id";

    }

    private final static String CREATE_TABLE = "CREATE TABLE " + struct_forums.TABLE_NAME + " ("
            + struct_forums.KEY_PID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + struct_forums.KEY_TITLE + " TEXT NOT NULL, "
            + struct_forums.KEY_DESCRIPTION + " TEXT NOT NULL, "
            + struct_forums.KEY_EMAIL + " TEXT NOT NULL, "
            + struct_forums.KEY_SCHEDULED + " TEXT NOT NULL, "
            + struct_forums.KEY_IMAGE + " TEXT DEFAULT '', "
            + struct_forums.KEY_USERNAME + " TEXT NOT NULL, "
            + struct_forums.KEY_UNIQUE + " INT NOT NULL, "
            + struct_forums.KEY_PREV_ID + " INT NOT NULL, "
            + struct_forums.KEY_ISCOMMENT + " INT NOT NULL, "
            + struct_forums.KEY_ViewTime + " TEXT NOT NULL, "
            + Db_Events.struct_events.KEY_VIEWED  + " BOOLEAN DEFAULT FALSE, "
            + struct_forums.KEY_DELETED + " BOOLEAN DEFAULT FALSE)";

    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + struct_forums.TABLE_NAME;
    private SQLiteDatabase dbSQL;
    private DatabaseHelper dbHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "meds_forums.db";
        public static final String DROP_DB = "DROP DATABASE "+DATABASE_NAME;
        public static final int DATABASE_VERSION = 7;

        private SQLiteDatabase sd;

        public DatabaseHelper(Context context, Boolean dbDel) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            if (dbDel)
                onDelete();
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            sd=db;
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
        public void onDelete() {
            sd.execSQL(DROP_DB);
        }
    }

    public void open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext, deleteDb);
        dbSQL = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public long insertDb(String title, String desc, String imgURI, long unique, String schedule, String viewTime, String username,String email, int comment, long prevId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(struct_forums.KEY_TITLE, title);
        initialValues.put(struct_forums.KEY_DESCRIPTION, desc);
        initialValues.put(struct_forums.KEY_IMAGE, imgURI);
        initialValues.put(struct_forums.KEY_UNIQUE, unique);
        initialValues.put(struct_forums.KEY_SCHEDULED, schedule);
        initialValues.put(struct_forums.KEY_USERNAME, username);
        initialValues.put(struct_forums.KEY_ViewTime, viewTime);
        initialValues.put(struct_forums.KEY_EMAIL, email);
        initialValues.put(Db_Events.struct_events.KEY_VIEWED,false);
        initialValues.put(struct_forums.KEY_ISCOMMENT,comment);
        initialValues.put(struct_forums.KEY_PREV_ID, prevId);

        return dbSQL.insert(struct_forums.TABLE_NAME, null, initialValues);
    }

    public boolean updateDb(int id, String title, String desc, long unique, String schedule, String viewTime, String username, String email) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_forums.KEY_TITLE, title);
        updatedValues.put(struct_forums.KEY_DESCRIPTION, desc);
        updatedValues.put(struct_forums.KEY_SCHEDULED, schedule);
        updatedValues.put(struct_forums.KEY_USERNAME, username);
        updatedValues.put(struct_forums.KEY_ViewTime, viewTime);
        updatedValues.put(struct_forums.KEY_EMAIL, email);

        return dbSQL.update(struct_forums.TABLE_NAME, updatedValues, struct_forums.KEY_UNIQUE + " = " + unique, null) > 0;
    }

    public boolean updateImageDb(String email, String imgURI) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_forums.KEY_EMAIL, email);
        updatedValues.put(struct_forums.KEY_IMAGE, imgURI);

        return dbSQL.update(struct_forums.TABLE_NAME, updatedValues, struct_forums.KEY_EMAIL + " = \""+ email+"\"", null) > 0;
    }

    public Cursor readDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_forums.TABLE_NAME, new String[]{struct_forums.KEY_PID, struct_forums.KEY_TITLE, struct_forums.KEY_DESCRIPTION, struct_forums.KEY_IMAGE, struct_forums.KEY_UNIQUE,
                struct_forums.KEY_SCHEDULED, struct_forums.KEY_ViewTime, struct_forums.KEY_DELETED, struct_forums.KEY_USERNAME, struct_forums.KEY_EMAIL,struct_forums.KEY_ISCOMMENT, struct_forums.KEY_PREV_ID}, selection, selectionArgs, null, null, sortOrder, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor searchDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_forums.TABLE_NAME, new String[]{struct_forums.KEY_PID, struct_forums.KEY_TITLE, struct_forums.KEY_DESCRIPTION, struct_forums.KEY_IMAGE, struct_forums.KEY_UNIQUE,
                struct_forums.KEY_SCHEDULED,struct_forums.KEY_DELETED}, selection, selectionArgs, null, null, sortOrder, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Boolean updateWhetherViewed(long id, Boolean viewed) {

        ContentValues updatedValues = new ContentValues();
        //updatedValues.put(struct_forums.KEY_PID, id);
        updatedValues.put(Db_Events.struct_events.KEY_VIEWED , viewed);

        return dbSQL.update(struct_forums.TABLE_NAME, updatedValues, null, null) > 0;
    }
    public Cursor countDbViewed(String selection, String selectionArgs[]) {

        Cursor cursor = dbSQL.query(struct_forums.TABLE_NAME, new String[]{struct_forums.KEY_PID,  Db_Events.struct_events.KEY_VIEWED },  selection, selectionArgs, null,  null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    @Override
    public int clearTablesData() {
        return dbSQL.delete(struct_forums.TABLE_NAME,null,null);
    }
}
