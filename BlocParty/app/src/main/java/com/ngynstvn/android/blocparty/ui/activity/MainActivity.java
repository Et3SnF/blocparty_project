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
import com.ngynstvn.android.blocparty.ui.adapter.PostItemAdapter;
import com.sromku.simple.fb.SimpleFacebook;

import org.jinstagram.Instagram;

import java.lang.ref.WeakReference;

import twitter4j.Twitter;

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
    private boolean isIGObjectValid;

    private PostItemAdapter postItemAdapter;

    private boolean isFBLoggedIn;
    private boolean isTwLoggedIn;
    private boolean isIGLoggedIn;

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
            twitter = BPUtils.getSPrefObject(sharedPreferences, Twitter.class, BPUtils.TW_OBJECT);
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

        if(isTwLoggedIn && twitter != null) {
            BlocpartyApplication.getSharedDataSource().getTwitterInformation(twitter);
        }

        if(isIGLoggedIn && instagram != null) {
            if(instagram != null) {
                BlocpartyApplication.getSharedDataSource().getInstagramInformation(instagram);
            }
            else {
                Log.e(TAG, "Instagram variable is null. Unable to fetch feed");
            }
        }

        if(isFBLoggedIn && simpleFacebook != null) {
            BlocpartyApplication.getSharedDataSource().getFacebookInformation(simpleFacebook);
        }

        BlocpartyApplication.getSharedDataSource().fetchAllPostItems();

        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postItemAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BlocpartyApplication.getSharedDataSource().fetchAllPostItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
