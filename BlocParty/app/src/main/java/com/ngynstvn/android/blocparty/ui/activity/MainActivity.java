package com.ngynstvn.android.blocparty.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.fragment.LoginFragment;
import com.ngynstvn.android.blocparty.ui.fragment.TwitterAuthFragment;
import com.sromku.simple.fb.SimpleFacebook;

/**
 * Created by Ngynstvn on 9/21/15.
 */

public class MainActivity extends AppCompatActivity implements TwitterAuthFragment.TwitterAuthFragDelegate {
    private static final String TAG = "(" + MainActivity.class.getSimpleName() + "): ";
    private static int instance_counter = 0;

    private TwitterAuthFragment twitterAuthFragment;

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;

    private SimpleFacebook simpleFacebook;
    private SharedPreferences sharedPreferences;

    private TextView welcomeMessage;

    // ----- Lifecycle Methods ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocparty);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_insert_photo_white_24dp);

        if(savedInstanceState != null) {
            savedInstanceState.getInt("counter");
        }

        sharedPreferences = getSharedPreferences("log_states", 0);

        if(sharedPreferences != null) {
            sharedPreferences.getInt("counter", 0);
        }

        twitterAuthFragment = new TwitterAuthFragment();
        twitterAuthFragment.setTwitterAuthFragDelegate(this);

        instance_counter++;

        welcomeMessage = (TextView) findViewById(R.id.tv_login_message);

        if(instance_counter > 1) {
            welcomeMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
        displayLoginFragment();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult() called");
        super.onActivityResult(requestCode, resultCode, data);
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", instance_counter);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected() called");
        return super.onOptionsItemSelected(item);
    }

    /**
     * Display Login Fragment
     */

    private void displayLoginFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("login_fragment").replace(R.id.fl_activity_blocparty, LoginFragment.newInstance());
        fragmentTransaction.commit();

        /*
         * getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
         * LoginFragment.newInstance()).commit();
         *
         */
    }
}
