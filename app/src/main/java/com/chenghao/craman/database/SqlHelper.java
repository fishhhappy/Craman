package com.chenghao.craman.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zhongya on 16/6/1.
 */
public class SqlHelper {
    public static final String DB_NAME = "data/data/com.chenghao.craman/databases/craman.db";
    private static SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_NAME, null);

    public void insert(String table, ContentValues values) {
        try {
            db.insert(table, null, values);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        try {
            db.update(table, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = null;
        try {
            cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return cursor;
    }

    public void closeDatabase() {
        db.close();
    }

}
