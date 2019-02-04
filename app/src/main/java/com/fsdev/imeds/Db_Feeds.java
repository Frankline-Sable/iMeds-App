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

public class Db_Feeds extends DatabaseManager{
    private Context mContext;
    private Boolean deleteDb = false;

    public Db_Feeds(Context mContext) {
        this.mContext = mContext;
    }

    public static abstract class struct_feeds implements BaseColumns {
        public static final String TABLE_NAME = "imeds_feeds";
        public static final String KEY_PID = "pid";
        public static final String KEY_UNIQUE = "feed_id";
        public static final String KEY_TITLE = "feeds_title";
        public static final String KEY_DESCRIPTION = "feeds_description";
        public static final String KEY_SCHEDULED = "feed_schedule";
        public static final String KEY_IMAGE = "feed_image";
        public static final String KEY_URL = "feed_alarm";
        public static final String KEY_ViewTime = "feed_viewTime";
        //public static final String KEY_MONTH = "feed_month";
        public static final String KEY_DELETED = "user_deleted";

    }

    private final static String CREATE_TABLE = "CREATE TABLE " + struct_feeds.TABLE_NAME + " ("
            + struct_feeds.KEY_PID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + struct_feeds.KEY_UNIQUE + " INTEGER NOT NULL, "
            + struct_feeds.KEY_TITLE + " TEXT NOT NULL, "
            + struct_feeds.KEY_DESCRIPTION + " TEXT NOT NULL, "
            + struct_feeds.KEY_SCHEDULED + " TEXT NOT NULL, "
            + struct_feeds.KEY_IMAGE + " TEXT NOT NULL, "
            // + struct_feeds.KEY_MONTH + " TEXT NOT NULL, "
            + struct_feeds.KEY_URL + " TEXT NOT NULL, "
            + struct_feeds.KEY_ViewTime + " TEXT NOT NULL, "
            + Db_Events.struct_events.KEY_VIEWED + " BOOLEAN DEFAULT FALSE, "
            + struct_feeds.KEY_DELETED + " BOOLEAN DEFAULT FALSE)";

    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + struct_feeds.TABLE_NAME;
    private SQLiteDatabase dbSQL;
    private DatabaseHelper dbHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "meds_feeds.db";
        public static final String DROP_DB = "DROP DATABASE " + DATABASE_NAME;
        public static final int DATABASE_VERSION = 6;
        private SQLiteDatabase sd;

        public DatabaseHelper(Context context, Boolean dbDel) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            if (dbDel)
                onDelete();
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            sd = db;
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

    @Override
    public void open() {
        dbHelper = new DatabaseHelper(mContext, deleteDb);
        dbSQL = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public long insertDb(String title, String desc, String imgURI, String url, String schedule, String viewTime, long uniqueId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(struct_feeds.KEY_TITLE, title);
        initialValues.put(struct_feeds.KEY_DESCRIPTION, desc);
        initialValues.put(struct_feeds.KEY_IMAGE, imgURI);
        initialValues.put(struct_feeds.KEY_URL, url);
        initialValues.put(struct_feeds.KEY_SCHEDULED, schedule);
        initialValues.put(struct_feeds.KEY_UNIQUE, uniqueId);
        initialValues.put(struct_feeds.KEY_ViewTime, viewTime);
        initialValues.put(Db_Events.struct_events.KEY_VIEWED,false);

        long id = dbSQL.insert(struct_feeds.TABLE_NAME, null, initialValues);
        return id;
    }

    public boolean updateDb(String title, String desc, String url, String schedule, String viewTime, long uniqueId) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_feeds.KEY_TITLE, title);
        updatedValues.put(struct_feeds.KEY_DESCRIPTION, desc);
        updatedValues.put(struct_feeds.KEY_URL, url);
        updatedValues.put(struct_feeds.KEY_SCHEDULED, schedule);
        updatedValues.put(struct_feeds.KEY_UNIQUE, uniqueId);
        updatedValues.put(struct_feeds.KEY_ViewTime, viewTime);

        return dbSQL.update(struct_feeds.TABLE_NAME, updatedValues, struct_feeds.KEY_UNIQUE + " = " + uniqueId, null) > 0;
    }

    public boolean updateImageDb(long uniqueId, String imgURI) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_feeds.KEY_UNIQUE, uniqueId);
        updatedValues.put(struct_feeds.KEY_IMAGE, imgURI);

        return dbSQL.update(struct_feeds.TABLE_NAME, updatedValues, struct_feeds.KEY_UNIQUE + " = " + uniqueId, null) > 0;
    }

    public Cursor readDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_feeds.TABLE_NAME, new String[]{struct_feeds.KEY_PID, struct_feeds.KEY_TITLE, struct_feeds.KEY_DESCRIPTION, struct_feeds.KEY_IMAGE, struct_feeds.KEY_URL,
                struct_feeds.KEY_SCHEDULED, struct_feeds.KEY_ViewTime, struct_feeds.KEY_DELETED, struct_feeds.KEY_UNIQUE}, selection, selectionArgs, null, null, sortOrder, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor searchDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_feeds.TABLE_NAME, new String[]{struct_feeds.KEY_PID, struct_feeds.KEY_TITLE, struct_feeds.KEY_DESCRIPTION, struct_feeds.KEY_IMAGE, struct_feeds.KEY_URL,
                struct_feeds.KEY_SCHEDULED, struct_feeds.KEY_DELETED}, selection, selectionArgs, null, null, sortOrder, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Boolean updateWhetherViewed(long id, Boolean viewed) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(Db_Events.struct_events.KEY_VIEWED , viewed);

        return dbSQL.update(struct_feeds.TABLE_NAME, updatedValues, null, null) > 0;
    }
    public Cursor countDbViewed(String selection, String selectionArgs[]) {

        Cursor cursor = dbSQL.query(struct_feeds.TABLE_NAME, new String[]{struct_feeds.KEY_PID,  Db_Events.struct_events.KEY_VIEWED },  selection, selectionArgs, null,  null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    @Override
    public int clearTablesData() {
        return dbSQL.delete(struct_feeds.TABLE_NAME, null, null);
    }
}
