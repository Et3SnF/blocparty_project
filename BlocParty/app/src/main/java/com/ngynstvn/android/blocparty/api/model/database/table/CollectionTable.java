package com.ngynstvn.android.blocparty.api.model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ngynstvn.android.blocparty.BPUtils;

/**
 * Created by Ngynstvn on 10/29/15.
 */

public class CollectionTable extends Table {

    private static final String TAG = BPUtils.classTag(CollectionTable.class);

    private static final String NAME = BPUtils.COLLECTION_TABLE;

    private static final String COLUMN_COLLECTION_NAME = "collection_name";
    private static final String COLUMN_USER_ID = "user_profile_id";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_COLLECTION_NAME + " TEXT, "
                + COLUMN_USER_ID + " INTEGER);";
    }

    // Setters for Builder class

    public static class Builder implements Table.Builder {

        ContentValues contentValues = new ContentValues();

        public Builder setCollectionName(String name) {
            contentValues.put(COLUMN_COLLECTION_NAME, name);
            return this;
        }

        public Builder setUserId(long id) {
            contentValues.put(COLUMN_USER_ID, id);
            return this;
        }

        @Override
        public long insert(SQLiteDatabase database) {
            return database.insert(NAME, null, contentValues);
        }
    }

    public static String getColumnCollectionName(Cursor cursor) {
        return getString(cursor, COLUMN_COLLECTION_NAME);
    }

    public static long getColumnUserId(Cursor cursor) {
        return getLong(cursor, COLUMN_USER_ID);
    }
}
