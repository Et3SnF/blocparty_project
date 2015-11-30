package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Ngynstvn on 10/7/15.
 */

public class DataSource {

    private static final String CLASS_TAG = BPUtils.classTag(DataSource.class);

    // Handler Variables
    private Handler uiThreadHandler;
    private Handler databaseHandler;
    private Handler pullHandler;
    private Handler pushHandler;

    private DatabaseOpenHelper databaseOpenHelper;

    private static ArrayList<PostItem> postItemArrayList;
    private static ArrayList<Collection> collectionArrayList;
    private static ArrayList<User> fbUserArrayList;
    private static ArrayList<User> twUserArrayList;
    private static ArrayList<User> igUserArrayList;

    // Callback interface

    public interface Callback<Result> {
        void onFetchingComplete(Result result);
    }

    // Instantiate the database

    public DataSource(Context context) {
        Log.v(CLASS_TAG, "DataSource instantiated");

        uiThreadHandler = new Handler();

        DatabaseThread databaseThread = new DatabaseThread();
        databaseThread.start();

        PullThread pullThread = new PullThread();
        pullThread.start();

        PushThread pushThread = new PushThread();
        pushThread.start();
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

    /**
     *
     * Thread Classes
     *
     */

    private class DatabaseThread extends Thread {

        // Put any database tables here

        private PostItemTable postItemTable;
        private CollectionTable collectionTable;
        private UserTable userTable;

        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "DatabaseThread");

            Looper.prepare();

            databaseHandler = new Handler();

            postItemTable = new PostItemTable();
            collectionTable = new CollectionTable();
            userTable = new UserTable();

            postItemArrayList = new ArrayList<PostItem>();
            collectionArrayList = new ArrayList<Collection>();
            fbUserArrayList = new ArrayList<User>();
            twUserArrayList = new ArrayList<User>();
            igUserArrayList = new ArrayList<User>();

            databaseOpenHelper = new DatabaseOpenHelper(BlocpartyApplication.getSharedInstance(),
                    postItemTable, collectionTable, userTable);

            Looper.loop();
        }
    }

    private class PullThread extends Thread {
        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "PullThread");
            Looper.prepare();
            pullHandler = new Handler();
            Looper.loop();
        }
    }

    private class PushThread extends Thread {
        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "PushThread");
            Looper.prepare();
            pushHandler = new Handler();
            Looper.loop();
        }
    }

    // ----- Fetch Methods ----- //

    private void fetchFacebookInformation() {

        pullHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "fetchFacebookInformation");

                SimpleFacebook simpleFacebook = BPUtils.getSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                        SimpleFacebook.class, BPUtils.FB_OBJECT);

                if (simpleFacebook == null) {
                    return;
                }

                // Get Profile Information
                // Get access to Graph API and get the GraphRequest

                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                BPUtils.logMethod(CLASS_TAG);
                                Log.v(CLASS_TAG, "Raw response: " + response.getRawResponse());

