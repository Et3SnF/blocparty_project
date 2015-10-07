package com.ngynstvn.android.blocparty.api.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.api.model.database.table.Table;

/**
 * Created by Ngynstvn on 10/6/15.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = BPUtils.classTag(DatabaseOpenHelper.class);

    private static final String dbName = "blocparty_db";
    private static final int DB_VERSION = 1;

    // Member variables

    private Table[] tables;

    // Constructor

    public DatabaseOpenHelper(Context context, Table... tables) {
        super(context, dbName, null, DB_VERSION);
        this.tables = tables;
    }

    // Lifecycle Methods

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "onCreate() called");

        // Create any table that inherits from Table class

        for(Table table : tables) {
            db.execSQL(table.getCreateStatement());
        }
    }

    // On Upgrade method to apply to whole database

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "onUpgrade() called");

        for(Table table : tables) {
            table.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
