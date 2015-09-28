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

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.LoginAdapter;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;

import java.lang.ref.WeakReference;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

    private static final String COUNTER = "counter";
    private static int instance_counter = 0;

    // Shared Preferences Variable

    private static SharedPreferences sharedPreferences;

    // Facebook Static Variables

    private static SimpleFacebook simpleFacebook;

    // Twitter Static Variables

    private static Twitter twitter;
    private static TwitterStream twitterStream;
    private static RequestToken requestToken;
    private static ConfigurationBuilder configurationBuilder;
    private static Configuration configuration;
    private static String token;
    private static String tokenSecret;

    // Instagram Static Variables

    private static final Token EMPTY_TOKEN = null;
    private static InstagramService instagramService;
    private static Instagram instagram;

    // Fields

    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;
    private boolean isIGTokenValid;

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
        bundle.putString(BPUtils.IG_TOKEN, igValue);
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
            instance_counter = savedInstanceState.getInt("counter");
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

        // Put something twitter related here to prevent crashes during build configurations.

        if(isTwitterConnected()) {
            token = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
            tokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

            configuration = configurationBuilder
                    .setDebugEnabled(true)
                    .setOAuthConsumerKey(getString(R.string.tck))
                    .setOAuthConsumerSecret(getString(R.string.tcs))
                    .setOAuthAccessToken(token)
                    .setOAuthAccessTokenSecret(tokenSecret)
                    .build();

            twitterStream = new TwitterStreamFactory(configuration).getInstance();
        }

        isIGTokenValid = sharedPreferences.getBoolean(BPUtils.IG_TOKEN_VALID, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
        outState.putInt("counter", instance_counter);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
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
                            Log.i(TAG, "Logged out of Facebook");

                            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                    BPUtils.FB_POSITION, adapterPosition, BPUtils.FB_LOGIN, false);
                        }
                    });
                    break;
                }
            case 1:
                if(isChecked) {

                    if (isTwitterConnected()) {
                        twLogin();
                    }
                    else {
                        authTwitter(adapterPosition, new Authoritative() {
                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "Logged in to Twitter");

                                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                        BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, true);
                            }

                            @Override
                            public void onFailure() {
                                Log.i(TAG, "Unable to log into Twitter");

                                BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                        BPUtils.TW_POSITION, adapterPosition, BPUtils.TW_LOGIN, false);
                            }
                        });
                    }

                    break;
                }
                else {
                    if(isIGLoggedIn(sharedPreferences)) {
                        igLogout(sharedPreferences);
                        Log.v(TAG, "Logged out of Twitter");
                    }
                    break;
                }
            case 2:
                if(isChecked) {
                    igLogin(sharedPreferences, new Authoritative() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "Logged into Instagram");
                            BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                                    BPUtils.IG_POSITION, adapterPosition, BPUtils.IG_LOGIN, true);
                        }

                        @Override
                        public void onFailure() {
                            Log.v(TAG, "Unable to log into Instagram");
                            BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_TOKEN);
                            BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                        }
                    });
                    break;
                }
                else {
                    igLogout(sharedPreferences);
                    Log.v(TAG, "Logged out of Instagram");
                    BPUtils.putSPrefLoginValue(sharedPreferences, BPUtils.FILE_NAME,
                            BPUtils.IG_POSITION, adapterPosition, BPUtils.IG_LOGIN, false);
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

    private void authTwitter(final int adapterPosition, final Authoritative authoritative) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {

                if(configurationBuilder == null) {
                    configurationBuilder = new ConfigurationBuilder();
                }

                configuration = configurationBuilder
                        .setDebugEnabled(true)
                        .setOAuthConsumerKey(getString(R.string.tck))
                        .setOAuthConsumerSecret(getString(R.string.tcs))
                        .build();

                twitter = new TwitterFactory(configuration).getInstance();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Log.v(TAG, "Getting token....");
                    requestToken = twitter.getOAuthRequestToken();

                    String token = requestToken.getToken();
                    String tokenSecret = requestToken.getTokenSecret();

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
                            TwitterAuthFragment.newInstance(requestToken.getAuthenticationURL())).commit();

                    return true;
                }
                catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
                catch (IllegalStateException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(aBoolean) {
                    BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN,
                            requestToken.getToken());
                    BPUtils.putSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME,
                            BPUtils.TW_ACCESS_TOKEN_SECRET, requestToken.getTokenSecret());
                    authoritative.onSuccess();
                    configuration = null;
                    configurationBuilder = null;
                }
                else {
                    authoritative.onFailure();
                }
            }
        }.execute();

    }

    private boolean isTwitterConnected() {
        return sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null) != null;
    }

    private void twLogout() {
        BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
        BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);
    }

    private void twLogin() {
        String token = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null);
        String tokenSecret = sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN_SECRET, null);

        if(token == null || tokenSecret == null) {
            Log.v(TAG, "Unable to log into Twitter (authentication issues)");
            return;
        }

        twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
        Log.v(TAG, "You are logged into Twitter");
    }

    // ----- Instagram Methods ----- //

    private void authInstagram(final InstagramService instagramService, final Authoritative authoritative) {

        final String authorizationURL = instagramService.getAuthorizationUrl(EMPTY_TOKEN);

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fl_activity_blocparty, IGAuthFragment.newInstance(authorizationURL)).commit();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(isIGTokenValid) {
                    authoritative.onSuccess();
                }
                else {
                    authoritative.onFailure();
                }
            }
        }.execute(authorizationURL);
    }

    private void igLogin(final SharedPreferences sharedPreferences, Authoritative authoritative) {
        String token = sharedPreferences.getString(BPUtils.IG_TOKEN, null);

        if(token!=null) {
            Token accessToken = instagramService.getAccessToken(EMPTY_TOKEN, new Verifier(token));
            instagram = new Instagram(accessToken);
            authoritative.onSuccess();
        }
        else {
            authInstagram(instagramService, new Authoritative() {
                @Override
                public void onSuccess() {
                    igLogin(sharedPreferences, new Authoritative() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "Logged into Instagram");
                            Token accessToken = instagramService.getAccessToken(EMPTY_TOKEN, new Verifier(sharedPreferences.getString(BPUtils.IG_TOKEN, null)));
                            instagram = new Instagram(accessToken);
                            BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, true);
                        }

                        @Override
                        public void onFailure() {
                            // Delete any stored token value
                            Log.v(TAG, "Unable to log into Instagram again...1");
                            BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_TOKEN);
                            BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                        }
                    });
                }

                @Override
                public void onFailure() {
                    // Delete any stored token value
                    Log.v(TAG, "Unable to log into Instagram again...2");
                    BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_TOKEN);
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_LOGIN, false);
                }
            });
        }
    }

    private void igLogout(SharedPreferences sharedPreferences) {
        BPUtils.delSPrefStrValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.IG_TOKEN);
    }

    private boolean isIGLoggedIn(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(BPUtils.IG_TOKEN, null) != null;
    }
}
