package com.ngynstvn.android.blocparty.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.fragment.IGAuthFragment;
import com.ngynstvn.android.blocparty.ui.fragment.LoginFragment;
import com.ngynstvn.android.blocparty.ui.fragment.TwitterAuthFragment;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import org.jinstagram.Instagram;
import org.jinstagram.auth.exceptions.OAuthException;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.exceptions.InstagramException;

import java.lang.ref.WeakReference;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/21/15.
 */

public class LoginActivity extends AppCompatActivity implements TwitterAuthFragment.TwitterAuthFragDelegate,
        LoginFragment.LoginFragmentDelegate {

    private static final String CLASS_TAG = BPUtils.classTag(LoginActivity.class);
    private static int instance_counter = 0;

    private Toolbar toolbar;
    private Menu menu;
    private MenuItem menuItem;

    // Auth Handler
    private Handler authHandler;

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

    /*
     * Interface Material
     */

    public interface LoginActivityDelegate {

    }

    private WeakReference<LoginActivityDelegate> loginActivityDelegate;

    public void setMainActivityDelegate(LoginActivityDelegate loginActivityDelegate) {
        this.loginActivityDelegate = new WeakReference<LoginActivityDelegate>(loginActivityDelegate);
    }

    public LoginActivityDelegate getLoginActivityDelegate() {

        // Delegated to MainActivity.java

        if(loginActivityDelegate == null) {
            return null;
        }

        return loginActivityDelegate.get();
    }

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
        BPUtils.logMethod(CLASS_TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_blocparty_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_insert_photo_white_24dp);

        sharedPreferences = getSharedPreferences("log_states", 0);

        if(sharedPreferences != null) {
            instance_counter = sharedPreferences.getInt("counter", 0);
        }

        instance_counter++;

        isTWAcctRegistered = BPUtils.newSPrefInstance(BPUtils.FILE_NAME).getBoolean(BPUtils.IS_TW_ACCT_REG, false);
        configurationBuilder = new ConfigurationBuilder();

        instagramService = BlocpartyApplication.getSharedInstagramService();
    }

    @Override
    protected void onStart() {
        BPUtils.logMethod(CLASS_TAG);
        super.onStart();
    }

    @Override
    protected void onResume() {
        BPUtils.logMethod(CLASS_TAG);
        super.onResume();

        displayLoginFragment();

        AuthenticationThread authenticationThread = new AuthenticationThread();
        authenticationThread.start();

        simpleFacebook = SimpleFacebook.getInstance(this);
        BPUtils.putSPrefObject(sharedPreferences, BPUtils.FILE_NAME, BPUtils.FB_OBJECT, simpleFacebook);

        // Must place sharedPreference here for it to save states properly
        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BPUtils.logMethod(CLASS_TAG);
        super.onActivityResult(requestCode, resultCode, data);
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        BPUtils.logMethod(CLASS_TAG);
        super.onPause();

        // Store anything in the event the user goes to home screen
        preserveTwitterObject();
        preserveInstagramObject();
    }

    @Override
    protected void onStop() {
        BPUtils.logMethod(CLASS_TAG);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        BPUtils.logMethod(CLASS_TAG);
        super.onDestroy();

        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                BPUtils.IG_AUTH_CODE);
    }

    // -----   -----  -----  -----  ----- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        BPUtils.logMethod(CLASS_TAG);
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BPUtils.logMethod(CLASS_TAG);

        if(item.getTitle() == getString(R.string.log_in_text)) {

            boolean isFBLoggedIn = sharedPreferences.getBoolean(BPUtils.FB_LOGIN, false);
            boolean isTwLoggedIn = sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false);
            boolean isIGLoggedIn = sharedPreferences.getBoolean(BPUtils.IG_LOGIN, false);

            if(!isFBLoggedIn && !isTwLoggedIn && !isIGLoggedIn) {
                Toast.makeText(BlocpartyApplication.getSharedInstance(), "You must be logged into at " +
                        "least one account in order to proceed", Toast.LENGTH_SHORT).show();
                return false;
            }

            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Authentication Thread
     *
     */

    private class AuthenticationThread extends Thread {
        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "AuthenticationThread");
            Looper.prepare();
            authHandler = new Handler();

            if(isTwitterConnected()) {
                twitter = BPUtils.getSPrefObject(sharedPreferences, Twitter.class, BPUtils.TW_OBJECT);
            }

            if(isInstagramConnected()) {
                instagram = BPUtils.getSPrefObject(sharedPreferences, Instagram.class, BPUtils.IG_OBJECT);
            }

            Looper.loop();
        }
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
                Log.i(CLASS_TAG, "Logged into Facebook");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, true);
            }

            @Override
            public void onCancel() {
                Log.i(CLASS_TAG, "Facebook login Cancelled");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
            }

            @Override
            public void onException(Throwable throwable) {
                Log.i(CLASS_TAG, "Facebook Login Exception!");

                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
            }

            @Override
            public void onFail(String s) {
                Log.i(CLASS_TAG, "Facebook Login Failed");

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
                    Log.i(CLASS_TAG, "Logged out of Facebook");

                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged out of Facebook",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onTWLogin(final LoginFragment loginFragment, final int adapterPosition) {
        BPUtils.logMethod(CLASS_TAG);
        authenticateTwitter();
    }

    @Override
    public void onTWLogout(LoginFragment loginFragment, int adapterPosition) {
        BPUtils.logMethod(CLASS_TAG);

        authHandler.post(new Runnable() {
            @Override
            public void run() {
                twitterLogout();
            }
        });
    }

    @Override
    public void onIGLogin(LoginFragment loginFragment, final int adapterPosition) {
        BPUtils.logMethod(CLASS_TAG);
        authenticateInstagram();
    }

    @Override
    public void onIGLogout(LoginFragment loginFragment, final int adapterPosition) {
        BPUtils.logMethod(CLASS_TAG);
        authHandler.post(new Runnable() {
            @Override
            public void run() {
                instagramLogout();
            }
        });
    }

    // ---- SOCIAL MEDIA METHODS ----- //

    /**
     *
     * Facebook methods
     *
     */

    private void fbLogin(SimpleFacebook simpleFacebook, int adapterPosition, OnLoginListener onLoginListener) {
        simpleFacebook.login(onLoginListener);
    }

    private void fbLogout(SimpleFacebook simpleFacebook, int adapterPosition, OnLogoutListener onLogoutListener) {
        simpleFacebook.logout(onLogoutListener);
    }

    /**
     *
     * Twitter methods
     *
     */

    private void authenticateTwitter() {
        if(authHandler != null) {
            authHandler.post(new Runnable() {
                @Override
                public void run() {
                    BPUtils.logMethod(CLASS_TAG, "authenticateTwitter");

                    twConsumerKey = sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null);
                    twConsumerSecret = sharedPreferences.getString(BPUtils.TW_CONSUMER_SECRET, null);
                    twToken = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
                    twTokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

                    if(twToken == null || twTokenSecret == null) {
                        getTwitterAccessToken();
                        return;
                    }

                    TwitterFactory twitterFactory = new TwitterFactory(getTwitterConfigBuilder(twConsumerKey,
                            twConsumerSecret, twToken, twTokenSecret));
                    Twitter twitter = twitterFactory.getInstance();

                    if(isTwitterObjValid(twitter)) {
                        Log.v(CLASS_TAG, "Storing Twitter Object...");
                        BPUtils.putSPrefObject(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_OBJECT, twitter);
                        BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_LOGIN, true);
                    }
                    else {
                        getTwitterAccessToken();
                    }
                }
            });
        }
    }

    private boolean isTwitterObjValid(Twitter twitter) {
        BPUtils.logMethod(CLASS_TAG);

        try {
            User user = twitter.verifyCredentials();

            Log.v(CLASS_TAG, "Printing out user information...");
            Log.v(CLASS_TAG, "User Screen Name: " + user.getScreenName());
            Log.v(CLASS_TAG, "User Full Name: " + user.getName());
            Log.v(CLASS_TAG, "User ID: " + user.getId());
            Log.v(CLASS_TAG, "Logging out information successful. TEST PASSED.");

            return true;
        }
        catch (TwitterException e) {
            Log.e(CLASS_TAG, "Returned TwitterException. TEST FAILED");
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e) {
            Log.e(CLASS_TAG, "Returned NullPointerException. TEST FAILED");
            return false;
        }
    }

    private void getTwitterAccessToken() {
        BPUtils.logMethod(CLASS_TAG);

        if (sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null) == null) {
            BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_CONSUMER_KEY,
                    getString(R.string.tck));
            BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME,
                    BPUtils.TW_CONSUMER_SECRET, getString(R.string.tcs));
        }

        try {
            twConsumerKey = sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null);
            twConsumerSecret = sharedPreferences.getString(BPUtils.TW_CONSUMER_SECRET, null);

            Twitter twitter = new TwitterFactory(getTwitterConfigBuilder(twConsumerKey,
                    twConsumerSecret, null, null)).getInstance();

            RequestToken requestToken = twitter.getOAuthRequestToken(getString(R.string.tcu));

            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                    TwitterAuthFragment.newInstance(requestToken.getAuthorizationURL()),
                    "tw_auth_fragment").addToBackStack("tw_auth_fragment").commit();
        }
        catch (TwitterException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Log.v(CLASS_TAG, "Unable to activate Twitter");
        }
    }

    private Configuration getTwitterConfigBuilder(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        configurationBuilder
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);

        return configurationBuilder.build();
    }

    private boolean isTwitterConnected() {
        Log.v(CLASS_TAG, "isTwitterConnected() called");
        return isTwitterObjValid(twitter);
    }

    private void twitterLogout() {
        BPUtils.logMethod(CLASS_TAG);

        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.TW_CONSUMER_KEY);
        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.TW_CONSUMER_SECRET);
        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);
        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.TW_OBJECT);
        BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_LOGIN, false);
        Log.v(CLASS_TAG, "Logged out of Twitter");
    }

    private void preserveTwitterObject() {
        authHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "preserveTwitterObject");

                if(twitter != null) {
                    BPUtils.putSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                            BPUtils.TW_OBJECT, twitter);
                    return;
                }

                Log.v(CLASS_TAG, "There was nothing to preserve the state of the Twitter object");
            }
        });
    }

    /**
     *
     * Instagram methods
     *
     */

    private void authenticateInstagram() {
        if(authHandler != null) {
            authHandler.post(new Runnable() {
                @Override
                public void run() {
                    BPUtils.logMethod(CLASS_TAG, "authenticateInstagram");
                    igAuthCode = sharedPreferences.getString(BPUtils.IG_AUTH_CODE, null);

                    if (igAuthCode == null) {
                        getInstagramToken();
                        return;
                    }

                    try {
                        // Authenticate Instagram
                        instagram = BPUtils.getSPrefObject(sharedPreferences, Instagram.class, BPUtils.IG_OBJECT);

                        if(isInstagramObjValid(instagram)) {
                            Log.v(CLASS_TAG, "Instagram authentication successful. Storing object.");
                            BPUtils.putSPrefObject(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_OBJECT, instagram);
                            BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_LOGIN, true);
                        }
                        else {
                            Log.e(CLASS_TAG, "There was an issue with validity of Instagram instance.");
                            getInstagramToken();
                        }
                    }
                    catch (OAuthException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getInstagramToken() {
        BPUtils.logMethod(CLASS_TAG);
        Log.v(CLASS_TAG, "Current code is null. Getting an IG Access Token");
        String authorizationURL = instagramService.getAuthorizationUrl(EMPTY_TOKEN);
        Log.v(CLASS_TAG, authorizationURL);
        getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                IGAuthFragment.newInstance(authorizationURL), "ig_auth_fragment")
                .addToBackStack("ig_auth_fragment").commit();
    }

    private boolean isInstagramObjValid(Instagram instagram) {
        BPUtils.logMethod(CLASS_TAG);

        // Ensure this method is running anywhere but UI Thread!!

        try {
            UserInfo userInfo = instagram.getCurrentUserInfo();
            String userName = userInfo.getData().getUsername();
            String userFullName = userInfo.getData().getFullName();
            String userId = userInfo.getData().getId();

            if(userName == null || userId == null) {
                Log.e(CLASS_TAG, "One entry of user information is null. Test failed.");
                return false;
            }

            Log.v(CLASS_TAG, "Can I print user information?:");
            Log.v(CLASS_TAG, "Username: " + userName);
            Log.v(CLASS_TAG, "Full Name: " + userFullName);
            Log.v(CLASS_TAG, "User ID: " + userId);
            Log.v(CLASS_TAG, "Information printed successfully. TEST PASSED");
            return true;
        }
        catch (InstagramException e) {
            Log.e(CLASS_TAG, "Returned InstagramException. Test Failed");
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e) {
            Log.e(CLASS_TAG, "Returned Instagram NullPointerException. Test Failed");
            return false;
        }
    }

    private void preserveInstagramObject() {
        authHandler.post(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "preserveInstagramObject");

                if(instagram != null) {
                    BPUtils.putSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                            BPUtils.IG_OBJECT, instagram);
                    return;
                }

                Log.v(CLASS_TAG, "There was nothing to preserve the state of the Instagram object");
            }
        });
    }

    private void instagramLogout() {
        BPUtils.logMethod(CLASS_TAG);
        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.IG_AUTH_CODE);
        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME, BPUtils.IG_OBJECT);
        BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
    }

    private boolean isInstagramConnected() {
        // Based connectivity on the validity of the instance of Instagram
        BPUtils.logMethod(CLASS_TAG);
        return isInstagramObjValid(BPUtils.getSPrefObject(sharedPreferences, Instagram.class, BPUtils.IG_OBJECT));
    }

    /**
     * Display Login Fragment
     */

    private void displayLoginFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("login_fragment").replace(R.id.fl_activity_blocparty_login,
                LoginFragment.newInstance(), "login_fragment");
        fragmentTransaction.commit();
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
            Log.v(CLASS_TAG, "Some backpressed fragment was null...heading back to a fragment...");
            displayLoginFragment();
        }
    }
}
