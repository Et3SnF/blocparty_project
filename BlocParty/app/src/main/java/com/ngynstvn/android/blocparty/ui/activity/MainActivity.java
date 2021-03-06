package com.ngynstvn.android.blocparty.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
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
import com.ngynstvn.android.blocparty.api.DataSource;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.ngynstvn.android.blocparty.ui.adapter.PostItemAdapter;
import com.ngynstvn.android.blocparty.ui.fragment.CollectionModeDialog;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Like;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.jinstagram.Instagram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by Ngynstvn on 10/14/15.
 */

public class MainActivity extends AppCompatActivity implements PostItemAdapter.PostItemAdapterDelegate,
        PostItemAdapter.DataSource {

    private static final String CLASS_TAG = BPUtils.classTag(MainActivity.class);

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

    private static SimpleFacebook simpleFacebook = null;
    private static Twitter twitter = null;
    private static Instagram instagram = null;

    private static boolean isFBLoggedIn = false;
    private static boolean isTwLoggedIn = false;
    private static boolean isIGLoggedIn = false;

    private PostItemAdapter postItemAdapter;

    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 3;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private static int lastItemPosition = 0;

    private static PostItem postItem;
    private static ArrayList<PostItem> currentPostItems;
    private static int fetchingPosition = 0;
    private static int latestListSize = 0;

    private boolean isColDialogActive = false;
    private static String currentCollectionName = null;

    private Handler dataHandler;

    // ----- Lifecycle Methods ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(CLASS_TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tb_activity_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_insert_photo_white_24dp);

        // For Android 6.0 or greater, read/write permissions still needs to be requested
        // here even if it is already declared in AndroidManifest

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            BPUtils.requestPermission(MainActivity.this, Manifest.permission.CAMERA);
            BPUtils.requestPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        DataBackgroundThread dataBackgroundThread = new DataBackgroundThread();
        dataBackgroundThread.start();

        isColDialogActive = getIntent().getBooleanExtra("show_dialog", false);

        postItemAdapter = new PostItemAdapter();
        postItemAdapter.setPostItemAdapterDelegate(this);
        postItemAdapter.setDataSource(this);

        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

        isFBLoggedIn = sharedPreferences.getBoolean(BPUtils.FB_LOGIN, false);
        isTwLoggedIn = sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false);
        isIGLoggedIn = sharedPreferences.getBoolean(BPUtils.IG_LOGIN, false);

        // Get any instances!

        simpleFacebook = BPUtils.getSPrefObject(sharedPreferences, SimpleFacebook.class, BPUtils.FB_OBJECT);
        twitter = BPUtils.getSPrefObject(sharedPreferences, Twitter.class, BPUtils.TW_OBJECT);
        instagram = BPUtils.getSPrefObject(sharedPreferences, Instagram.class, BPUtils.IG_OBJECT);

        // Inflate initial views

        allPostsLayout = (LinearLayout) findViewById(R.id.ll_all_user_posts);
        filteredPostsLayout = (LinearLayout) findViewById(R.id.ll_filtered_user_posts);
    }

    @Override
    protected void onStart() {
        Log.e(CLASS_TAG, "onStart() called");
        super.onStart();
        currentCollectionName = sharedPreferences.getString(BPUtils.CURRENT_COLLECTION, null);
        Log.v(CLASS_TAG, "Current collection name: " + currentCollectionName);
        isFilterActive = (currentCollectionName != null);
        Log.v(CLASS_TAG, "Is Filter Active?: " + isFilterActive);
    }

    @Override
    protected void onResume() {
        Log.e(CLASS_TAG, "onResume() called");
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(this);

        if(!isFBLoggedIn && !isTwLoggedIn && !isIGLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if(isFBLoggedIn && simpleFacebook != null) {
            simpleFacebook = BPUtils.getSPrefObject(sharedPreferences, SimpleFacebook.class, BPUtils.FB_OBJECT);
        }

        if(isTwLoggedIn && twitter != null) {
            twitter = BPUtils.getSPrefObject(sharedPreferences, Twitter.class, BPUtils.TW_OBJECT);
        }

        if(isIGLoggedIn && instagram != null) {
            instagram = BPUtils.getSPrefObject(sharedPreferences, Instagram.class, BPUtils.IG_OBJECT);
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

            BlocpartyApplication.getSharedDataSource().fetchPostItems(new DataSource.Callback<ArrayList<PostItem>>() {
                @Override
                public void onFetchingComplete(final ArrayList<PostItem> postItems) {
                    if (currentPostItems.size() == 0) {
                        dataHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                BPUtils.logMethod(CLASS_TAG, "dataHandler");
                                currentPostItems.addAll(postItems);
                            }
                        });
                        latestListSize = currentPostItems.size();
                        fetchingPosition = latestListSize - 1; // Start
                        postItemAdapter.notifyItemRangeInserted(0, latestListSize);
                    }
                }
            });

            lastItemPosition = sharedPreferences.getInt(BPUtils.LAST_POST_ITEM_POSITION, 0);
            linearLayoutManager.scrollToPositionWithOffset(lastItemPosition, 0);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if(firstVisibleItem == fetchingPosition) {
                        BlocpartyApplication.getSharedDataSource().fetchMorePostItems(new DataSource
                                .Callback<ArrayList<PostItem>>() {
                            @Override
                            public void onFetchingComplete(final ArrayList<PostItem> postItems) {
                                Log.v(CLASS_TAG, "Loading more...");
                                dataHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        BPUtils.logMethod(CLASS_TAG, "dataHandler");
                                        currentPostItems.addAll(postItems);
                                    }
                                });
                                fetchingPosition += postItems.size();
                                Log.v(CLASS_TAG, "New fetching position: " + fetchingPosition);
                                postItemAdapter.notifyItemRangeInserted(latestListSize, currentPostItems.size());
                            }
                        }, fetchingPosition);
                    }
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    BlocpartyApplication.getSharedDataSource().fetchPostItems(new DataSource.Callback<ArrayList<PostItem>>() {
                        @Override
                        public void onFetchingComplete(ArrayList<PostItem> postItems) {
                            currentPostItems.clear();
                            currentPostItems.addAll(postItems);
                            postItemAdapter.notifyItemRangeChanged(0, currentPostItems.size());
                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
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

            latestListSize = 0;

            BlocpartyApplication.getSharedDataSource().fetchPostItems(new DataSource.Callback<ArrayList<PostItem>>() {
                @Override
                public void onFetchingComplete(ArrayList<PostItem> postItems) {
                    if (currentPostItems.size() == 0) {
                        currentPostItems.addAll(postItems);
                        latestListSize = currentPostItems.size();
                        fetchingPosition = latestListSize - 1; // Start
                        postItemAdapter.notifyItemRangeInserted(0, latestListSize);
                    }
                }
            });

            lastItemPosition = sharedPreferences.getInt(BPUtils.LAST_POST_ITEM_POSITION, 0);
            linearLayoutManager.scrollToPositionWithOffset(lastItemPosition, 0);

            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(postItemAdapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
//                    visibleItemCount = recyclerView.getChildCount();
//                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    // DON'T ADD USERS WHILE SCROLLING. THIS WILL CAUSE AN EXPONENTIAL AMOUNT OF CALLS
                    // AND TAKE UP ALL MEMORY --> Leading to crash
                }
            });

            BlocpartyApplication.getSharedDataSource().fetchFilteredPostItems(new DataSource.Callback<List<PostItem>>() {
                @Override
                public void onFetchingComplete(List<PostItem> postItems) {
                    currentPostItems.clear();
                    currentPostItems.addAll(postItems);
                    postItemAdapter.notifyDataSetChanged();
                }
            }, currentCollectionName);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    BlocpartyApplication.getSharedDataSource().fetchFilteredPostItems(new DataSource.Callback<List<PostItem>>() {
                        @Override
                        public void onFetchingComplete(List<PostItem> postItems) {
                            currentPostItems.clear();
                            currentPostItems.addAll(postItems);
                            postItemAdapter.notifyDataSetChanged();
                        }
                    }, currentCollectionName);
                    postItemAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            collectionName.setText(currentCollectionName);

            if(firstVisibleItem == fetchingPosition) {
                BlocpartyApplication.getSharedDataSource().fetchMoreFilteredPostItems(new DataSource.Callback<List<PostItem>>() {
                    @Override
                    public void onFetchingComplete(List<PostItem> postItems) {
                        Log.v(CLASS_TAG, "Loading more...");
                        currentPostItems.addAll(postItems);
                        fetchingPosition += postItems.size();
                        Log.v(CLASS_TAG, "New fetching position: " + fetchingPosition);
                        postItemAdapter.notifyItemRangeInserted(latestListSize, currentPostItems.size());
                    }
                }, currentCollectionName, fetchingPosition);
            }

            closeFilteredButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(CLASS_TAG, "Close Collection View button clicked");
                    isFilterActive = false;
                    allPostsLayout.setEnabled(true);
                    allPostsLayout.setVisibility(View.VISIBLE);
                    filteredPostsLayout.setEnabled(false);
                    filteredPostsLayout.setVisibility(View.GONE);

                    BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.CURRENT_COLLECTION);
                    currentPostItems.clear();
                    restartActivity();
                }
            });
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
        Log.e(CLASS_TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(CLASS_TAG, "onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(CLASS_TAG, "onDestroy() called");
        super.onDestroy();
        BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
    }

    // -----   -----  -----  -----  ----- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(CLASS_TAG, "onCreateOptionsMenu() called");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(CLASS_TAG, "onOptionsItemSelected() called");

        if(item.getItemId() == R.id.action_camera_mode) {
            Log.v(CLASS_TAG, "Camera button clicked");

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else {
                // If permission is granted for Marshmallow, open camera.
                if(ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                                .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, CameraActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else {
                    BPUtils.displayDialog(MainActivity.this, "Unable to open camera. Accept permissions and try again.");
                    BPUtils.requestPermission(MainActivity.this, Manifest.permission.CAMERA);
                }
            }

            return true;
        }

        if(item.getItemId() == R.id.action_login_mode) {
            Log.v(CLASS_TAG, "Login button clicked");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Clear the DB, ArrayList, and ViewHolder once you're here so everything has a fresh start

            BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
            currentPostItems.clear();
            postItemAdapter.notifyDataSetChanged();

            if(sharedPreferences != null) {
                instance_counter = sharedPreferences.getInt("counter", 0);
            }

            instance_counter++;
            BPUtils.putSPrefIntValue(sharedPreferences, BPUtils.FILE_NAME, "counter", instance_counter);

            return true;
        }

        if(item.getItemId() == R.id.action_collection_mode) {
            Log.v(CLASS_TAG, "Collection Button Clicked");
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     *
     * PostItemAdapter.PostItemAdapterDelegate Implemented Methods
     *
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPostItemImagePanZoomed(PostItemAdapter postItemAdapter, int adapterPosition) {
        BPUtils.putSPrefIntValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.LAST_POST_ITEM_POSITION,
                adapterPosition);
        PostItem postItem = currentPostItems.get(adapterPosition);
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra(BPUtils.POST_IMAGE_URL, postItem.getPostImageUrl());
        startActivity(intent);
    }

    @Override
    public void onPostItemImageDownloaded(PostItemAdapter postItemAdapter, int adapterPosition) {
        DownloadPostImageTask downloadPostImageTask = new DownloadPostImageTask(adapterPosition);
        downloadPostImageTask.start();
    }

    @Override
    public void onPostItemLiked(PostItemAdapter postItemAdapter, int adapterPosition, boolean isLiked) {
        LikePostItem likePostItem = new LikePostItem(adapterPosition, isLiked);
        likePostItem.start();
    }

    /**
     *
     * PostItemAdapter.Delegate Implemented Methods
     *
     */

    @Override
    public PostItem getPostItem(PostItemAdapter postItemAdapter, int position) {
        return currentPostItems.get(position);
    }

    @Override
    public int getItemCount(PostItemAdapter postItemAdapter) {
        return currentPostItems.size();
    }

    /**
     *
     * Data Background Thread
     *
     */

    private class DataBackgroundThread extends Thread {
        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "DataBackgroundThread");
            Looper.prepare();
            dataHandler = new Handler();
            currentPostItems = new ArrayList<>();
            Looper.loop();
        }
    }

    /**
     *
     * Post Item Liked Thread
     *
     */

    private class LikePostItem extends Thread {

        private int adapterPosition;
        private boolean isLiked;

        public LikePostItem(int adapterPosition, boolean isLiked) {
            this.adapterPosition = adapterPosition;
            this.isLiked = isLiked;
        }

        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG);

            // Set up the MessageQueue in case something requires it
            Looper.prepare();

            PostItem postItem = currentPostItems.get(adapterPosition);

            if(postItem.getPostImageUrl().contains("fbcdn.net")) {
                Log.v(CLASS_TAG, "Detected Facebook Like");

                OnPublishListener onPublishListener = new OnPublishListener() {
                    @Override
                    public void onComplete(String response) {
                        Log.v(CLASS_TAG, "Published Like Successfully");
                        Log.v(CLASS_TAG, "Response: " + response);
                    }

                    @Override
                    public void onFail(String reason) {
                        Log.e(CLASS_TAG, "There was an issue liking the Facebook post.");
                    }
                };

                Like like = new Like.Builder().build();
                Log.v(CLASS_TAG, "Post Item ID: " + postItem.getPostId());
                SimpleFacebook.getInstance().publish(String.valueOf(postItem.getPostId()), like,
                        onPublishListener);
            }
            else if(postItem.getPostImageUrl().contains("http://pbs.twimg.com")) {
                Log.v(CLASS_TAG, "Detected Twitter Favorite");

                TwitterFactory twitterFactory = new TwitterFactory();
                Twitter twitter = twitterFactory.getInstance();

                String consumerKey = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_CONSUMER_KEY, null);
                String consumerSecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_CONSUMER_SECRET, null);
                String token = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_ACCESS_TOKEN, "");
                String tokenSecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                        .getString(BPUtils.TW_ACCESS_TOKEN_SECRET, "");

                try {
                    twitter.setOAuthConsumer(consumerKey, consumerSecret);
                    AccessToken accessToken = new AccessToken(token, tokenSecret);
                    twitter.setOAuthAccessToken(accessToken);

                    if(isLiked) {
                        Log.v(CLASS_TAG, "Twitter Status Favorite Checked");
                        twitter.createFavorite(postItem.getPostId());
                    }
                    else {
                        Log.v(CLASS_TAG, "Twitter Status Unfavored Checked");
                        twitter.destroyFavorite(postItem.getPostId());
                    }
                }
                catch (TwitterException e) {
                    Log.v(CLASS_TAG, "There was and issue with favoring status.");
                    e.printStackTrace();
                }
            }
            else if(postItem.getPostImageUrl().contains("https://scontent.cdninstagram.com/hphotos")) {
                Log.v(CLASS_TAG, "Detected Instagram Heart");

                String mediaId = String.valueOf(postItem.getPostId()) + "_" + String.valueOf(postItem.getOpProfileId());
                String urlString = "https://api.instagram.com/v1/media/" + mediaId + "/likes";

//                try {
//                    if(isLiked) {
//                        instagram.setUserLike(mediaId);
//                        BlocpartyApplication.getSharedDataSource().updatePostItemLike(postItem.getPostId(), isLiked);
//                    }
//                    else {
//                        instagram.deleteUserLike(mediaId);
//                        BlocpartyApplication.getSharedDataSource().updatePostItemLike(postItem.getPostId(), !isLiked);
//                    }
//                }
//                catch (InstagramException e) {
//                    e.printStackTrace();
//                    BPUtils.displayDialog(MainActivity.this, "There was an issue hearting the " +
//                            "Instagram post. Try again later.");
//                    BlocpartyApplication.getSharedDataSource().updatePostItemLike(postItem.getPostId(), !isLiked);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            postItemAdapter.notifyItemChanged(adapterPosition);
//                        }
//                    });
//                }

                try {
                    // Establish initial connection material
                    URL url = new URL(urlString);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setConnectTimeout(15000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    // Access Token argument
                    Uri.Builder uriBuilder = new Uri.Builder()
                            .appendQueryParameter("access_token", instagram.getAccessToken().getToken());

                    String query = uriBuilder.build().getEncodedQuery();

                    // Necessary output information before connection
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write(query);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    // Connect the URL
                    httpURLConnection.connect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Looper.loop();
        }
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
            File storageDirectory = new File(BPUtils.IMG_FOLDER_PATH);

            if(!storageDirectory.exists()) {
                storageDirectory.mkdir();
            }

            PostItem postItem = currentPostItems.get(adapterPosition);

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
                        BPUtils.requestPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                });
            }
        }
    }
}
