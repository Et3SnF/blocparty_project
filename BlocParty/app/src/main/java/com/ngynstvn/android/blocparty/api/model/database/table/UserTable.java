package com.ngynstvn.android.blocparty.api.model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ngynstvn.android.blocparty.BPUtils;

/**
 * Created by Ngynstvn on 10/29/15.
 */
public class UserTable extends Table {

    private static final String TAG = BPUtils.classTag(UserTable.class);

    private static final String NAME = BPUtils.USER_TABLE;

    private static final String COLUMN_USER_FULL_NAME = "user_full_name";
    private static final String COLUMN_USER_SOCIAL_NETWORK = "user_social_network";
    private static final String COLUMN_USER_PROFILE_ID = "user_profile_id";
    private static final String COLUMN_USER_PROFILE_PIC_URL = "user_profile_pic_url";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_USER_FULL_NAME + " TEXT, "
                + COLUMN_USER_SOCIAL_NETWORK + " TEXT, "
                + COLUMN_USER_PROFILE_ID + " INTEGER, "
                + COLUMN_USER_PROFILE_PIC_URL + " TEXT);";
    }

    public static class Builder implements Table.Builder {

        ContentValues contentValues = new ContentValues();

        public Builder setColumnUserFullName(String name) {
            contentValues.put(COLUMN_USER_FULL_NAME, name);
            return this;
        }

        public Builder setColumnUserSocialNetwork(String socialNetwork) {
            contentValues.put(COLUMN_USER_SOCIAL_NETWORK, socialNetwork);
            return this;
        }

        public Builder setColumnUserProfileId(long id) {
            contentValues.put(COLUMN_USER_PROFILE_ID, id);
            return this;
        }

        public Builder setColumnUserProfilePicUrl(String url) {
            contentValues.put(COLUMN_USER_PROFILE_PIC_URL, url);
            return this;
        }

        @Override
        public long insert(SQLiteDatabase database) {
            return database.insert(NAME, null, contentValues);
        }
    }

    public static String getColumnUserFullName(Cursor cursor) {
        return getString(cursor, COLUMN_USER_FULL_NAME);
    }

    public static String getColumnUserSocialNetwork(Cursor cursor) {
        return getString(cursor, COLUMN_USER_SOCIAL_NETWORK);
    }

    public static long getColumnUserProfileId(Cursor cursor) {
        return getLong(cursor, COLUMN_USER_PROFILE_ID);
    }

    public static String getColumnUserProfilePicUrl(Cursor cursor) {
        return getString(cursor, COLUMN_USER_PROFILE_PIC_URL);
    }

}
