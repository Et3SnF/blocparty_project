package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.api.model.Collection;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.ngynstvn.android.blocparty.api.model.User;
import com.ngynstvn.android.blocparty.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocparty.api.model.database.table.CollectionTable;
import com.ngynstvn.android.blocparty.api.model.database.table.PostItemTable;
import com.ngynstvn.android.blocparty.api.model.database.table.UserTable;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Ngynstvn on 10/7/15.
 */

public class DataSource {

    private static final String CLASS_TAG = BPUtils.classTag(DataSource.class);

    private static String fbOPName = "";
    private static long fbOPProfileId = 0L;
    private static long fbPostId = 0L;
    private static String fbProfilePicUrl = "";
    private static String fbPostImageUrl = "";
    private static String fbPostCaption = "";
    private long fbPostPublishDate = 0L;
    private static int fbPostLiked = 0;

    private static String twOPName = "";
    private static long twOPProfileId = 0L;
    private static String twProfilePicUrl = "";
    private static long twPostId = 0L;
    private static String twPostImageUrl = "";
    private static String twPostCaption = "";
    private static long twPostPublishDate = 0L;
    private static int twPostLiked = 0;

    private static String igOPName = "";
    private static long igOPProfileId = 0L;
    private static String igProfilePicUrl = "";
    private static long igPostId = 0L;
    private static String igPostImageUrl = "";
    private static String igPostCaption = "";
    private static long igPostPublishDate = 0L;
    private static int igPostLiked = 0;

    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase database;

    private static ArrayList<PostItem> postItemArrayList;
    private static ArrayList<Collection> collectionArrayList;
    private static ArrayList<User> fbUserArrayList;
    private static ArrayList<User> twUserArrayList;
    private static ArrayList<User> igUserArrayList;

    // Instantiate the database

    public DataSource(Context context) {
        Log.v(CLASS_TAG, "DataSource instantiated");

        PostItemTable postItemTable = new PostItemTable();
        CollectionTable collectionTable = new CollectionTable();
        UserTable userTable = new UserTable();

        postItemArrayList = new ArrayList<PostItem>();
        collectionArrayList = new ArrayList<Collection>();
        fbUserArrayList = new ArrayList<User>();
        twUserArrayList = new ArrayList<User>();
        igUserArrayList = new ArrayList<User>();

        // This will be network dependent so the application starts out at a clean slate every time.
        databaseOpenHelper = new DatabaseOpenHelper(BlocpartyApplication.getSharedInstance(),
                postItemTable, collectionTable, userTable);

        database = databaseOpenHelper.getWritableDatabase();
    }

    public DatabaseOpenHelper getDatabaseOpenHelper() {
        return databaseOpenHelper;
    }

    public ArrayList<PostItem> getPostItemArrayList() {
        BPUtils.logMethod(CLASS_TAG);
        return postItemArrayList;
    }

    public ArrayList<Collection> getCollectionArrayList() {
        return collectionArrayList;
    }

    public ArrayList<User> getFbUserArrayList() {
        return fbUserArrayList;
    }

    public ArrayList<User> getTwUserArrayList() {
        return twUserArrayList;
    }

    public ArrayList<User> getIgUserArrayList() {
        return igUserArrayList;
    }

    // ----- Fetch Methods ----- //

