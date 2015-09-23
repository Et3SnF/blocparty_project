package com.ngynstvn.android.blocparty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.LoginAdapter;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;

import java.util.List;

/**
 * Created by Ngynstvn on 9/21/15.
 */

public class MainActivity extends AppCompatActivity implements LoginAdapter.LoginAdapterDelegate {

    private static final String TAG = "(" + MainActivity.class.getSimpleName() + "): ";

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;

    private SimpleFacebook simpleFacebook;

    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;

    // ----- Lifecycle Methods ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        simpleFacebook = SimpleFacebook.getInstance(this);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocparty);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_insert_photo_white_24dp);

        loginAdapter = new LoginAdapter();
        loginAdapter.setLoginAdapterDelegate(this);
        recyclerView = (RecyclerView) findViewById(R.id.rv_login_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(loginAdapter);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult() called");
        super.onActivityResult(requestCode, resultCode, data);
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
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
     *
     * LoginAdapter.LoginAdapterDelegate implemented methods
     *
     */

    @Override
    public void onFBLoginClicked(LoginAdapter loginAdapter) {
        fbLogin(simpleFacebook);
    }

    @Override
    public void onFBDismissClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onTwitterLoginClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onTwitterDismissClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onIGLoginClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onIGDismissClicked(LoginAdapter loginAdapter) {

    }

    // ----- Separate Methods ----- //

    private void fbLogin(SimpleFacebook simpleFacebook) {
        final OnLoginListener onLoginListener = new OnLoginListener() {
            @Override
            public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                Log.i(TAG, "Logged in");
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Login Cancelled");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "Login Exception");
            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "Login Failed");
            }
        };

        simpleFacebook.login(onLoginListener);
    }
}
