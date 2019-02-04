package com.fsdev.imeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.BoringLayout;

/**
 * Created by Frankline Sable on 07/11/2017.
 */

public class Db_Events extends DatabaseManager{
    private Context mContext;
    private Boolean deleteDb = false;

    public Db_Events(Context mContext) {
        this.mContext = mContext;
    }

    public static abstract class struct_events implements BaseColumns {
        public static final String TABLE_NAME = "imeds_events";
        public static final String KEY_PID = "pid";
        public static final String KEY_TITLE = "events_title";
        public static final String KEY_DESCRIPTION = "events_description";
        public static final String KEY_SCHEDULED = "event_schedule";
        public static final String KEY_IMAGE = "event_image";
        public static final String KEY_ALARM = "event_alarm";
        public static final String KEY_VENUE = "event_venue";
        public static final String KEY_YEAR = "event_year";
        public static final String KEY_MONTH = "event_month";
        public static final String KEY_VIEWED = "data_viewed";
    }

    private final static String CREATE_TABLE = "CREATE TABLE " + struct_events.TABLE_NAME + " ("
            + struct_events.KEY_PID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + struct_events.KEY_TITLE + " TEXT NOT NULL, "
            + struct_events.KEY_DESCRIPTION + " TEXT NOT NULL, " + struct_events.KEY_SCHEDULED + " TEXT NOT NULL, "
            + struct_events.KEY_IMAGE + " TEXT NOT NULL, " + struct_events.KEY_VENUE + " TEXT NOT NULL, "
            + struct_events.KEY_MONTH + " TEXT NOT NULL, " + struct_events.KEY_YEAR + " TEXT NOT NULL, "
            + struct_events.KEY_VIEWED + " BOOLEAN DEFAULT FALSE, "
            + struct_events.KEY_ALARM + " BOOLEAN DEFAULT TRUE)";

    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + struct_events.TABLE_NAME;
    private SQLiteDatabase dbSQL;
    private DatabaseHelper dbHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "meds_events.db";
        public static final String DROP_DB = "DROP DATABASE " + DATABASE_NAME;
        public static final int DATABASE_VERSION = 9;
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
       try{
           dbHelper = new DatabaseHelper(mContext, deleteDb);
           dbSQL = dbHelper.getWritableDatabase();
       }
       catch (SQLException e){e.printStackTrace();};
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public long insertDb(String title, String desc, String imgURI, String venue, String schedule, String month, String year) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(struct_events.KEY_TITLE, title);
        initialValues.put(struct_events.KEY_DESCRIPTION, desc);
        initialValues.put(struct_events.KEY_IMAGE, imgURI);
        initialValues.put(struct_events.KEY_VENUE, venue);
        initialValues.put(struct_events.KEY_SCHEDULED, schedule);
        initialValues.put(struct_events.KEY_MONTH, month);
        initialValues.put(struct_events.KEY_YEAR, year);
        initialValues.put(struct_events.KEY_VIEWED,false);

        long id = dbSQL.insert(struct_events.TABLE_NAME, null, initialValues);
        return id;
    }

    public boolean updateDb(int id, String title, String desc, String venue, String schedule, String month, String year) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_events.KEY_PID, id);
        updatedValues.put(struct_events.KEY_TITLE, title);
        updatedValues.put(struct_events.KEY_DESCRIPTION, desc);
        updatedValues.put(struct_events.KEY_VENUE, venue);
        updatedValues.put(struct_events.KEY_SCHEDULED, schedule);
        updatedValues.put(struct_events.KEY_MONTH, month);
        updatedValues.put(struct_events.KEY_YEAR, year);
//        return dbSQL.update(struct_events.TABLE_NAME, updatedValues, struct_events.KEY_TITLE+ " = "+ " =\"" + title+ "\"",  + id, null) > 0;
        return dbSQL.update(struct_events.TABLE_NAME, updatedValues, struct_events.KEY_TITLE+" =\"" + title+ "\"", null) > 0;
    }

    public boolean updateImageDb(int id, String imgURI) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_events.KEY_PID, id);
        updatedValues.put(struct_events.KEY_IMAGE, imgURI);

        return dbSQL.update(struct_events.TABLE_NAME, updatedValues, struct_events.KEY_PID + " = " + id, null) > 0;
    }




    public Cursor readDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_events.TABLE_NAME, new String[]{struct_events.KEY_PID, struct_events.KEY_TITLE, struct_events.KEY_DESCRIPTION, struct_events.KEY_IMAGE, struct_events.KEY_VENUE,
                struct_events.KEY_SCHEDULED, struct_events.KEY_ALARM, struct_events.KEY_MONTH, struct_events.KEY_YEAR}, selection, selectionArgs, null, null, sortOrder, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor searchDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_events.TABLE_NAME, new String[]{struct_events.KEY_PID, struct_events.KEY_TITLE, struct_events.KEY_DESCRIPTION, struct_events.KEY_IMAGE, struct_events.KEY_VENUE,
                struct_events.KEY_SCHEDULED, struct_events.KEY_ALARM}, selection, selectionArgs, null, null, sortOrder, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Boolean updateWhetherViewed(long id, Boolean viewed) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_events.KEY_VIEWED, viewed);

        return dbSQL.update(struct_events.TABLE_NAME, updatedValues, null, null) > 0;
    }
    public Cursor countDbViewed(String selection, String selectionArgs[]) {

        Cursor cursor = dbSQL.query(struct_events.TABLE_NAME, new String[]{struct_events.KEY_PID,  struct_events.KEY_VIEWED},  selection, selectionArgs, null,  null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    @Override
    public int clearTablesData() {
        return dbSQL.delete(struct_events.TABLE_NAME, null, null);
    }
}
