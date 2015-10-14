package com.ngynstvn.android.blocparty.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.fragment.IGAuthFragment;
import com.ngynstvn.android.blocparty.ui.fragment.LoginFragment;
import com.ngynstvn.android.blocparty.ui.fragment.MainFragment;
import com.ngynstvn.android.blocparty.ui.fragment.TwitterAuthFragment;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import org.jinstagram.Instagram;
import org.jinstagram.auth.exceptions.OAuthException;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/21/15.
 */

public class MainActivity extends AppCompatActivity implements TwitterAuthFragment.TwitterAuthFragDelegate,
        LoginFragment.LoginFragmentDelegate, MainFragment.MainFragmentDelegate {

    private static final String TAG = "(" + MainActivity.class.getSimpleName() + "): ";
    private static int instance_counter = 0;

    private TwitterAuthFragment twitterAuthFragment;
    private LoginFragment loginFragment;

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;

    private SharedPreferences sharedPreferences;

    // Facebook Static Variables

    private static SimpleFacebook simpleFacebook;

    // Twitter Static Variables

    private static Twitter twitter;
    private static TwitterFactory twitterFactory;
    private static ConfigurationBuilder configurationBuilder;
    private static String twConsumerKey;
    private static String twConsumerSecret;
    private static String twToken;
    private static String twTokenSecret;
    private boolean isTWAcctRegistered;

    // Instagram Static Variables

    private static final Token EMPTY_TOKEN = null;
    private static InstagramService instagramService;
    private static Instagram instagram;
    private static String igAuthCode;

    private TextView welcomeMessage;

    /**
     *
     * CALLBACK INTERFACE
     *
     */

    private interface Authoritative {
        void onSuccess();
        void onFailure();
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

        if(savedInstanceState != null) {
            String token = sharedPreferences.getString(BPUtils.IG_TOKEN, "");
            instagram = new Instagram(token);
            Log.v(TAG, instagram.getClientId());
        }

        isTWAcctRegistered = BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.IS_TW_ACCT_REG, false);

        configurationBuilder = new ConfigurationBuilder();
        instagramService = BlocpartyApplication.getSharedInstagramService();
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

        displayLoginFragment();

        simpleFacebook = SimpleFacebook.getInstance(this);

        // Must place sharedPreference here for it to save states properly
        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

        // Resume any Twitter activity when back to this fragment

        if(!isTWAcctRegistered && sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null) != null &&
                sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null) != null) {

            String twConsumerKey = sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null);
            String twConsumerSecret = sharedPreferences.getString(BPUtils.TW_CONSUMER_SECRET, null);
            String twToken = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
            String twTokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

            TwitterFactory twitterFactory = new TwitterFactory(getConfiguration(twConsumerKey, twConsumerSecret,
                    twToken, twTokenSecret));

            twitter = twitterFactory.getInstance();

            isTWAcctRegistered = true;

            BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IS_TW_ACCT_REG, isTWAcctRegistered);
        }

        if(isTwitterConnected() && isTWAcctRegistered) {
            if(twitter == null) {
                String twConsumerKey = sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null);
                String twConsumerSecret = sharedPreferences.getString(BPUtils.TW_CONSUMER_SECRET, null);
                String twToken = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
                String twTokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

                twitterFactory = new TwitterFactory(getConfiguration(twConsumerKey, twConsumerSecret,
                        twToken, twTokenSecret));

                twitter = twitterFactory.getInstance();
            }
        }

        // Resume any Instagram activity

        if(isIGLoggedIn()) {
            instagram = new Instagram(getString(R.string.igc));
        }
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

        if(twConsumerKey != null && twConsumerSecret != null && twToken != null && twTokenSecret != null) {
            outState.putString("twConsumerKey", twConsumerKey);
            outState.putString("twConsumerSecret", twConsumerSecret);
            outState.putString("twToken", twToken);
            outState.putString("twTokenSecret", twTokenSecret);
        }

        if(igAuthCode != null) {
            outState.putString("igAuthCode", igAuthCode);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();

        // Store anything in the event the user goes to home screen

        BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                BPUtils.TW_CONSUMER_KEY, twConsumerKey);
        BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                BPUtils.TW_CONSUMER_SECRET, twConsumerSecret);

        if(twToken != null && twConsumerSecret!= null) {
            BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                    BPUtils.TW_ACCESS_TOKEN, twToken);
            BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                    BPUtils.TW_ACCESS_TOKEN_SECRET, twTokenSecret);
        }

        // Store anything related to instagram here.

        if(igAuthCode != null) {
            Log.v(TAG, "igAuthCode stored: " + igAuthCode);
            BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                    BPUtils.IG_AUTH_CODE, igAuthCode);
        }
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

        BPUtils.delSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                BPUtils.IG_AUTH_CODE);
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
            this.menuItem = item;
            return true;
        }

        if(item.getItemId() == R.id.action_login_mode) {
            Log.v(TAG, "Login button clicked");
            this.menuItem = item;
            displayLoginFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * LoginFragment.LoginFragmentDelegate Methods
     *
     */

    @Override
    public void onFBLogin(LoginFragment loginFragment, final int adapterPosition) {
        fbLogin(simpleFacebook, adapterPosition, new OnLoginListener() {
            @Override
            public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                Log.i(TAG, "Logged into Facebook");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, true);

                Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged into Facebook",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Facebook login Cancelled");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
            }

            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "Facebook Login Exception!");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "Facebook Login Failed");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
            }
        });
    }

    @Override
    public void onFBLogout(LoginFragment loginFragment, final int adapterPosition) {
        if(simpleFacebook.isLogin()) {
            fbLogout(simpleFacebook, adapterPosition, new OnLogoutListener() {
                @Override
                public void onLogout() {
                    BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                            BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
                    Log.i(TAG, "Logged out of Facebook");

                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged out of Facebook",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onTWLogin(final LoginFragment loginFragment, final int adapterPosition) {
        twitterLogin(new Authoritative() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Logged into Twitter");
                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, true);
            }

            @Override
            public void onFailure() {
                Log.v(TAG, "Unable to log into Twitter");
                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, false);
            }
        });
    }

    @Override
    public void onTWLogout(LoginFragment loginFragment, int adapterPosition) {
        if(isTwitterConnected()) {
            BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
            BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);

            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                    BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, false);

            Log.v(TAG, "Logged out of Twitter");

            Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged out of Twitter",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onIGLogin(LoginFragment loginFragment, final int adapterPosition) {
        igLogin(new Authoritative() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "Logged into Instagram");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.IG_POSITION, adapterPosition, BPUtils.IG_LOGIN, true);
            }

            @Override
            public void onFailure() {
                Log.v(TAG, "Unable to log into Instagram");
                BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
            }
        });
    }

    @Override
    public void onIGLogout(LoginFragment loginFragment, int adapterPosition) {
        if(isIGLoggedIn()) {
            igLogout();
            Log.v(TAG, "Logged out of Instagram");
            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                    BPUtils.IG_POSITION, adapterPosition, BPUtils.IG_LOGIN, false);
        }
    }

    /**
     *
     * MainFragment.MainFragmentDelegate Implemented Methods
     *
     */

    @Override
    public void onPostItemsRefreshed(MainFragment mainFragment) {
        loadPostItems();
    }

    /**
     *
     * All Social Network methods
     *
     */

    // ----- Facebook Methods ----- //

    private void fbLogin(SimpleFacebook simpleFacebook, int adapterPosition, OnLoginListener onLoginListener) {
        simpleFacebook.login(onLoginListener);
    }

    private void fbLogout(SimpleFacebook simpleFacebook, int adapterPosition, OnLogoutListener onLogoutListener) {
        simpleFacebook.logout(onLogoutListener);
    }

    // ----- Twitter Methods ----- //

    private void twitterLogin(final Authoritative authoritative) {
        Log.v(TAG, "twitterLogin() called");

        if(isTwitterConnected()) {
            authoritative.onSuccess();
        }
        else {
            getAccessToken();
        }
    }

    private void getAccessToken() {
        Log.v(TAG, "getAccessToken() called");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                if (sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null) == null) {
                    BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_CONSUMER_KEY,
                            getString(R.string.tck));
                    BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME,
                            BPUtils.TW_CONSUMER_SECRET, getString(R.string.tcs));
                }

                try {

                    twConsumerKey = sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null);
                    twConsumerSecret = sharedPreferences.getString(BPUtils.TW_CONSUMER_SECRET, null);

                    Twitter twitter = new TwitterFactory(getConfiguration(twConsumerKey,
                            twConsumerSecret, null, null)).getInstance();

                    RequestToken requestToken = twitter.getOAuthRequestToken(getString(R.string.tcu));

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
                            TwitterAuthFragment.newInstance(requestToken.getAuthorizationURL()),
                            "tw_auth_fragment").commit();

                    return null;
                }
                catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                } catch (NullPointerException e) {
                    Log.v(TAG, "Unable to activate Twitter");
                    return null;
                }
            }
        }.execute();
    }

    private Configuration getConfiguration(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        configurationBuilder
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);

        return configurationBuilder.build();
    }

    private boolean isTwitterConnected() {
        Log.v(TAG, "isTwitterConnected() called");
        return sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false);
    }

    // ----- Instagram Methods ----- //

    private void igLogin(final Authoritative authoritative) {
        Log.v(TAG, "igLogin() called");

        igAuthCode = sharedPreferences.getString(BPUtils.IG_AUTH_CODE, null);

        Log.v(TAG, "Current code: " + igAuthCode);

        if(igAuthCode == null) {
            Log.v(TAG, "Current code is null. Getting another IG Access Token");
            getIGAccessToken();
            return;
        }

        new AsyncTask<Void, Void, Token>() {
            @Override
            protected Token doInBackground(Void... params) {
                try {
                    Verifier verifier = new Verifier(igAuthCode);
                    return instagramService.getAccessToken(EMPTY_TOKEN, verifier);
                }
                catch(OAuthException e) {
                    Log.e(TAG, "There was an issue extracting the access token. Trying again...");
                    authoritative.onFailure();
                    getIGAccessToken();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Token accessToken) {
                instagram = new Instagram(accessToken);
                authoritative.onSuccess();
            }
        }.execute();
    }

    private void getIGAccessToken() {
        Log.v(TAG, "getIGAccessToken() called");
        final String authorizationURL = instagramService.getAuthorizationUrl(EMPTY_TOKEN);
        Log.v(TAG, authorizationURL);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getFragmentManager().beginTransaction().addToBackStack("ig_auth_fragment")
                        .replace(R.id.fl_activity_blocparty, IGAuthFragment.newInstance(authorizationURL),
                                "ig_auth_fragment").commit();
                return null;
            }
        }.execute();
    }

    private void igLogout() {
        Log.v(TAG, "igLogout() called");
        BPUtils.delSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.IG_AUTH_CODE);
    }

    private boolean isIGLoggedIn() {
        Log.v(TAG, "isIGLoggedIn() called");
        return BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getString(BPUtils.IG_AUTH_CODE, null) != null;
    }

    /**
     * Display Login Fragment
     */

    private void displayLoginFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("login_fragment").replace(R.id.fl_activity_blocparty,
                LoginFragment.newInstance(), "login_fragment");
        fragmentTransaction.commit();
    }

    /**
     * Display Main Fragment
     */

    private void displayMainFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("main_fragment").replace(R.id.fl_activity_blocparty,
                MainFragment.newInstance(), "main_fragment");
        fragmentTransaction.commit();
    }

    /**
     *
     * Load the Items
     *
     */

    public void loadPostItems() {
        Log.v(TAG, "loadPostItems() called");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if(sharedPreferences.getBoolean(BPUtils.FB_LOGIN, false)) {
                    BlocpartyApplication.getSharedDataSource().getFacebookInformation(simpleFacebook);
                }

                if(sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false)) {
                    BlocpartyApplication.getSharedDataSource().getTwitterInformation(twitter);
                }

                if(sharedPreferences.getBoolean(BPUtils.IG_LOGIN, false)) {
                    BlocpartyApplication.getSharedDataSource().getInstagramInformation(instagram);
                }

                BlocpartyApplication.getSharedDataSource().fetchAllPostItems();
                return null;
            }
        }.execute();
    }

    /**
     *
     * Back pressing Method
     *
     */

    @Override
    public void onBackPressed() {
        final IGAuthFragment igAuthFragment = (IGAuthFragment) getFragmentManager().findFragmentByTag("ig_auth_fragment");
        final TwitterAuthFragment twitterAuthFragment = (TwitterAuthFragment) getFragmentManager().findFragmentByTag("tw_auth_fragment");

        try {
            if(igAuthFragment.isVisible()) {
                displayLoginFragment();
            } else if (twitterAuthFragment.isVisible()) {
                displayLoginFragment();
            }
        }
        catch (NullPointerException e) {
            Log.v(TAG, "Some backpressed fragment was null...heading back to a fragment...");
            displayLoginFragment();
        }
    }
}
