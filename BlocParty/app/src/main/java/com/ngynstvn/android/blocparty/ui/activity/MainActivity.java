package com.ngynstvn.android.blocparty.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.ngynstvn.android.blocparty.api.model.User;
import com.ngynstvn.android.blocparty.ui.adapter.PostItemAdapter;
import com.ngynstvn.android.blocparty.ui.fragment.CollectionModeDialog;
import com.sromku.simple.fb.SimpleFacebook;

import org.jinstagram.Instagram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 10/14/15.
 */

public class MainActivity extends AppCompatActivity implements PostItemAdapter.PostItemAdapterDelegate {

    private static final String TAG = BPUtils.classTag(MainActivity.class);

    private int instance_counter = 0;

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;

    private boolean isFilterActive = false;

    // All Posts Fields here

    private LinearLayout allPostsLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Filtered Posts Fields here

    private LinearLayout filteredPostsLayout;
    // SRL and RV apply here
    private CircleImageView topLeftImage;
    private CircleImageView topRightImage;
    private CircleImageView botLeftImage;
    private CircleImageView botRightImage;
    private TextView collectionName;
    private Button closeFilteredButton;

    // ---- Other Fields ---- //

    private SharedPreferences sharedPreferences;

    // Social Media Instances for this Activity

    private static SimpleFacebook simpleFacebook;
    private static Twitter twitter;
    private static Instagram instagram;

    private PostItemAdapter postItemAdapter;

    private boolean isFBLoggedIn;
    private boolean isTwLoggedIn;
    private boolean isIGLoggedIn;

    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 3;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private LinearLayoutManager linearLayoutManager;

    private static PostItem postItem;

    private boolean isColDialogActive = false;

    private static String currentCollectionName = null;

    /*
     * Interface Material
     */

    public interface MainActivityDelegate {
        void onPostItemsRefreshed(MainActivity mainActivity);
    }

    private WeakReference<MainActivityDelegate> mainActivityDelegate;

    public void setMainActivityDelegate(MainActivityDelegate mainActivityDelegate) {
        this.mainActivityDelegate = new WeakReference<MainActivityDelegate>(mainActivityDelegate);
    }

    public MainActivityDelegate getMainActivityDelegate() {
        if(mainActivityDelegate == null) {
            return null;
        }

        return mainActivityDelegate.get();
    }

    // ----- Lifecycle Methods ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tb_activity_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_insert_photo_white_24dp);

        isColDialogActive = getIntent().getBooleanExtra("show_dialog", false);

