package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocparty.api.model.database.table.PostItemTable;
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

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Ngynstvn on 10/7/15.
 */

public class DataSource {

    private static final String TAG = BPUtils.classTag(DataSource.class);

    private static String fbUserId = "";
    private static String fbFirstName = "";
    private static String fbLastName = "";
    private static String fbProfilePicUrl = "";
    private static String fbPostImageUrl = "";
    private static String fbPostCaption = "";
    private static long fbPostPublishDate = 0L;
    private static AccessToken accessToken;

    private static String twFirstName = "";
    private static String twLastName = "";
    private static String twProfilePicUrl = "";
    private static String twPostImageUrl = "";
    private static String twPostCaption = "";
    private static long twPostPublishDate = 0L;

    private static String igFirstName = "";
    private static String igLastName = "";
    private static String igProfilePicUrl = "";
    private static String igPostImageUrl = "";
    private static String igPostCaption = "";
    private static long igPostPublishDate = 0L;

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

        database = databaseOpenHelper.getWritableDatabase();
    }

    public void getFacebookInformation(final SimpleFacebook simpleFacebook) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.FB_LOGIN, false)) {
            Log.v(TAG, "Facebook is logged in. Getting photos.");

            // Get Profile Information

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
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

                            fbUserId = response.getId();
                            fbFirstName = response.getFirstName();
                            fbLastName = response.getLastName();
                            fbProfilePicUrl = response.getPicture();
                        }
                    });

                    accessToken = new AccessToken(
                            BlocpartyApplication.getSharedInstance().getString(R.string.fbat),
                            BlocpartyApplication.getSharedInstance().getString(R.string.fbai),
                            fbUserId, null, null, null, null, null);
                }

                @Override
                protected Void doInBackground(final Void... params) {

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

                                            fbFirstName = object.getString("name");
                                            fbUserId = object.getString("id");
                                            fbPostImageUrl = jsonArray.getJSONObject(0).getJSONArray("images")
                                                    .getJSONObject(0).getString("source");

                                            fbPostCaption = jsonArray.getJSONObject(0).getString("name");
                                            String rawDate = jsonArray.getJSONObject(0).getString("created_time");

                                            fbPostPublishDate = 0L;

                                            new PostItemTable.Builder()
                                                    .setOPFirstName(fbFirstName)
                                                    .setOPLastName(fbLastName)
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

                    // Get Posts

//            simpleFacebook.getPosts(new OnPostsListener() {
//                @Override
//                public void onComplete(List<Post> response) {
//                    Log.v(TAG, "Number of posts: " + response.size());
//
//                    for(Post post : response) {
//                        Log.v(TAG, "Posts: " + post.getId());
//                    }
//                }
//            });

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

                            twFirstName = status.getUser().getName();
                            twLastName = status.getUser().getName();
                            twProfilePicUrl = status.getUser().getBiggerProfileImageURL();
                            twPostPublishDate = status.getCreatedAt().getTime();

                            if(status.getMediaEntities().length != 0) {
                                Log.e(TAG, "Image URL: " + status.getMediaEntities()[0].getMediaURL());
                                twPostImageUrl = status.getMediaEntities()[0].getMediaURL();
                                twPostCaption = status.getText();
                            }

                            // Split the Name later.

                            new PostItemTable.Builder()
                                    .setOPFirstName(twFirstName)
                                    .setOPLastName(twLastName)
                                    .setProfilePicUrl(twProfilePicUrl)
                                    .setPostImageUrl(twPostImageUrl)
                                    .setPostCaption(twPostCaption)
                                    .setPostPublishDate(twPostPublishDate)
                                    .setIsPostLiked(0)
                                    .insert(databaseOpenHelper.getWritableDatabase());

                            Log.v(TAG, "Created At: " + status.getCreatedAt());
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
                }

            }.execute();
        }
    }

    public void getInstagramInformation(final Instagram instagram) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getString(BPUtils.IG_AUTH_CODE, null) != null) {
            Log.e(TAG, "Instagram is logged in. Getting profile info.");

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        UserInfo userInfo = instagram.getCurrentUserInfo();

                        Log.v(TAG, userInfo.getData().getUsername());
                        Log.v(TAG, userInfo.getData().getProfilePicture());
                        Log.v(TAG, userInfo.getData().getFullName());

                        MediaFeed mediaFeed = instagram.getUserFeeds();
                        List<MediaFeedData> mediaFeeds = mediaFeed.getData();

                        for (MediaFeedData mediaFeedData : mediaFeeds) {
                            Log.e(TAG, "User: " + mediaFeedData.getUser().getFullName());
                            Log.v(TAG, "Created time: " + mediaFeedData.getCreatedTime());
                            Log.v(TAG, "Link: " + mediaFeedData.getLink());

                            igFirstName = mediaFeedData.getUser().getFullName();
                            igLastName = mediaFeedData.getUser().getFullName();
                            igProfilePicUrl = userInfo.getData().getProfilePicture();
                            igPostImageUrl = mediaFeedData.getLink();

                            try {
                                Log.e(TAG, "Text: " + mediaFeedData.getCaption().getText());
                                igPostCaption = mediaFeedData.getCaption().getText();
                            }
                            catch (NullPointerException e) {
                                Log.v(TAG, "Unable to get text for " + mediaFeedData.getUser().getFullName());
                            }

                            try {
                                Log.v(TAG, "CT: " + mediaFeedData.getCaption().getCreatedTime());
                            }
                            catch (NullPointerException e) {
                                Log.v(TAG, "Unable to get CT for " + mediaFeedData.getUser().getFullName());
                            }

                            // Split the Name later.

                            new PostItemTable.Builder()
                                    .setOPFirstName(igFirstName)
                                    .setOPLastName(igLastName)
                                    .setProfilePicUrl(igProfilePicUrl)
                                    .setPostImageUrl(igPostImageUrl)
                                    .setPostCaption(igPostCaption)
                                    .setPostPublishDate(igPostPublishDate)
                                    .setIsPostLiked(0)
                                    .insert(databaseOpenHelper.getWritableDatabase());
                        }

                    } catch (InstagramException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        else {
            Log.v(TAG, "Something went wrong in retrieving Instagram data");
        }

    }
}
