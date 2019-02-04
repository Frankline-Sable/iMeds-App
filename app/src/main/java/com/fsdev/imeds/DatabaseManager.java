package com.fsdev.imeds;

import android.database.Cursor;

import java.sql.SQLException;

/**
 * Created by Frankline Sable on 03/12/2017.
 */

public  class DatabaseManager {
    public  int clearTablesData(){
        return 0;
    };
    public void open() throws SQLException {
    }
    public void close() {
    }
    public Cursor countDbViewed(String selection, String selectionArgs[]) {
        return null;
    }
}
