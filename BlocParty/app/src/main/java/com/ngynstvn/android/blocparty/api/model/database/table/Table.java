package com.ngynstvn.android.blocparty.api.model.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ngynstvn on 10/6/15.
 */

public abstract class Table {

    // Builder interface to create builder class later

    public interface Builder {
        public long insert(SQLiteDatabase database);
    }

    // Class Variables

    protected static final String COLUMN_ID = "_id";

    // Abstract methods for subclasses to implement

    public abstract String getName();
    public abstract String getCreateStatement();

    // Essential fetchRow method

    public Cursor fetchRow(SQLiteDatabase database, long rowId) {
        return database.query(true, getName(), null, COLUMN_ID + " = ?",
                new String[] {String.valueOf(rowId)}, null, null, null, null);
    }

    // get rowId

    public static long getRowId(Cursor cursor) {
        return getLong(cursor, COLUMN_ID);
    }

    // Get data type methods

    protected static int getInteger(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return -1;
        }

        return cursor.getInt(columnIndex);
    }

    protected static double getDouble(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return -1;
        }

        return cursor.getDouble(columnIndex);
    }

    protected static long getLong(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return -1;
        }

        return cursor.getLong(columnIndex);
    }

    protected static String getString(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);

        if(columnIndex == -1) {
            return "";
        }

        return cursor.getString(columnIndex);
    }

    // onUpgrade() method

    public void onUpgrade(SQLiteDatabase writableDatabase, int oldVersion, int newVersion) {
        // In case one table is upgrade over the other
    }
}
