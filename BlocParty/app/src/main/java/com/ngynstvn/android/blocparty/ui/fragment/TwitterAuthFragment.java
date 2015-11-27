package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;

import java.lang.ref.WeakReference;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/26/15.
 */

public class TwitterAuthFragment extends Fragment {

    private static final String CLASS_TAG = BPUtils.classTag(TwitterAuthFragment.class);

    private static final String TOKEN_URL = "request_token_url";
    private static int counter = 0;

    private WebView webView;

    private Handler twitterAuthHandler;

    public static TwitterAuthFragment newInstance(String value) {
        TwitterAuthFragment twitterAuthFragment = new TwitterAuthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN_URL, value);
        twitterAuthFragment.setArguments(bundle);
        return twitterAuthFragment;
    }

    // ---- Interface Material ----- //

    public interface TwitterAuthFragDelegate {

    }

    private WeakReference<TwitterAuthFragDelegate> twitterAuthFragDelegate;

    public WeakReference<TwitterAuthFragDelegate> setTwitterAuthFragDelegate(TwitterAuthFragDelegate twitterAuthFragDelegate) {
        return this.twitterAuthFragDelegate = new WeakReference<TwitterAuthFragDelegate>(twitterAuthFragDelegate);
    }

    public TwitterAuthFragDelegate getTwitterAuthFragDelegate() {
        if(twitterAuthFragDelegate == null) {
            return null;
        }

        return twitterAuthFragDelegate.get();
    }

    // ----- Lifecycle Methods ----- //


    @Override
    public void onAttach(Activity activity) {
        BPUtils.logMethod(CLASS_TAG, "API <= 23");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        BPUtils.logMethod(CLASS_TAG, "API > 23");
        super.onAttach(context);
        twitterAuthFragDelegate = new WeakReference<TwitterAuthFragDelegate>((TwitterAuthFragDelegate) getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        TwitterAuthTestTask twitterAuthTestTask = new TwitterAuthTestTask();
        twitterAuthTestTask.start();
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        View view = inflater.inflate(R.layout.auth_webview, container, false);

        webView = (WebView) view.findViewById(R.id.wv_twitter_auth);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getArguments().getString(TOKEN_URL));

        return view;
    }

    // ---- Menu Related Methods ---- //

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        BPUtils.logMethod(CLASS_TAG);
        if(menu != null) {
            menu.findItem(R.id.action_login_button).setVisible(false).setEnabled(false);
        }
    }

    // WebClient class that will use JavaScript to do its magic!

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v(CLASS_TAG, "shouldOverrideUrlLoading() called");
            Log.v(CLASS_TAG, "Current URL in shouldOverrideUrlLoading(): " + url);

            String dummyURL = "https://mobile.twitter.com/?oauth_token=";

            if(url.contains(dummyURL)) {
                twitterAuthHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.logMethod(CLASS_TAG, "shouldOverrideUrlLoading (oauth_token)");

                        if(counter > 0) {
                            counter--;
                            Log.v(CLASS_TAG, "Current Counter: " + counter);
                        }

                        String consumerKey = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                                .getString(BPUtils.TW_CONSUMER_KEY, null);
                        String consumerKeySecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                                .getString(BPUtils.TW_CONSUMER_SECRET, null);
                        String token = getString(R.string.tat);
                        String tokenSecret = getString(R.string.tats);

                        TwitterFactory twitterFactory = new TwitterFactory(getTwitterConfigBuilder(consumerKey,
                                consumerKeySecret, token, tokenSecret));
                        Twitter twitter = twitterFactory.getInstance();

                        if(isTwitterObjValid(twitter)) {
                            BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                    BPUtils.TW_ACCESS_TOKEN, token);

                            BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                    BPUtils.TW_ACCESS_TOKEN_SECRET, tokenSecret);

                            BPUtils.putSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                    BPUtils.TW_OBJECT, twitter);

                            BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                    BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, true);

                            returnToLoginFragment();
                        }
                        else {
                            BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                    BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, false);

                            Toast.makeText(BlocpartyApplication.getSharedInstance(), "There was an issue " +
                                    "with the Twitter service. Try again later.", Toast.LENGTH_LONG).show();

                            returnToLoginFragment();
                        }
                    }
                });

                return true;
            }
            else if(url.contains("https://api.twitter.com/login/error?")) {
                view.loadUrl("https://en.wikipedia.org/wiki/Uh_oh");
                Toast.makeText(BlocpartyApplication.getSharedInstance(), "Wrong Twitter username/password " +
                        "combination", Toast.LENGTH_LONG).show();
                return true;
            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v(CLASS_TAG, "Current URL in onPageFinished(): " + url);

            counter++;
            Log.v(CLASS_TAG, "Current counter: " + counter);

            if(url.equals("https://api.twitter.com/oauth/authorize") && counter > 1) {

                twitterAuthHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.logMethod(CLASS_TAG, "onPageFinished");

                        removeTwitterValues();

                        BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, false);

                        returnToLoginFragment();
                    }
                });
            }

            if(url.equals("https://en.m.wikipedia.org/wiki/Uh_oh")) {
                counter = 0;

                twitterAuthHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BPUtils.logMethod(CLASS_TAG, "onPageFinished");

                        removeTwitterValues();

                        BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, false);

                        returnToLoginFragment();
                    }
                });
            }
        }
    }

    /**
     *
     * Thread class
     *
     */

    private class TwitterAuthTestTask extends Thread {
        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "InstagramAuthTestTask");
            Looper.prepare();
            twitterAuthHandler = new Handler();
            Looper.loop();
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

    private Configuration getTwitterConfigBuilder(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        configurationBuilder
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);

        return configurationBuilder.build();
    }

    private void returnToLoginFragment() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BPUtils.logMethod(CLASS_TAG, "returnToLoginFragment");

                getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                        LoginFragment.newInstance()).commit();
            }
        });
    }

    private void removeTwitterValues() {
        BPUtils.logMethod(CLASS_TAG);

        SharedPreferences sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

        if (sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null) != null) {
            BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_CONSUMER_KEY);
            BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_CONSUMER_SECRET);
            BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
            BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);
        }
    }
}
