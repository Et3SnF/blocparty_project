package com.ngynstvn.android.blocparty.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.List;

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
}
