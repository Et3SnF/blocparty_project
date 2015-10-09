package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.LoginAdapter;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import org.jinstagram.Instagram;
import org.jinstagram.auth.exceptions.OAuthException;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.exceptions.InstagramException;

import java.lang.ref.WeakReference;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

    // Shared Preferences Variable

    private static SharedPreferences sharedPreferences;

    // Facebook Static Variables

    private static SimpleFacebook simpleFacebook;

    // Twitter Static Variables

    private static Twitter twitter;
    private static ConfigurationBuilder configurationBuilder;
    private static Configuration configuration;
    private static String twConsumerKey;
    private static String twConsumerSecret;
    private static String twToken;
    private static String twTokenSecret;

    // Instagram Static Variables

    private static final Token EMPTY_TOKEN = null;
    private static InstagramService instagramService;
    private static Instagram instagram;
    private static String igAuthCode;

    // Fields

    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;

    /**
     *
     * Instantiation Method
     *
     */

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    public static LoginFragment newInstance(String igValue) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BPUtils.IG_AUTH_CODE, igValue);
        return loginFragment;
    }

    /*
     * Interface Material
     */

    public interface LoginFragmentDelegate {
        void onFBLogin(LoginFragment loginFragment, int adapterPosition, boolean setCheck);
    }

    private WeakReference<LoginFragmentDelegate> loginFragmentDelegate;

    public void setLoginFragmentDelegate(LoginFragmentDelegate loginFragmentDelegate) {
        this.loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>(loginFragmentDelegate);
    }

    public LoginFragmentDelegate getLoginFragmentDelegate() {

        // Delegate #1: MainActivity (Placed to prevent circular communication between LoginFragment
        // and LoginAdapter)

        if(loginFragmentDelegate == null) {
            return null;
        }

        return loginFragmentDelegate.get();
    }

    // Twitter callback interface

    private interface Authoritative {
        void onSuccess();
        void onFailure();
    }

    // ----- Lifecycle Methods ----- //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        simpleFacebook = SimpleFacebook.getInstance();
        loginAdapter = new LoginAdapter();

        if(savedInstanceState != null) {
            String token = getArguments().getString(BPUtils.IG_TOKEN);
            instagram = new Instagram(token);
            Log.v(TAG, instagram.getClientId());
        }

        configurationBuilder = new ConfigurationBuilder();
        instagramService = BlocpartyApplication.getSharedInstagramService();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginAdapter = new LoginAdapter();
        loginAdapter.setLoginAdapterDelegate(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_login_items);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated() called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance();

        // Must place sharedPreference here for it to save states properly
        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

        // Place the recyclerview stuff here in order for LoginAdapterViewHolder to instantiate

        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(loginAdapter);

        // Resume any Twitter activity when back to this fragment

        if(sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null) != null &&
            sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null) != null) {

            twConsumerKey = sharedPreferences.getString(BPUtils.TW_CONSUMER_KEY, null);
            twConsumerSecret = sharedPreferences.getString(BPUtils.TW_CONSUMER_SECRET, null);
            twToken = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
            twTokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(twConsumerKey, twConsumerSecret);
            twitter.setOAuthAccessToken(new AccessToken(twToken, twTokenSecret));
        }

        if(isTwitterConnected()) {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

            configurationBuilder.setOAuthConsumerKey(twConsumerKey)
                    .setOAuthConsumerSecret(twConsumerSecret);

            twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
        }

        // Resume any Instagram activity

        if(isIGLoggedIn()) {
            instagram = new Instagram(getString(R.string.igc));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
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
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView() called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();

        BPUtils.delSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                BPUtils.IG_AUTH_CODE);
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDetach() called");
        super.onDetach();
    }

    // ----- ----- ---- ----- //

    /**
     *
     * LoginAdapter.LoginAdapterDelegate implemented methods
     *
     */

    @Override
    public void onLoginSwitchActivated(LoginAdapter loginAdapter, final int adapterPosition, final boolean isChecked) {

        switch(adapterPosition) {
            case 0:
                if(isChecked) {

                    fbLogin(simpleFacebook, adapterPosition, new OnLoginListener() {
                        @Override
                        public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                            Log.i(TAG, "Logged into Facebook");

                            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                    BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, true);

                            Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged into Facebook",
                                    Toast.LENGTH_SHORT).show();

                            BlocpartyApplication.getSharedDataSource().getFacebookInformation(simpleFacebook);
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
                    break;
                }
                else {
                    fbLogout(simpleFacebook, adapterPosition, new OnLogoutListener() {
                        @Override
                        public void onLogout() {
                            if(simpleFacebook.isLogin()) {
                                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                        BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
                                Log.i(TAG, "Logged out of Facebook");

                                Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged out of Facebook",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                }
            case 1:
                if(isChecked) {
                    twitterLogin(new Authoritative() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "Logged into Twitter");
                            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                    BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, true);
                            BlocpartyApplication.getSharedDataSource().getTwitterInformation(twitter);
                        }

                        @Override
                        public void onFailure() {
                            Log.v(TAG, "Unable to log into Twitter");
                            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                    BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, false);
                        }
                    });
                    break;
                }
                else {
                    if(isTwitterConnected()) {
                        BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
                        BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);

                        BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, false);

                        Log.v(TAG, "Logged out of Twitter");

                        Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged out of Twitter",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

            case 2:
                if(isChecked) {
                    igLogin(new Authoritative() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "Logged into Instagram");

                            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                    BPUtils.IG_POSITION, adapterPosition, BPUtils.IG_LOGIN, true);

                            BlocpartyApplication.getSharedDataSource().getInstagramInformation(instagram);
                        }

                        @Override
                        public void onFailure() {
                            Log.v(TAG, "Unable to log into Instagram");
                            BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                        }
                    });
                    break;
                }
                else {
                    if(isIGLoggedIn()) {
                        igLogout();
                        Log.v(TAG, "Logged out of Instagram");
                        BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                BPUtils.IG_POSITION, adapterPosition, BPUtils.IG_LOGIN, false);
                    }
                    break;
                }
        }
    }

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
        TwitterFactory twitterFactory = new TwitterFactory();
        AccessToken accessToken = loadAccessToken();

        if(accessToken != null) {

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

            configuration = configurationBuilder
                    .setOAuthConsumerKey(twConsumerKey)
                    .setOAuthConsumerSecret(twConsumerSecret)
                    .build();

            twitter = new TwitterFactory(configuration).getInstance();
            twitter.setOAuthAccessToken(accessToken);
            twitter = twitterFactory.getInstance();
            authoritative.onSuccess();
            Log.v(TAG, "Twitter Login Complete");

            Toast.makeText(BlocpartyApplication.getSharedInstance(), "Logged into Twitter",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            getAccessToken();
        }
    }

    private AccessToken loadAccessToken() {
        Log.v(TAG, "loadAccessToken() called");
        twToken = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
        twTokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

        if(twToken != null && twTokenSecret != null) {
            return new AccessToken(twToken, twTokenSecret);
        }

        return null;
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

                    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

                    Configuration configuration = configurationBuilder
                            .setOAuthConsumerKey(twConsumerKey)
                            .setOAuthConsumerSecret(twConsumerSecret)
                            .build();

                    Twitter twitter = new TwitterFactory(configuration).getInstance();

                    RequestToken requestToken = twitter.getOAuthRequestToken(getString(R.string.tcu));

                    BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME,
                            BPUtils.TW_ACCESS_TOKEN, requestToken.getToken());

                    BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME,
                            BPUtils.TW_ACCESS_TOKEN_SECRET, requestToken.getTokenSecret());

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
                            TwitterAuthFragment.newInstance(requestToken.getAuthorizationURL())).commit();

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
                    Log.e(TAG, "There was an issue extracting the access token.");
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
                getFragmentManager().beginTransaction()
                        .replace(R.id.fl_activity_blocparty, IGAuthFragment.newInstance(authorizationURL)).commit();
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
}
