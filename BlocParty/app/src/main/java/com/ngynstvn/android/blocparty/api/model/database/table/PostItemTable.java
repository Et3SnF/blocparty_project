package com.ngynstvn.android.blocparty.api.model.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ngynstvn.android.blocparty.BPUtils;

/**
 * Created by Ngynstvn on 10/7/15.
 */
public class PostItemTable extends Table {

    private static final String TAG = BPUtils.classTag(PostItemTable.class);

    private static final String NAME = BPUtils.POST_ITEM_TABLE;
    private static final String COLUMN_OP_FIRST_NAME = "op_first_name";
    private static final String COLUMN_OP_LAST_NAME = "op_last_name";
    private static final String COLUMN_OP_PROFILE_PIC_URL = "op_profile_pic_url";
    private static final String COLUMN_POST_IMAGE_URL = "post_image_url";
    private static final String COLUMN_POST_IMAGE_CAPTION = "post_image_caption";
    private static final String COLUMN_POST_PUBLISH_DATE = "publish_date";
    private static final String COLUMN_IS_LIKED = "is_post_liked";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_OP_FIRST_NAME + " TEXT,"
                + COLUMN_OP_LAST_NAME + " TEXT,"
                + COLUMN_OP_PROFILE_PIC_URL + " TEXT,"
                + COLUMN_POST_IMAGE_URL + " TEXT,"
                + COLUMN_POST_IMAGE_CAPTION + " TEXT,"
                + COLUMN_POST_PUBLISH_DATE + " INTEGER,"
                + COLUMN_IS_LIKED + " INTEGER);";
    }

    // Setters inside Builder class

    public static class Builder implements Table.Builder {

        ContentValues contentValues = new ContentValues();

        public Builder setOPFirstName(String name) {
            contentValues.put(COLUMN_OP_FIRST_NAME, name);
            return this;
        }

        public Builder setOPLastName(String name) {
            contentValues.put(COLUMN_OP_LAST_NAME, name);
            return this;
        }

        public Builder setProfilePicUrl(String url) {
            contentValues.put(COLUMN_OP_PROFILE_PIC_URL, url);
            return this;
        }

        public Builder setPostImageUrl(String url) {
            contentValues.put(COLUMN_POST_IMAGE_URL, url);
            return this;
        }

        public Builder setPostCaption(String text) {
            contentValues.put(COLUMN_POST_IMAGE_CAPTION, text);
            return this;
        }

        public Builder setPostPublishDate(int value) {
            contentValues.put(COLUMN_POST_PUBLISH_DATE, value);
            return this;
        }

        public Builder setIsPostLiked(int value) {
            contentValues.put(COLUMN_IS_LIKED, value);
            return this;
        }

        @Override
        public long insert(SQLiteDatabase database) {
            return database.insert(NAME, null, contentValues);
        }
    }

    // Getters

    public static String getColumnOpFirstName(Cursor cursor) {
        return getString(cursor, COLUMN_OP_FIRST_NAME);
    }

    public static String getColumnOpLastName(Cursor cursor) {
        return getString(cursor, COLUMN_OP_LAST_NAME);
    }

    public static String getColumnOpProfilePicUrl(Cursor cursor) {
        return getString(cursor, COLUMN_OP_PROFILE_PIC_URL);
    }

    public static String getColumnPostImageUrl(Cursor cursor) {
        return getString(cursor, COLUMN_POST_IMAGE_URL);
    }

    public static String getColumnPostImageCaption(Cursor cursor) {
        return getString(cursor, COLUMN_POST_IMAGE_CAPTION);
    }

    public static long getPostPublishDate(Cursor cursor) {
        return getLong(cursor, COLUMN_POST_PUBLISH_DATE);
    }

    public static int getIsPostLiked(Cursor cursor) {
        return getInteger(cursor, COLUMN_IS_LIKED);
    }
}
