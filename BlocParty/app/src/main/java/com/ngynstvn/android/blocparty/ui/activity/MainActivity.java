package com.ngynstvn.android.blocparty.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.ngynstvn.android.blocparty.api.model.User;
import com.ngynstvn.android.blocparty.ui.adapter.PostItemAdapter;
import com.ngynstvn.android.blocparty.ui.fragment.CollectionModeDialog;
import com.sromku.simple.fb.SimpleFacebook;

import org.jinstagram.Instagram;

import java.lang.ref.WeakReference;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 10/14/15.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = BPUtils.classTag(MainActivity.class);

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocparty);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_insert_photo_white_24dp);

        postItemAdapter = new PostItemAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.rv_main_fragment);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_main_fragment);

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
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
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

        BlocpartyApplication.getSharedDataSource().displayPostItems();
        linearLayoutManager = new LinearLayoutManager(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BlocpartyApplication.getSharedDataSource().displayPostItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

                PostItem postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList().get(firstVisibleItem);

                User user = new User(0); // dummy argument. it doesn't matter for DB insertion

                user.setUserFullName(postItem.getOpFullName());
                user.setUserProfilePicUrl(postItem.getOpProfilePicUrl());
                user.setUserProfileId(postItem.getOpProfileId());
                user.setCollectionId(0); // default collection ID

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

        if(!BlocpartyApplication.getSharedDataSource().isDBEmpty(BPUtils.POST_ITEM_TABLE)) {
            BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
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

            return true;
        }

        if(item.getItemId() == R.id.action_login_mode) {
            Log.v(TAG, "Login button clicked");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            // Clear the DB, ArrayList, and ViewHolder once you're here so everything has a fresh start

            BlocpartyApplication.getSharedDataSource().clearTable(BPUtils.POST_ITEM_TABLE);
            BlocpartyApplication.getSharedDataSource().getPostItemArrayList().clear();
            postItemAdapter.notifyDataSetChanged();

            return true;
        }

        if(item.getItemId() == R.id.action_collection_mode) {
            Log.v(TAG, "Collection Button Clicked");
            showCollectionModeDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCollectionModeDialog() {
        CollectionModeDialog collectionModeDialog = CollectionModeDialog.newInstance();
        collectionModeDialog.show(getFragmentManager(), "collection_mode_dialog");
    }
}