        postItemAdapter = new PostItemAdapter();
        postItemAdapter.setPostItemAdapterDelegate(this);

        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);
        isFBLoggedIn = sharedPreferences.getBoolean(BPUtils.FB_LOGIN, false);
        isTwLoggedIn = sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false);
        isIGLoggedIn = sharedPreferences.getBoolean(BPUtils.IG_LOGIN, false);

        // Get any instances!

        if(isFBLoggedIn) {
            simpleFacebook = BPUtils.getSPrefObject(sharedPreferences, SimpleFacebook.class, BPUtils.FB_OBJECT);
        }

        if(isTwLoggedIn) {
//            twitter = BPUtils.getSPrefObject(sharedPreferences, Twitter.class, BPUtils.TW_OBJECT);

            String consumerKey = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                    .getString(BPUtils.TW_CONSUMER_KEY, null);
            String consumerKeySecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                    .getString(BPUtils.TW_CONSUMER_SECRET, null);
            String token = getString(R.string.tat);
            String tokenSecret = getString(R.string.tats);

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

            configurationBuilder
                    .setOAuthConsumerKey(consumerKey)
                    .setOAuthConsumerSecret(consumerKeySecret)
                    .setOAuthAccessToken(token)
                    .setOAuthAccessTokenSecret(tokenSecret);

            TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
            twitter = twitterFactory.getInstance();
        }

        if(isIGLoggedIn) {
            instagram = BPUtils.getSPrefObject(sharedPreferences, Instagram.class, BPUtils.IG_OBJECT);
        }

        // Inflate initial views

        allPostsLayout = (LinearLayout) findViewById(R.id.ll_all_user_posts);
        filteredPostsLayout = (LinearLayout) findViewById(R.id.ll_filtered_user_posts);
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
        currentCollectionName = sharedPreferences.getString(BPUtils.CURRENT_COLLECTION, null);
        Log.v(TAG, "Current collection name: " + currentCollectionName);
        isFilterActive = (currentCollectionName != null);
        Log.v(TAG, "Is Filter Active?: " + isFilterActive);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(this);

        if(!isFBLoggedIn && !isTwLoggedIn && !isIGLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if(isFBLoggedIn && simpleFacebook != null) {
            BlocpartyApplication.getSharedDataSource().fetchFacebookInformation(simpleFacebook);
        }

        if(isTwLoggedIn && twitter != null) {
            BlocpartyApplication.getSharedDataSource().fetchTwitterInformation(twitter);
        }

        if(isIGLoggedIn && instagram != null) {
            if(instagram != null) {
                BlocpartyApplication.getSharedDataSource().fetchInstagramInformation(instagram);
            }
            else {
                Log.e(TAG, "Instagram variable is null. Unable to fetch feed");
            }
        }

        if(isColDialogActive) {
            showCollectionModeDialog();
            isColDialogActive = false;
        }

        /**
         *
         * Views Area
         *
         */

        if(!isFilterActive) {

            // Disable this layout until it is needed
            allPostsLayout.setEnabled(true);
            allPostsLayout.setVisibility(View.VISIBLE);
            filteredPostsLayout.setEnabled(false);
            filteredPostsLayout.setVisibility(View.GONE);

            // Inflate views here

            recyclerView = (RecyclerView) findViewById(R.id.rv_activity_main_all);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_activity_main_all);
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.material_indigo_500));

            linearLayoutManager = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(postItemAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

//                Log.v(TAG, "VisibleItemCount: " + visibleItemCount);
//                Log.v(TAG, "TotalItemCount: " + totalItemCount);
                    Log.v(TAG, "Visible Item Position: " + firstVisibleItem);

                    if (firstVisibleItem != -1) {
                        postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList().get(firstVisibleItem);
                    }

                    User user = new User(0); // dummy argument. it doesn't matter for DB insertion

                    user.setUserFullName(postItem.getOpFullName());
                    user.setUserProfilePicUrl(postItem.getOpProfilePicUrl());
                    user.setUserProfileId(postItem.getOpProfileId());

                    if (postItem.getPostImageUrl().contains("https://scontent.cdninstagram.com/hphotos")) {
                        user.setUserSocNetwork("Instagram");
                    } else if (postItem.getPostImageUrl().contains("http://pbs.twimg.com")) {
                        user.setUserSocNetwork("Twitter");
                    } else if (postItem.getPostImageUrl().contains("fbcdn.net")) {
                        user.setUserSocNetwork("Facebook");
                    }

                    BlocpartyApplication.getSharedDataSource().addUserToDB(user);

//                if (loading) {
//
//                    // If list is loading, stop it and set the previousTotal to the current list's total
//
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        previousTotal = totalItemCount;
//                        Log.v(TAG, "Previous Total: " + previousTotal);
//                    }
//                }
//
//                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//                    Log.v(TAG, "End has been reached...loading more...");
//                    postItemAdapter.notifyDataSetChanged();
//                    loading = true;
//                }
//
//                Log.v(TAG, "totalItemCount > previousTotal: " + totalItemCount + " > " + previousTotal);
//                Log.v(TAG, "(totalItemCount - visibleItemCount) <= (firstVisibleItem + " +
//                        "visibleThreshold): " + (totalItemCount - visibleItemCount) + " <= "
//                        + (firstVisibleItem + visibleThreshold));
//                Log.v(TAG, "Current loading state: " + loading);
                }
            });

            BlocpartyApplication.getSharedDataSource().fetchAllPostItems();
            postItemAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    BlocpartyApplication.getSharedDataSource().fetchAllPostItems();
                    postItemAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });


            if(!BlocpartyApplication.getSharedDataSource().isDBEmpty(BPUtils.POST_ITEM_TABLE)) {
                BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
            }
        }
        else {
            // Disable this layout until it is needed
            allPostsLayout.setEnabled(false);
            allPostsLayout.setVisibility(View.GONE);
            filteredPostsLayout.setEnabled(true);
            filteredPostsLayout.setVisibility(View.VISIBLE);

            // Inflate views here

            recyclerView = (RecyclerView) findViewById(R.id.rv_activity_main_filtered);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_activity_main_filtered);
            topLeftImage = (CircleImageView) findViewById(R.id.civ_top_left_pic_view);
            topRightImage = (CircleImageView) findViewById(R.id.civ_top_right_pic_view);
            botLeftImage = (CircleImageView) findViewById(R.id.civ_bot_left_pic_view);
            botRightImage = (CircleImageView) findViewById(R.id.civ_bot_right_pic_view);
            collectionName = (TextView) findViewById(R.id.tv_collection_name_view);
            closeFilteredButton = (Button) findViewById(R.id.btn_close_collection_view);

            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.material_indigo_500));

            linearLayoutManager = new LinearLayoutManager(this);

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(postItemAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

//                Log.v(TAG, "VisibleItemCount: " + visibleItemCount);
//                Log.v(TAG, "TotalItemCount: " + totalItemCount);
                    Log.v(TAG, "Visible Item Position: " + firstVisibleItem);

                    if (firstVisibleItem != -1) {
                        postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList().get(firstVisibleItem);
                    }

                    User user = new User(0); // dummy argument. it doesn't matter for DB insertion

                    user.setUserFullName(postItem.getOpFullName());
                    user.setUserProfilePicUrl(postItem.getOpProfilePicUrl());
                    user.setUserProfileId(postItem.getOpProfileId());

                    if (postItem.getPostImageUrl().contains("https://scontent.cdninstagram.com/hphotos")) {
                        user.setUserSocNetwork("Instagram");
                    } else if (postItem.getPostImageUrl().contains("http://pbs.twimg.com")) {
                        user.setUserSocNetwork("Twitter");
                    } else if (postItem.getPostImageUrl().contains("fbcdn.net")) {
                        user.setUserSocNetwork("Facebook");
                    }

                    BlocpartyApplication.getSharedDataSource().addUserToDB(user);

//                if (loading) {
//
//                    // If list is loading, stop it and set the previousTotal to the current list's total
//
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        previousTotal = totalItemCount;
//                        Log.v(TAG, "Previous Total: " + previousTotal);
//                    }
//                }
//
//                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//                    Log.v(TAG, "End has been reached...loading more...");
//                    postItemAdapter.notifyDataSetChanged();
//                    loading = true;
//                }
//
//                Log.v(TAG, "totalItemCount > previousTotal: " + totalItemCount + " > " + previousTotal);
//                Log.v(TAG, "(totalItemCount - visibleItemCount) <= (firstVisibleItem + " +
//                        "visibleThreshold): " + (totalItemCount - visibleItemCount) + " <= "
//                        + (firstVisibleItem + visibleThreshold));
//                Log.v(TAG, "Current loading state: " + loading);
                }
            });

            BlocpartyApplication.getSharedDataSource().fetchFilteredPostItems(currentCollectionName);
            postItemAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    BlocpartyApplication.getSharedDataSource().fetchFilteredPostItems(currentCollectionName);
                    postItemAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            collectionName.setText(currentCollectionName);

            closeFilteredButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Close Collection View button clicked");
                    isFilterActive = false;
                    allPostsLayout.setEnabled(true);
                    allPostsLayout.setVisibility(View.VISIBLE);
                    filteredPostsLayout.setEnabled(false);
                    filteredPostsLayout.setVisibility(View.GONE);

                    BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.CURRENT_COLLECTION);
                    BlocpartyApplication.getSharedDataSource().getPostItemArrayList().clear();
                    restartActivity();
                }
            });

            if(!BlocpartyApplication.getSharedDataSource().isDBEmpty(BPUtils.POST_ITEM_TABLE)) {
                BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }

    // -----   -----  -----  -----  ----- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "onCreateOptionsMenu() called");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected() called");

        if(item.getItemId() == R.id.action_camera_mode) {
            Log.v(TAG, "Camera button clicked");
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
            return true;
        }

        if(item.getItemId() == R.id.action_login_mode) {
            Log.v(TAG, "Login button clicked");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            // Clear the DB, ArrayList, and ViewHolder once you're here so everything has a fresh start

            BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
            BlocpartyApplication.getSharedDataSource().getPostItemArrayList().clear();
            postItemAdapter.notifyDataSetChanged();

            if(sharedPreferences != null) {
                instance_counter = sharedPreferences.getInt("counter", 0);
            }

            instance_counter++;
            BPUtils.putSPrefIntValue(sharedPreferences, BPUtils.FILE_NAME, "counter", instance_counter);

            return true;
        }

        if(item.getItemId() == R.id.action_collection_mode) {
            Log.v(TAG, "Collection Button Clicked");
            showCollectionModeDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ---- Other Methods ---- //

    private void showCollectionModeDialog() {
        CollectionModeDialog collectionModeDialog = CollectionModeDialog.newInstance();
        collectionModeDialog.show(getFragmentManager(), "collection_mode_dialog");
    }

    private void restartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     *
     * PostItemAdapterDelegate Implemented Methods
     *
     */

    @Override
    public void onPostItemImageDownloaded(PostItemAdapter postItemAdapter, int adapterPosition) {
        DownloadPostImageTask downloadPostImageTask = new DownloadPostImageTask(adapterPosition);
        downloadPostImageTask.start();
    }

    /**
     *
     * Download Post Image Task
     *
     */

    private class DownloadPostImageTask extends Thread {

        private int adapterPosition;

        public DownloadPostImageTask(int adapterPosition) {
            this.adapterPosition = adapterPosition;
        }

        @Override
        public void run() {
            BPUtils.logMethod(BPUtils.classTag(DownloadPostImageTask.class), getClass().getSimpleName());

            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String imageFileName = "IMAGE_BP_" + timeStamp + ".jpg";
            File storageDirectory = new File(Environment.getExternalStorageDirectory() + "/Blocparty/");

            if(!storageDirectory.exists()) {
                storageDirectory.mkdir();
            }

            PostItem postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList()
                    .get(adapterPosition);

            URL imageURL = null;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            File savedImageFile = null;
            FileOutputStream fileOutputStream = null;

            try {
                imageURL = new URL(postItem.getPostImageUrl());
                httpURLConnection = (HttpURLConnection) imageURL.openConnection();
                inputStream = httpURLConnection.getInputStream();
                savedImageFile = new File(storageDirectory, imageFileName);
                fileOutputStream = new FileOutputStream(savedImageFile);

                int read = -1;
                byte[] buffer = new byte[httpURLConnection.getContentLength()];

                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }

                httpURLConnection.disconnect();
                inputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();

                // Make the image accessible via the System's media provider so that the user
                // can find the image in the app that contains all of the pictures (Gallery)

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri savedImageUri = Uri.fromFile(savedImageFile);
                mediaScanIntent.setData(savedImageUri);
                MainActivity.this.sendBroadcast(mediaScanIntent);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.displayDialog(MainActivity.this, "The image has been successfully saved.");
                    }
                });
            }
            catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.displayDialog(MainActivity.this, "There was an issue downloading the image. Try again.");
                    }
                });
            }
        }
    }
}
