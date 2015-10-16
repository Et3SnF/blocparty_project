package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.ngynstvn.android.blocparty.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocparty.api.model.database.table.PostItemTable;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Post;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnPostsListener;
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

    private static final String TAG = BPUtils.classTag(DataSource.class);

    private static String fbUserId = "";
    private static String fbOPName = "";
    private static String fbProfilePicUrl = "";
    private static String fbPostImageUrl = "";
    private static String fbPostCaption = "";
    private static long fbPostPublishDate = 0L;

    private static String twOPName = "";
    private static String twProfilePicUrl = "";
    private static String twPostImageUrl = "";
    private static String twPostCaption = "";
    private static long twPostPublishDate = 0L;

    private static String igOPName = "";
    private static String igProfilePicUrl = "";
    private static String igPostImageUrl = "";
    private static String igPostCaption = "";
    private static long igPostPublishDate = 0L;

    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase database;
    private PostItemTable postItemTable;

    private ArrayList<PostItem> postItemArrayList;

    // Instantiate the database

    public DataSource(Context context) {
        Log.v(TAG, "DataSource instantiated");

        postItemTable = new PostItemTable();

        postItemArrayList = new ArrayList<>();

        // This will be network dependent so the application starts out at a clean slate every time.
        databaseOpenHelper = new DatabaseOpenHelper(BlocpartyApplication.getSharedInstance(), postItemTable);

        database = databaseOpenHelper.getWritableDatabase();
    }

    public DatabaseOpenHelper getDatabaseOpenHelper() {
        return databaseOpenHelper;
    }

    public ArrayList<PostItem> getPostItemArrayList() {
        return postItemArrayList;
    }

    public void getFacebookInformation(final SimpleFacebook simpleFacebook) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.FB_LOGIN, false)) {
            Log.v(TAG, "Facebook is logged in. Getting photos.");

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

                            Log.v(TAG, "Profile information: "
                                    + "ID: " + response.getId() + " | \n"
                                    + "First Name : " + response.getFirstName() + " | \n"
                                    + "Last Name: " + response.getLastName() + " | \n"
                                    + "Picture: " + response.getPicture());
                        }
                    });

                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    Log.v(TAG, "Current ID: " + accessToken.getUserId());

                    // Get photos

                    // Get access to Graph API and get the GraphRequest
                    // Decided to just get photo information from this JSON

                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v(TAG, "Raw response: " + response.getRawResponse());

                                    try {

                                        JSONArray jsonArray = object.optJSONObject("photos").getJSONArray("data");

                                        for(int i = 0; i < jsonArray.length(); i++) {

                                            fbOPName = object.optString("name");
                                            fbUserId = object.getString("id");
                                            fbPostImageUrl = jsonArray.getJSONObject(0).getJSONArray("images")
                                                    .getJSONObject(0).getString("source");

//                                            if(jsonArray.getJSONObject(0).getString("name") != null ||
//                                                    jsonArray.getJSONObject(0).getString("name").length() == 0) {
//                                                fbPostCaption = jsonArray.getJSONObject(0).getString("name");
//                                            }
//                                            else if(jsonArray.getJSONObject(0).getString("name") == null){
//                                                fbPostCaption = "";
//                                            }

                                            String rawDate = jsonArray.getJSONObject(0).getString("created_time");

                                            fbPostPublishDate = 0L;

                                            new PostItemTable.Builder()
                                                    .setOPFullName(fbOPName)
                                                    .setProfilePicUrl(fbProfilePicUrl)
                                                    .setPostImageUrl(fbPostImageUrl)
                                                    .setPostCaption(fbPostCaption)
                                                    .setPostPublishDate(fbPostPublishDate)
                                                    .setIsPostLiked(0)
                                                    .insert(database);
                                        }

                                    } catch (JSONException e) {
                                        Log.e(TAG, "Unable to parse JSON info");
                                        e.printStackTrace();
                                    }
                                }
                            });

                    // Bundle the string necessary to perform query

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,photos{created_time,picture,name,images}");

                    // Perform the request

                    request.setParameters(parameters);
                    request.executeAsync();

            simpleFacebook.getPosts(new OnPostsListener() {
                @Override
                public void onComplete(List<Post> response) {
                    Log.v(TAG, "Number of posts: " + response.size());

                    for(Post post : response) {
                        Log.v(TAG, "Posts: " + post.getId());
                    }
                }
            });

                    return null;

                }
            }.execute();

        }
        else {
            Log.v(TAG, "Facebook is not logged in. Unable to get information.");
        }
    }

    public void getTwitterInformation(final Twitter twitter) {

        Log.v(TAG, "getTwitterInformation() called");

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.TW_LOGIN, false)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        // Timeline information

                        List<twitter4j.Status> statuses = twitter.getHomeTimeline();

                        if(statuses == null) {
                            cancel(true);
                            Log.e(TAG, "Fetching timeline cancelled");
                            return null;
                        }

                        Log.e(TAG, "Getting timeline...information");

                        for(twitter4j.Status status : statuses) {
                            Log.v(TAG, "User: " + status.getUser().getName());
                            Log.v(TAG, "Profile Pic: " + status.getUser().getBiggerProfileImageURL());
                            Log.v(TAG, "Status: " + status.getText());

                            twOPName = status.getUser().getName();
                            twProfilePicUrl = status.getUser().getBiggerProfileImageURL();
                            twPostPublishDate = status.getCreatedAt().getTime();

                            if(status.getMediaEntities().length != 0) {
                                Log.e(TAG, "Image URL: " + status.getMediaEntities()[0].getMediaURL());
                                twPostImageUrl = status.getMediaEntities()[0].getMediaURL();
                                twPostCaption = status.getText();
                            }

                            // Split the Name later.

                            if(twPostImageUrl != null && twPostImageUrl.length() > 0) {
                                new PostItemTable.Builder()
                                        .setOPFullName(twOPName)
                                        .setProfilePicUrl(twProfilePicUrl)
                                        .setPostImageUrl(twPostImageUrl)
                                        .setPostCaption(twPostCaption)
                                        .setPostPublishDate(twPostPublishDate)
                                        .setIsPostLiked(0)
                                        .insert(databaseOpenHelper.getWritableDatabase());
                            }
                        }

                        return null;
                    }
                    catch (TwitterException e) {
                        Log.v(TAG, "There was an issue getting the timeline");
                        e.printStackTrace();
                        return null;
                    }
                    catch(IllegalStateException e) {
                        Log.v(TAG, "Twitter is not properly authenticated");
                        e.printStackTrace();
                        return null;
                    }
                    catch(NullPointerException e) {
                        Log.v(TAG, "There was an issue getting Twitter information");
                        e.printStackTrace();
                        return null;
                    }
                }
            }.execute();
        }
    }

    public void getInstagramInformation(final Instagram instagram) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getString(BPUtils.IG_AUTH_CODE, null) != null) {
            Log.e(TAG, "Instagram is logged in. Getting profile info.");

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
                        Log.e(TAG, "There was an issue getting UserInfo object");
                        e.printStackTrace();
                        BPUtils.putSPrefBooleanValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(UserInfo userInfo) {

                    if (userInfo != null) {
                            Log.v(TAG, userInfo.getData().getUsername());
                            Log.v(TAG, userInfo.getData().getProfilePicture());
                            Log.v(TAG, userInfo.getData().getFullName());

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
                                        Log.v(TAG, "There was something wrong with the MediaFeed object");
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
//                                            Log.v(TAG, "User: " + mediaFeedData.getUser().getFullName());
//                                            Log.v(TAG, "Created time: " + mediaFeedData.getCreatedTime());
//                                            Log.v(TAG, "Image Link: " + mediaFeedData.getImages().getStandardResolution().getImageUrl());

                                            Log.v(TAG, "IG Post ID: " + mediaFeedData.getId());
                                            igOPName = mediaFeedData.getUser().getFullName();
                                            igProfilePicUrl = mediaFeedData.getUser().getProfilePictureUrl();
                                            igPostImageUrl = mediaFeedData.getImages().getStandardResolution().getImageUrl();

                                            try {
//                                                Log.v(TAG, "Text: " + mediaFeedData.getCaption().getText());
                                                igPostCaption = mediaFeedData.getCaption().getText();
                                            } catch (NullPointerException e) {
                                                Log.v(TAG, "Unable to get text for " + mediaFeedData.getUser().getFullName());
                                            }

                                            try {
//                                                Log.v(TAG, "CT: " + mediaFeedData.getCaption().getCreatedTime());
                                                igPostPublishDate = (1000L * Long.parseLong(mediaFeedData.getCaption().getCreatedTime()));
                                            }
                                            catch (NullPointerException e) {
                                                Log.v(TAG, "Unable to get CT for " + mediaFeedData.getUser().getFullName());
                                            }

                                            // Split the Name later.

                                            counter++;

                                            Log.v(TAG, "Instagram Items Inserted into DB: " + counter);

                                            new PostItemTable.Builder()
                                                    .setOPFullName(igOPName)
                                                    .setProfilePicUrl(igProfilePicUrl)
                                                    .setPostImageUrl(igPostImageUrl)
                                                    .setPostCaption(igPostCaption)
                                                    .setPostPublishDate(igPostPublishDate)
                                                    .setIsPostLiked(0)
                                                    .insert(databaseOpenHelper.getWritableDatabase());
                                        }
                                    }
                                }

                            }.execute();
                        }
                    }
            }.execute();
        }
        else {
            Log.v(TAG, "Something went wrong in retrieving Instagram data");
        }

    }

    public void fetchAllPostItems() {
        Log.v(TAG, "fetchAllPostItems() called");

        // Clear the current ArrayList and then insert new items into it.

        postItemArrayList.clear();

        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("Select * from " + BPUtils.POST_ITEM_TABLE + " " +
                "order by publish_date desc;", null);

        if(cursor.moveToFirst()) {
            do {
                postItemArrayList.add(itemFromCursor(cursor));
                Log.v(TAG, "Current arrayList size: " + postItemArrayList.size());
            }
            while (cursor.moveToNext());
        }

        cursor.close();

    }

    static PostItem itemFromCursor(Cursor cursor) {

        boolean isLiked = PostItemTable.getIsPostLiked(cursor) != 0;

        return new PostItem(PostItemTable.getRowId(cursor), PostItemTable.getColumnOPFullName(cursor),
                PostItemTable.getColumnOpProfilePicUrl(cursor), PostItemTable.getColumnPostImageUrl(cursor),
                PostItemTable.getColumnPostImageCaption(cursor), PostItemTable.getPostPublishDate(cursor), isLiked);
    }

    public boolean isDBEmpty(String tableName) {
        Cursor cursor = BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, tableName, null, null, null, null, null, null, null);

        if(cursor.getCount() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isDBCountGTLimit(String tableName, int limit) {
        Cursor cursor = BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper()
                .getReadableDatabase().query(true, tableName, null, null, null, null, null, null, null);

        return cursor.getCount() >= limit;
    }

    public void clearTable(String tableName) {
        BlocpartyApplication.getSharedDataSource().getDatabaseOpenHelper().getWritableDatabase()
                .execSQL("Delete from " + tableName + ";");
    }
}
