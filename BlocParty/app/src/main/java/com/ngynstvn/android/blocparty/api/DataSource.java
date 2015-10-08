package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.api.model.database.DatabaseOpenHelper;
import com.ngynstvn.android.blocparty.api.model.database.table.PostItemTable;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Account;
import com.sromku.simple.fb.entities.Album;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.entities.Post;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnAccountsListener;
import com.sromku.simple.fb.listeners.OnAlbumsListener;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnPhotosListener;
import com.sromku.simple.fb.listeners.OnPostsListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;

import org.jinstagram.Instagram;
import org.jinstagram.entity.common.Caption;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.util.Date;
import java.util.List;

import twitter4j.ExtendedMediaEntity;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * Created by Ngynstvn on 10/7/15.
 */

public class DataSource {

    private static final String TAG = BPUtils.classTag(DataSource.class);

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

    public void getFacebookInformation(SimpleFacebook simpleFacebook) {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.FB_LOGIN, false)) {
            Log.v(TAG, "Facebook is logged in. Getting photos.");

            // Get Profile

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

            // Get photos

            simpleFacebook.getPhotos(new OnPhotosListener() {
                @Override
                public void onComplete(List<Photo> response) {
                    Log.v(TAG, "Number of photos: " + response.size());

                    for(Photo photo : response) {
                        Log.v(TAG, photo.getLink());
                    }
                }
            });

            // Get Albums

            simpleFacebook.getAlbums(new OnAlbumsListener() {
                @Override
                public void onComplete(List<Album> response) {
                    Log.v(TAG, "Number of albums: " + response.size());

                    for(Album album : response) {
                        Log.v(TAG, album.getName());
                    }
                }
            });

            // Get Accounts

            simpleFacebook.getAccounts(new OnAccountsListener() {
                @Override
                public void onComplete(List<Account> response) {
                    Log.v(TAG, "Number of accounts: " + response.size());
                }
            });

            // Get Posts

            simpleFacebook.getPosts(new OnPostsListener() {
                @Override
                public void onComplete(List<Post> response) {
                    Log.v(TAG, "Number of posts: " + response.size());
                }
            });

            simpleFacebook.getFriends(new OnFriendsListener() {
                @Override
                public void onComplete(List<Profile> response) {
                    Log.v(TAG, "Number of friends: " + response.size());
                }
            });
        }
        else {
            Log.v(TAG, "Facebook is not logged in. Unable to get information.");
        }
    }

    public void getTwitterInformation() {

        if(BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.TW_LOGIN, false)) {
            Log.v(TAG, "Twitter is logged in. Getting photos.");

            Status status = new Status() {
                @Override
                public Date getCreatedAt() {
                    return null;
                }

                @Override
                public long getId() {
                    return 0;
                }

                @Override
                public String getText() {
                    return null;
                }

                @Override
                public String getSource() {
                    return null;
                }

                @Override
                public boolean isTruncated() {
                    return false;
                }

                @Override
                public long getInReplyToStatusId() {
                    return 0;
                }

                @Override
                public long getInReplyToUserId() {
                    return 0;
                }

                @Override
                public String getInReplyToScreenName() {
                    return null;
                }

                @Override
                public GeoLocation getGeoLocation() {
                    return null;
                }

                @Override
                public Place getPlace() {
                    return null;
                }

                @Override
                public boolean isFavorited() {
                    return false;
                }

                @Override
                public boolean isRetweeted() {
                    return false;
                }

                @Override
                public int getFavoriteCount() {
                    return 0;
                }

                @Override
                public User getUser() {
                    return null;
                }

                @Override
                public boolean isRetweet() {
                    return false;
                }

                @Override
                public Status getRetweetedStatus() {
                    return null;
                }

                @Override
                public long[] getContributors() {
                    return new long[0];
                }

                @Override
                public int getRetweetCount() {
                    return 0;
                }

                @Override
                public boolean isRetweetedByMe() {
                    return false;
                }

                @Override
                public long getCurrentUserRetweetId() {
                    return 0;
                }

                @Override
                public boolean isPossiblySensitive() {
                    return false;
                }

                @Override
                public String getLang() {
                    return null;
                }

                @Override
                public Scopes getScopes() {
                    return null;
                }

                @Override
                public String[] getWithheldInCountries() {
                    return new String[0];
                }

                @Override
                public long getQuotedStatusId() {
                    return 0;
                }

                @Override
                public Status getQuotedStatus() {
                    return null;
                }

                @Override
                public int compareTo(Status another) {
                    return 0;
                }

                @Override
                public UserMentionEntity[] getUserMentionEntities() {
                    return new UserMentionEntity[0];
                }

                @Override
                public URLEntity[] getURLEntities() {
                    return new URLEntity[0];
                }

                @Override
                public HashtagEntity[] getHashtagEntities() {
                    return new HashtagEntity[0];
                }

                @Override
                public MediaEntity[] getMediaEntities() {
                    return new MediaEntity[0];
                }

                @Override
                public ExtendedMediaEntity[] getExtendedMediaEntities() {
                    return new ExtendedMediaEntity[0];
                }

                @Override
                public SymbolEntity[] getSymbolEntities() {
                    return new SymbolEntity[0];
                }

                @Override
                public RateLimitStatus getRateLimitStatus() {
                    return null;
                }

                @Override
                public int getAccessLevel() {
                    return 0;
                }
            };

            MediaEntity[] media = status.getMediaEntities();

            for(MediaEntity medium : media) {
                Log.v(TAG, medium.getMediaURL());
            }
        }
        else {
            Log.v(TAG, "Something went wrong on retrieving Twitter photos");
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

                            try {
                                Log.e(TAG, "Text: " + mediaFeedData.getCaption().getText());
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