//                                    BPUtils.saveRawJSONResponse("facebook_response.txt", response.getRawResponse());

                                String opName = "";
                                long profileId = 0L;
                                long postId = 0L;
                                String profilePicUrl = "";
                                String postImageUrl = "";
                                String postCaption = "";
                                long postPublishDate = 0L;
                                int isFBPostLiked = 0;

                                try {

                                    JSONArray jsonArray = object.optJSONObject("photos").getJSONArray("data");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        opName = object.optString("name");
                                        profileId = object.optLong("id");

                                        if (jsonArray.getJSONObject(i).getJSONObject("album").getString("name").equalsIgnoreCase("Profile Pictures")) {
                                            profilePicUrl = jsonArray.getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("source");
                                        }

                                        if (jsonArray.getJSONObject(i).getJSONObject("album").getString("name").equalsIgnoreCase("Timeline Photos")) {
                                            postId = Long.parseLong(jsonArray.getJSONObject(i).getString("id"));
                                            postImageUrl = jsonArray.getJSONObject(i).getJSONArray("images")
                                                    .getJSONObject(0).getString("source");

                                            try {
                                                postCaption = jsonArray.getJSONObject(i).getString("name");
                                            } catch (JSONException e) {
                                                postCaption = "";
                                            }

                                            postPublishDate = BPUtils.dateConverter(jsonArray.getJSONObject(i).getString("created_time"));

                                            try {
                                                JSONArray likesArray = jsonArray.getJSONObject(i).getJSONObject("likes").getJSONArray("data");
                                                Log.v(CLASS_TAG, "Likes Array: " + likesArray.toString());

                                                if (likesArray.toString().contains(String.valueOf(profileId))) {
                                                    isFBPostLiked = 1;
                                                }
                                            } catch (JSONException e) {
                                                isFBPostLiked = 0;
                                            }
                                        }

                                        // Get the album id for the blocparty_project photos

                                        if (postPublishDate != 0 && !isValueInDB(BPUtils.POST_ITEM_TABLE,
                                                BPUtils.POST_ID, String.valueOf(postId))) {
                                            addPostItemToDB(opName, profileId, profilePicUrl,
                                                    postId, postImageUrl, postCaption,
                                                    postPublishDate, isFBPostLiked);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e(CLASS_TAG, "Unable to parse JSON info");
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    Log.e(CLASS_TAG, "JSON Object null. Hiccup...");
                                    e.printStackTrace();
                                }

                                // Find the photo id belonging to the Timeline Photos album

                                if (BPUtils.newSPrefInstance(BPUtils.FB_TP_ID)
                                        .getString(BPUtils.FB_TP_ALB_ID, null) == null) {
                                    findFBTimelinePhotosId(object);
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,albums{id,name},photos{album,images,id," +
                        "name,link,created_time, likes}");
                request.setParameters(parameters);
                request.executeAsync();
            }
        });
    }

    private void findFBTimelinePhotosId(JSONObject object) {

        BPUtils.logMethod(CLASS_TAG);

        try {
            JSONArray idArray = object.optJSONObject("albums").getJSONArray("data");

            for (int i = 0; i < idArray.length(); i++) {
                if (idArray.getJSONObject(i).getString("name").equalsIgnoreCase("Timeline Photos")) {

                    BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FB_TP_ID), BPUtils.FB_TP_ID,
                            BPUtils.FB_TP_ALB_ID, idArray.getJSONObject(i).getString("id"));

                    Log.v(CLASS_TAG, "Album: " + idArray.getJSONObject(i).getString("name"));
                    Log.v(CLASS_TAG, "Album ID: " + idArray.getJSONObject(i).getString("id"));
                    break;
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchTwitterInformation() {

        pullHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "fetchTwitterInformation");

                String consumerKey = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_CONSUMER_KEY, null);
                String consumerKeySecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_CONSUMER_SECRET, null);
                String token = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_ACCESS_TOKEN, null);
                String tokenSecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

                TwitterFactory twitterFactory = new TwitterFactory(BPUtils.getTwitterConfigBuilder(
                        consumerKey, consumerKeySecret, token, tokenSecret));
                Twitter twitter = twitterFactory.getInstance();

                if (twitter == null) {
                    return;
                }

                if(token != null && tokenSecret != null) {
                    String opName = "";
                    long opProfileId = 0L;
                    String profilePicUrl = "";
                    long postId = 0L;
                    String postImageUrl = "";
                    String postCaption = "";
                    long postPublishDate = 0L;
                    int isTwPostLiked = 0;

                    try {
                        // Timeline information

                        List<twitter4j.Status> statuses = twitter.getHomeTimeline();

                        if (statuses == null) {
                            Log.e(CLASS_TAG, "Fetching timeline cancelled");
                            return;
                        }

                        Log.e(CLASS_TAG, "Getting timeline...information");

                        for (twitter4j.Status status : statuses) {
                            opName = status.getUser().getName();
                            opProfileId = status.getUser().getId();
                            profilePicUrl = status.getUser().getBiggerProfileImageURL();
                            postPublishDate = status.getCreatedAt().getTime();

                            if (status.getMediaEntities().length != 0) {
                                postImageUrl = status.getMediaEntities()[0].getMediaURL();
                                postId = status.getMediaEntities()[0].getId();
                                postCaption = status.getText();
                            }

                            if (status.getFavoriteCount() > 0 && status.isFavorited()) {
                                isTwPostLiked = 1;
                            } else {
                                isTwPostLiked = 0;
                            }

//                        BPUtils.logTwitterPostItemInfo(CLASS_TAG, status);

                            if (!isValueInDB(BPUtils.POST_ITEM_TABLE, BPUtils.TW_POST_IMG_URL,
                                    postImageUrl) && postId != 0 && !isValueInDB(BPUtils.POST_ITEM_TABLE,
                                    BPUtils.POST_ID, String.valueOf(postId))) {
                                addPostItemToDB(opName, opProfileId, profilePicUrl, postId,
                                        postImageUrl, postCaption, postPublishDate, isTwPostLiked);
                            }
                        }

                    } catch (TwitterException e) {
                        Log.v(CLASS_TAG, "There was an issue getting the timeline");
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        Log.v(CLASS_TAG, "Twitter is not properly authenticated");
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.v(CLASS_TAG, "There was an issue getting Twitter information");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fetchInstagramInformation() {

        pullHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "fetchInstagramInformation");

                Instagram instagram = BPUtils.getSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                        Instagram.class, BPUtils.IG_OBJECT);

                if(instagram == null) {
                    return;
                }

                String opName = "";
                long opProfileId = 0L;
                String profilePicUrl = "";
                long postId = 0L;
                String postImageUrl = "";
                String postCaption = "";
                long postPublishDate = 0L;
                int isIGPostLiked = 0;

                // Fetch Instagram user info

                try {
                    // Fetch any instagram information and store it as object
                    List<MediaFeedData> mediaFeedDatas = instagram.getUserFeeds().getData();

                    for (MediaFeedData mediaFeedData : mediaFeedDatas) {

//                        BPUtils.logInstagramPostItemInfo(CLASS_TAG, mediaFeedData);

                        opName = mediaFeedData.getUser().getFullName();

                        if (opName.length() == 0) {
                            opName = mediaFeedData.getUser().getUserName();
                        }

                        opProfileId = Long.parseLong(mediaFeedData.getUser().getId());
                        postId = Long.parseLong(mediaFeedData.getId().split("_")[0]);
                        profilePicUrl = mediaFeedData.getUser().getProfilePictureUrl();
                        postImageUrl = mediaFeedData.getImages().getStandardResolution().getImageUrl();

                        try {
                            postCaption = mediaFeedData.getCaption().getText();
                        } catch (NullPointerException e) {
                            postCaption = "";
                        }

                        try {
                            postPublishDate = (1000L * Long.parseLong(mediaFeedData.getCaption().getCreatedTime()));
                        }
                        catch (NullPointerException e) {
                            postPublishDate = -1;
                        }

                        if(postPublishDate != -1 && !isValueInDB(BPUtils.POST_ITEM_TABLE,
                                BPUtils.POST_ID, String.valueOf(postId))) {
                            addPostItemToDB(opName, opProfileId, profilePicUrl,
                                    postId, postImageUrl, postCaption, postPublishDate, isIGPostLiked);
                        }
                    }
                } catch (InstagramException e) {
                    Log.e(CLASS_TAG, "There was an issue getting Instagram information.");
                    e.printStackTrace();
                    BPUtils.putSPrefBooleanValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                            BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                } catch (NullPointerException e) {
                    Log.e(CLASS_TAG, "Something instagram is null. Look at StackTrace.");
                    e.printStackTrace();
                    BPUtils.putSPrefBooleanValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                            BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                }

            }
        });
    }

    /**
     *
     * Fetching Threads
     *
     */

    public synchronized void fetchPostItems(final Callback<ArrayList<PostItem>> fetchCallback) {

        pullHandler.post(new Runnable() {
            @Override
            public void run() {
                clearTable(BPUtils.POST_ITEM_TABLE);
            }
        });

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.FB_LOGIN, false)) {
            fetchFacebookInformation();
        }

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.TW_LOGIN, false)) {
            fetchTwitterInformation();
        }

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.IG_LOGIN, false)) {
            fetchInstagramInformation();
        }

        pullHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "fetchPostItems");

                final ArrayList<PostItem> fetchedItems = new ArrayList<PostItem>();

                SQLiteDatabase database = databaseOpenHelper.getReadableDatabase();
                Cursor cursor = database.query(BPUtils.POST_ITEM_TABLE, null, null, null,
                        BPUtils.POST_ID, null, BPUtils.PUBLISH_DATE + " DESC ", String.valueOf(20));

                if(cursor.moveToFirst() && fetchedItems.size() <= 20) {
                    do {
                        fetchedItems.add(itemFromCursor(cursor));
                    }
                    while(cursor.moveToNext());
                }

                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.logMethod(CLASS_TAG, "uiThreadHandler");
                        Log.v(CLASS_TAG, "Items Fetched: " + fetchedItems.size());
                        fetchCallback.onFetchingComplete(fetchedItems);
                    }
                });
            }
        });
    }

    public synchronized void fetchMorePostItems(final Callback<ArrayList<PostItem>> fetchCallback,
                                                final long lastRowId) {

        fetchFacebookInformation();
        fetchTwitterInformation();
        fetchInstagramInformation();

        pullHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "fetchMorePostItems");

                final ArrayList<PostItem> moreFetchedItems = new ArrayList<PostItem>();

                final String statement = "Select * from " + BPUtils.POST_ITEM_TABLE
                        + " where " + BPUtils.ROW_ID + " > " + String.valueOf(lastRowId)
                        + " group by " + BPUtils.POST_ID
                        + " order by " + BPUtils.PUBLISH_DATE + " desc "
                        + " limit 20 ";

                SQLiteDatabase database = databaseOpenHelper.getReadableDatabase();
                Cursor cursor = database.rawQuery(statement, null);

                if(cursor.moveToFirst() && moreFetchedItems.size() <= 20) {
                    do {
                        moreFetchedItems.add(itemFromCursor(cursor));
                    }
                    while(cursor.moveToNext());
                }

                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.logMethod(CLASS_TAG, "uiThreadHandler");
                        Log.v(CLASS_TAG, "More Items Fetched: " + moreFetchedItems.size());
                        fetchCallback.onFetchingComplete(moreFetchedItems);
                    }
                });
            }
        });
    }

    public void fetchFilteredPostItems(String collectionName) {

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

        boolean isLiked = false;

        if(PostItemTable.getIsPostLiked(cursor) == 1) {
            isLiked = true;
        }
        else {
            isLiked = false;
        }

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

    public void updatePostItemLike(long postId, boolean isLiked) {
        BPUtils.logMethod(CLASS_TAG);

        // watch out if this is going to not work

        int isLikedValue = isLiked ? 1 : 0;

        String statement = "Update " + BPUtils.POST_ITEM_TABLE + " set " + BPUtils.IS_POST_LIKED
                + " = " + isLikedValue + " where " + BPUtils.POST_ID + " = " + String.valueOf(postId);
        Cursor cursor = databaseOpenHelper.getWritableDatabase().rawQuery(statement, null);

        cursor.close();
    }

    public void addCollectionToDB(final String name, final String userProfileId) {
        BPUtils.logMethod(CLASS_TAG);

        new CollectionTable.Builder()
                .setCollectionName(name)
                .setUserId(Long.parseLong(userProfileId))
                .insert(databaseOpenHelper.getWritableDatabase());
    }

    public void addUserToDB(final User user) {
        pushHandler.post(new Runnable() {
            @Override
            public void run() {
//                BPUtils.logMethod(CLASS_TAG, "addUserToDB");

                if(!isValueInDB(BPUtils.USER_TABLE, "user_full_name", user.getUserFullName()) &&
                        !isValueInDB(BPUtils.USER_TABLE, "user_profile_id", String.valueOf(user.getUserProfileId()))) {
                    new UserTable.Builder()
                            .setColumnUserFullName(user.getUserFullName())
                            .setColumnUserSocialNetwork(user.getUserSocNetwork())
                            .setColumnUserProfileId(user.getUserProfileId())
                            .setColumnUserProfilePicUrl(user.getUserProfilePicUrl())
                            .insert(databaseOpenHelper.getWritableDatabase());
                }
            }
        });
    }

    // DB Methods

    public boolean isDBEmpty(String tableName) {
        BPUtils.logMethod(CLASS_TAG);

        Cursor cursor = BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, tableName, null, null, null, null, null, null, null);

        return cursor.getCount() == 0;
    }

    public boolean isValueInDB(String tableName, String field, String fieldValue) {
//        BPUtils.logMethod(CLASS_TAG);

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
        BPUtils.logMethod(CLASS_TAG);

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
        BPUtils.logMethod(CLASS_TAG);

        final String statement = "Delete from " + tableName + ";";

        BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase()
                .execSQL(statement);
    }

}