        public void fetchFacebookInformation(final SimpleFacebook simpleFacebook) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.FB_LOGIN, false)) {
            Log.v(CLASS_TAG, "Facebook is logged in. Getting photos.");

            // Get Profile Information

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(final Void... params) {

                    PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
                    pictureAttributes.setHeight(300);
                    pictureAttributes.setWidth(300);
                    pictureAttributes.setType(PictureAttributes.PictureType.SQUARE);

                    Profile.Properties properties = new Profile.Properties.Builder()
                            .add(Profile.Properties.ID)
                            .add(Profile.Properties.FIRST_NAME)
                            .add(Profile.Properties.LAST_NAME)
                            .add(Profile.Properties.PICTURE, pictureAttributes)
                            .build();

                    simpleFacebook.getProfile(properties, new OnProfileListener() {
                        @Override
                        public void onComplete(Profile response) {

                            Log.v(CLASS_TAG, "Profile information: "
                                    + "ID: " + response.getId() + " | "
                                    + "First Name : " + response.getFirstName() + " | "
                                    + "Last Name: " + response.getLastName() + " | "
                                    + "Picture: " + response.getPicture());
                        }
                    });

                    AccessToken accessToken = AccessToken.getCurrentAccessToken();

                    // Get photos

                    // Get access to Graph API and get the GraphRequest
                    // Decided to just get photo information from this JSON

                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v(CLASS_TAG, "Raw response: " + response.getRawResponse());

                                    try {

                                        JSONArray jsonArray = object.optJSONObject("photos").getJSONArray("data");

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            fbOPName = object.optString("name");

                                            fbOPProfileId = object.optLong("id");

                                            if(jsonArray.getJSONObject(i).getJSONObject("album").getString("name").equalsIgnoreCase("Profile Pictures")) {
                                                fbProfilePicUrl = jsonArray.getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("source");
                                            }

                                            if(jsonArray.getJSONObject(i).getJSONObject("album").getString("name").equalsIgnoreCase("Timeline Photos")){
                                                    fbPostId = Long.parseLong(jsonArray.getJSONObject(i).getString("id"));
                                                    fbPostImageUrl = jsonArray.getJSONObject(i).getJSONArray("images")
                                                            .getJSONObject(0).getString("source");

                                                    try{
                                                    fbPostCaption = jsonArray.getJSONObject(i).getString("name");
                                                }
                                                catch (JSONException e) {
                                                    fbPostCaption = "";
                                                }

                                                fbPostPublishDate = BPUtils.dateConverter(jsonArray.getJSONObject(i).getString("created_time"));
                                            }

                                            // Get the album id for the blocparty_project photos

                                            if(fbPostPublishDate != 0) {
                                                addPostItemToDB(fbOPName, fbOPProfileId, fbProfilePicUrl,
                                                        fbPostId, fbPostImageUrl, fbPostCaption,
                                                        fbPostPublishDate, fbPostLiked);
                                            }
                                        }

                                        // Find the photo id belonging to the Timeline Photos album

                                        JSONArray idArray = object.optJSONObject("albums").getJSONArray("data");

                                        for(int i = 0; i < idArray.length(); i++) {
                                            if(idArray.getJSONObject(i).getString("name").equalsIgnoreCase("Timeline Photos")) {

                                                BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils
                                                                .FB_TP_ID), BPUtils.FB_TP_ID,
                                                        BPUtils.FB_TP_ALB_ID, idArray.getJSONObject(i)
                                                                .getString("id"));

                                                Log.v(CLASS_TAG, "Album: " + idArray.getJSONObject(i).getString("name"));
                                                Log.v(CLASS_TAG, "Album ID: " + idArray.getJSONObject(i).getString("id"));
                                                break;
                                            }
                                        }

                                    }
                                    catch (JSONException e) {
                                        Log.e(CLASS_TAG, "Unable to parse JSON info");
                                        e.printStackTrace();
                                    }
                                    catch (NullPointerException e) {
                                        Log.e(CLASS_TAG, "JSON Object null. Hiccup...");
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,albums{id,name},photos{album,images,id,name,link,created_time}");
                    request.setParameters(parameters);
                    request.executeAsync();

                    return null;

                }
            }.execute();

        }
        else {
            Log.v(CLASS_TAG, "Facebook is not logged in. Unable to get information.");
        }
    }

    public void fetchTwitterInformation(final Twitter twitter) {

        Log.v(CLASS_TAG, "fetchTwitterInformation() called");

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.TW_LOGIN, false)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        // Timeline information

                        List<twitter4j.Status> statuses = twitter.getHomeTimeline();

                        if(statuses == null) {
                            cancel(true);
                            Log.e(CLASS_TAG, "Fetching timeline cancelled");
                            return null;
                        }

                        Log.e(CLASS_TAG, "Getting timeline...information");

                        for(twitter4j.Status status : statuses) {
//                            Log.v(CLASS_TAG, "User: " + status.getUser().getName());
//                            Log.v(CLASS_TAG, "User ID: " + status.getUser().getId());
//                            Log.v(CLASS_TAG, "Profile Pic: " + status.getUser().getBiggerProfileImageURL());
//                            Log.v(CLASS_TAG, "Status: " + status.getText());
//                            Log.v(CLASS_TAG, "Post ID: " + status.getId());

                            twOPName = status.getUser().getName();
                            twOPProfileId = status.getUser().getId();
                            twProfilePicUrl = status.getUser().getBiggerProfileImageURL();
                            twPostPublishDate = status.getCreatedAt().getTime();

                            if(status.getMediaEntities().length != 0) {
//                                Log.e(CLASS_TAG, "Image URL: " + status.getMediaEntities()[0].getMediaURL());
                                twPostImageUrl = status.getMediaEntities()[0].getMediaURL();
                                twPostId = status.getMediaEntities()[0].getId();
                                twPostCaption = status.getText();
                            }

                            if(!isValueInDB(BPUtils.POST_ITEM_TABLE, BPUtils.TW_POST_IMG_URL, twPostImageUrl) && twPostId != 0) {

                                addPostItemToDB(twOPName, twOPProfileId, twProfilePicUrl, twPostId,
                                        twPostImageUrl, twPostCaption, twPostPublishDate, twPostLiked);
                            }
                        }

                        return null;
                    }
                    catch (TwitterException e) {
                        Log.v(CLASS_TAG, "There was an issue getting the timeline");
                        e.printStackTrace();
                        return null;
                    }
                    catch(IllegalStateException e) {
                        Log.v(CLASS_TAG, "Twitter is not properly authenticated");
                        e.printStackTrace();
                        return null;
                    }
                    catch(NullPointerException e) {
                        Log.v(CLASS_TAG, "There was an issue getting Twitter information");
                        e.printStackTrace();
                        return null;
                    }
                }
            }.execute();
        }
    }

    public void fetchInstagramInformation(final Instagram instagram) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getString(BPUtils.IG_AUTH_CODE, null) != null) {
            Log.e(CLASS_TAG, "Instagram is logged in. Getting profile info.");

            new AsyncTask<Void, Void, UserInfo>() {
                @Override
                protected void onPreExecute() {
                    clearTable(BPUtils.POST_ITEM_TABLE);
                }

                @Override
                protected UserInfo doInBackground(Void... params) {

                    try {
                        return instagram.getCurrentUserInfo();
                    }
                    catch (InstagramException e) {
                        Log.e(CLASS_TAG, "There was an issue getting UserInfo object");
                        e.printStackTrace();
                        BPUtils.putSPrefBooleanValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(UserInfo userInfo) {

                    if (userInfo != null) {
                            Log.v(CLASS_TAG, userInfo.getData().getUsername());
                            Log.v(CLASS_TAG, userInfo.getData().getProfilePicture());
                            Log.v(CLASS_TAG, userInfo.getData().getFullName());

                            new AsyncTask<Void, Void, List<MediaFeedData>>() {
                                @Override
                                protected List<MediaFeedData> doInBackground(Void... params) {

                                    MediaFeed mediaFeed = null;

                                    try {
                                        mediaFeed = instagram.getUserFeeds();
                                        return mediaFeed.getData();
                                    }
                                    catch (InstagramException e) {
                                        e.printStackTrace();
                                        Log.v(CLASS_TAG, "There was something wrong with the MediaFeed object");
                                        BPUtils.putSPrefBooleanValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                                                BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                                        return null;
                                    }
                                }

                                @Override
                                protected void onPostExecute(List<MediaFeedData> mediaFeedDatas) {

                                    int counter = 0;

                                    if(mediaFeedDatas != null) {
                                        for (MediaFeedData mediaFeedData : mediaFeedDatas) {
//                                            Log.v(CLASS_TAG, "User: " + mediaFeedData.getUser().getFullName());
//                                            Log.v(CLASS_TAG, "User ID: " + mediaFeedData.getUser().getId());
//                                            Log.v(CLASS_TAG, "Created time: " + mediaFeedData.getCreatedTime());
//                                            Log.v(CLASS_TAG, "Image Link: " + mediaFeedData.getImages().getStandardResolution().getImageUrl());

                                            igOPName = mediaFeedData.getUser().getFullName();

                                            if(igOPName.length() == 0) {
                                                igOPName = mediaFeedData.getUser().getUserName();
                                            }

                                            igOPProfileId = Long.parseLong(mediaFeedData.getUser().getId());
//                                            Log.v(CLASS_TAG, "Raw ID: " + mediaFeedData.getId());
//                                            Log.v(CLASS_TAG, "Inserted Post ID: " + Long.parseLong(mediaFeedData.getId().split("_")[0]));
                                            igPostId = Long.parseLong(mediaFeedData.getId().split("_")[0]);
                                            igProfilePicUrl = mediaFeedData.getUser().getProfilePictureUrl();
                                            igPostImageUrl = mediaFeedData.getImages().getStandardResolution().getImageUrl();

                                            try {
//                                                Log.v(CLASS_TAG, "Text: " + mediaFeedData.getCaption().getText());
                                                igPostCaption = mediaFeedData.getCaption().getText();
                                            } catch (NullPointerException e) {
                                                igPostCaption = "";
                                                Log.v(CLASS_TAG, "Unable to get text for " + mediaFeedData.getUser().getFullName());
                                            }

                                            try {
//                                                Log.v(CLASS_TAG, "CT: " + mediaFeedData.getCaption().getCreatedTime());
                                                igPostPublishDate = (1000L * Long.parseLong(mediaFeedData.getCaption().getCreatedTime()));
                                            }
                                            catch (NullPointerException e) {
                                                Log.v(CLASS_TAG, "Unable to get CT for " + mediaFeedData.getUser().getFullName());
                                            }

                                            counter++;

//                                            Log.v(CLASS_TAG, "Instagram Items Inserted into DB: " + counter);

                                            addPostItemToDB(igOPName, igOPProfileId, igProfilePicUrl,
                                                    igPostId, igPostImageUrl, igPostCaption, igPostPublishDate, igPostLiked);
                                        }
                                    }
                                }

                            }.execute();
                        }
                    }
            }.execute();
        }
        else {
            Log.v(CLASS_TAG, "Something went wrong in retrieving Instagram data");
        }

    }

    public void fetchAllPostItems() {
        Log.v(CLASS_TAG, "fetchAllPostItems() called");

        // Clear the current ArrayList and then insert new items into it.

        postItemArrayList.clear();

        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        final String statement = "Select * from " + BPUtils.POST_ITEM_TABLE
                + " order by " + BPUtils.PUBLISH_DATE + " desc "
                + " limit 20;";

        Cursor cursor = database.rawQuery(statement, null);

        if(cursor.moveToFirst()) {
            do {
                postItemArrayList.add(itemFromCursor(cursor));
//                Log.v(CLASS_TAG, "Current arrayList size: " + postItemArrayList.size());
            }
            while (cursor.moveToNext());
        }

        cursor.close();

    }

    public void fetchFilteredPostItems(String collectionName) {

        postItemArrayList.clear();

        final String statement = "Select * from " + BPUtils.COLLECTION_TABLE
                + " join " + BPUtils.USER_TABLE + " on " + BPUtils.COLLECTION_TABLE + "." + BPUtils.USER_PROFILE_ID
                + " = " + BPUtils.USER_TABLE + "." + BPUtils.USER_PROFILE_ID
                + " join " + BPUtils.POST_ITEM_TABLE + " on " + BPUtils.USER_TABLE + "." + BPUtils.USER_PROFILE_ID
                + " = " + BPUtils.POST_ITEM_TABLE + "." + BPUtils.OP_PROFILE_ID
                + " where " + BPUtils.COLLECTION_TABLE + "." + BPUtils.COLLECTION_NAME + " = '" + collectionName + "'"
                + " group by " + BPUtils.POST_ITEM_TABLE + "." + BPUtils.POST_ID
                + " order by " + BPUtils.POST_ITEM_TABLE + "." + BPUtils.PUBLISH_DATE + " desc;";

        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(statement, null);

        if(cursor.moveToFirst()) {
            do {
                postItemArrayList.add(itemFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
    }

    public void fetchUsers(String socialNetwork) {
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        final String statement = "Select * from " + BPUtils.USER_TABLE
                + " where " + BPUtils.USER_SOCIAL_NETWORK + " like '" + socialNetwork + "'"
                + " order by " + BPUtils.USER_FULL_NAME + ";";

        Cursor cursor = database.rawQuery(statement, null);

        if(socialNetwork.equalsIgnoreCase("Facebook")) {
            if(cursor.moveToFirst()) {
                do {
                    fbUserArrayList.add(userFromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        }
        else if(socialNetwork.equalsIgnoreCase("Twitter")) {
            if(cursor.moveToFirst()) {
                do {
                    twUserArrayList.add(userFromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        }
        else if(socialNetwork.equalsIgnoreCase("Instagram")) {
            if(cursor.moveToFirst()) {
                do {
                    igUserArrayList.add(userFromCursor(cursor));
                }
                while (cursor.moveToNext());
            }
        }

        cursor.close();
    }

    public void fetchCollections() {
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        final String statement = "Select distinct " + BPUtils.COLLECTION_NAME
                + " from " + BPUtils.COLLECTION_TABLE
                + " order by " + BPUtils.COLLECTION_NAME + ";";

        Cursor cursor = database.rawQuery(statement, null);

        if(cursor.moveToFirst()) {
            do {
                collectionArrayList.add(collectionFromCursor(cursor));
            }
            while(cursor.moveToNext());
        }

        cursor.close();
    }

    public ArrayList<User> fetchCollectionUsers(String collectionName) {

        ArrayList<User> userArrayList = new ArrayList<>();

        final String statement = "Select * from " + BPUtils.COLLECTION_TABLE
                + " join " + BPUtils.USER_TABLE + " on " + BPUtils.COLLECTION_TABLE + "."
                + BPUtils.USER_PROFILE_ID + " = " + BPUtils.USER_TABLE + "." + BPUtils.USER_PROFILE_ID
                + " where " + BPUtils.COLLECTION_TABLE + "." + BPUtils.COLLECTION_NAME + " = '" + collectionName + "';";

        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(statement, null);

        if(cursor.moveToFirst()) {
            do {
                userArrayList.add(userFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return userArrayList;
    }

    // Object from Cursor Methods

    static PostItem itemFromCursor(Cursor cursor) {

        boolean isLiked = PostItemTable.getIsPostLiked(cursor) != 0;

        return new PostItem(PostItemTable.getRowId(cursor), PostItemTable.getColumnOPFullName(cursor),
                PostItemTable.getColumnOpProfileId(cursor), PostItemTable.getColumnOpProfilePicUrl(cursor),
                PostItemTable.getColumnPostId(cursor), PostItemTable.getColumnPostImageUrl(cursor),
                PostItemTable.getColumnPostImageCaption(cursor), PostItemTable.getPostPublishDate(cursor), isLiked);
    }

    static Collection collectionFromCursor(Cursor cursor) {
        return new Collection(CollectionTable.getRowId(cursor), CollectionTable.getColumnCollectionName(cursor),
                CollectionTable.getColumnUserId(cursor));
    }

    static User userFromCursor(Cursor cursor) {
        return new User(UserTable.getRowId(cursor), UserTable.getColumnUserFullName(cursor),
                UserTable.getColumnUserSocialNetwork(cursor), UserTable.getColumnUserProfileId(cursor),
                UserTable.getColumnUserProfilePicUrl(cursor));
    }

    // Add Objects to DB

    private void addPostItemToDB(String name, long profileId, String profImgUrl, long postId,
                                 String postImgUrl, String postCaption, long publishDate, int isLiked) {

        if(isLiked < 0 || isLiked > 1) {
            Log.e(CLASS_TAG, "isLiked value is not either 0 or 1. Setting isLiked to 0");
            isLiked = 0;
        }

        new PostItemTable.Builder()
                .setOPFullName(name)
                .setOPProfileId(profileId)
                .setProfilePicUrl(profImgUrl)
                .setPostId(postId)
                .setPostImageUrl(postImgUrl)
                .setPostCaption(postCaption)
                .setPostPublishDate(publishDate)
                .setIsPostLiked(isLiked)
                .insert(databaseOpenHelper.getWritableDatabase());
    }

    public void addCollectionToDB(final String name, final String userProfileId) {
        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                new CollectionTable.Builder()
                        .setCollectionName(name)
                        .setUserId(Long.parseLong(userProfileId))
                        .insert(databaseOpenHelper.getWritableDatabase());
            }
        });
    }

    public void addUserToDB(final User user) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if(!isValueInDB(BPUtils.USER_TABLE, "user_full_name", user.getUserFullName()) &&
                        !isValueInDB(BPUtils.USER_TABLE, "user_profile_id", String.valueOf(user.getUserProfileId()))) {
                    new UserTable.Builder()
                            .setColumnUserFullName(user.getUserFullName())
                            .setColumnUserSocialNetwork(user.getUserSocNetwork())
                            .setColumnUserProfileId(user.getUserProfileId())
                            .setColumnUserProfilePicUrl(user.getUserProfilePicUrl())
                            .insert(databaseOpenHelper.getWritableDatabase());
                }
                else {
                    return null;
                }

                return null;
            }
        }.execute();
    }

    // DB Methods

    public boolean isDBEmpty(String tableName) {
        Cursor cursor = BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, tableName, null, null, null, null, null, null, null);

        return cursor.getCount() == 0;
    }

    public boolean isValueInDB(String tableName, String field, String fieldValue) {
        Cursor cursor = BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().rawQuery("Select * from " + tableName + " where " + field
                        + " like '" + fieldValue + "'", null);

        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }

    public int getDBItemCount(String tableName, String field, String fieldValue) {

        final String statement = "Select * from " + tableName
                + " where " + field + " like '" + fieldValue + "';";

        Cursor cursor = BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().rawQuery(statement, null);

        if(cursor.moveToFirst()) {
            return cursor.getCount();
        }

        return 0;
    }

    public void clearTable(String tableName) {

        final String statement = "Delete from " + tableName + ";";

        BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase()
                .execSQL(statement);
    }

}
