package com.fsdev.imeds;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Frankline Sable on 28/11/2017.
 */

public class Db_Wiki extends DatabaseManager{
    private Context mContext;

    public Db_Wiki(Context mContext) {
        this.mContext = mContext;
    }

    public static abstract class struct_wiki implements BaseColumns {
        public static final String TABLE_NAME = "imeds_wiki";
        public static final String KEY_PID = "_id";
        public static final String KEY_TITLE = "wiki_title";
        public static final String KEY_DESCRIPTION = "wiki_description";
        public static final String KEY_DATE = "wiki_date";
        public static final String KEY_CATS = "wiki_cats";
        public static final String KEY_META= "wiki_meta";
    }

    private final static String CREATE_TABLE = "CREATE TABLE " + struct_wiki.TABLE_NAME + " ("
            + struct_wiki.KEY_PID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + struct_wiki.KEY_TITLE + " TEXT NOT NULL, "
            + struct_wiki.KEY_DESCRIPTION + " TEXT NOT NULL, "
            + struct_wiki.KEY_DATE + " TEXT NOT NULL, "
            + struct_wiki.KEY_META + " TEXT NOT NULL, "
            + Db_Events.struct_events.KEY_VIEWED  + " BOOLEAN DEFAULT FALSE, "
            + struct_wiki.KEY_CATS + " INT NOT NULL )";

    private final static String DROP_TABLE = "DROP TABLE IF EXISTS " + struct_wiki.TABLE_NAME;
    private SQLiteDatabase dbSQL;
    private DatabaseHelper dbHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "meds_wiki.db";
        public static final int DATABASE_VERSION = 5;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
    }

    public void open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        dbSQL = dbHelper.getWritableDatabase();
    }


    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public long insertDb(String title, String desc, int cats, String date, String meta) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(struct_wiki.KEY_TITLE, title);
        initialValues.put(struct_wiki.KEY_DESCRIPTION, desc);
        initialValues.put(struct_wiki.KEY_CATS, cats);
        initialValues.put(struct_wiki.KEY_DATE, date);
        initialValues.put(Db_Events.struct_events.KEY_VIEWED , false);
        initialValues.put(struct_wiki.KEY_META, meta);

        long id = dbSQL.insert(struct_wiki.TABLE_NAME, null, initialValues);
        return id;
    }

    public boolean updateDb(int id, String title, String desc, int cats, String date,String meta) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_wiki.KEY_PID, id);
        updatedValues.put(struct_wiki.KEY_TITLE, title);
        updatedValues.put(struct_wiki.KEY_DESCRIPTION, desc);
        updatedValues.put(struct_wiki.KEY_CATS, cats);
        updatedValues.put(struct_wiki.KEY_DATE, date);
        updatedValues.put(struct_wiki.KEY_META, meta);

        return dbSQL.update(struct_wiki.TABLE_NAME, updatedValues, struct_wiki.KEY_TITLE+ " =\"" + title+ "\"", null) > 0;
    }

    public Cursor readDb(String selection, String selectionArgs[], String sortOrder) {

        Cursor cursor = dbSQL.query(struct_wiki.TABLE_NAME, new String[]{struct_wiki.KEY_PID, struct_wiki.KEY_TITLE, struct_wiki.KEY_DESCRIPTION, struct_wiki.KEY_CATS,
                struct_wiki.KEY_DATE, Db_Events.struct_events.KEY_VIEWED , struct_wiki.KEY_META}, selection, selectionArgs, null, null, sortOrder, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Boolean updateWikiStatusDb(long id, Boolean latest) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_wiki.KEY_PID, id);
        updatedValues.put(Db_Events.struct_events.KEY_VIEWED , latest);

        return dbSQL.update(struct_wiki.TABLE_NAME, updatedValues, struct_wiki.KEY_PID + " = " + id, null) > 0;
    }
    public Cursor countDbViewed(String selection, String selectionArgs[]){

        Cursor cursor = dbSQL.query(struct_wiki.TABLE_NAME, new String[]{struct_wiki.KEY_PID, struct_wiki.KEY_CATS, Db_Events.struct_events.KEY_VIEWED },  selection, selectionArgs, null,  null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    @Override
    public int clearTablesData() {
        return  dbSQL.delete(struct_wiki.TABLE_NAME,null,null);
    }

    public Boolean updateWhetherViewed(long id, Boolean viewed) {

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(struct_wiki.KEY_PID, id);
        updatedValues.put(Db_Events.struct_events.KEY_VIEWED , viewed);

        return dbSQL.update(struct_wiki.TABLE_NAME, updatedValues, struct_wiki.KEY_PID + " = " + id, null) > 0;
    }
}
