package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocparty.api.model.database.table.PostItemTable;

/**
 * Created by Ngynstvn on 10/7/15.
 */

public class DataSource {

    private static final String TAG = BPUtils.classTag(DataSource.class);
    private static final String POST_ITEM_TABLE = BPUtils.POST_ITEM_TABLE;

    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase database;
    private PostItemTable postItemTable;

    // Instantiate the database

    public DataSource(Context context) {
        Log.v(TAG, "DataSource instantiated");

        context.deleteDatabase(BPUtils.DB_NAME);
        postItemTable = new PostItemTable();

        // This will be network dependent so the application starts out at a clean slate every time.
        databaseOpenHelper = new DatabaseOpenHelper(BlocpartyApplication.getSharedInstance(), postItemTable);

        // Test data
        testData();
    }

    private void testData() {

        database = databaseOpenHelper.getWritableDatabase();

        new PostItemTable.Builder()
                .setOPFirstName("Richard")
                .setOPLastName("Matthews")
                .setProfilePicUrl("https://www.google.com.")
                .setPostImageUrl("https://www.google.com/")
                .setPostCaption("This is just a test to see if database works.")
                .setPostPublishDate(10115)
                .setIsPostLiked(0)
                .insert(database);
    }
}
