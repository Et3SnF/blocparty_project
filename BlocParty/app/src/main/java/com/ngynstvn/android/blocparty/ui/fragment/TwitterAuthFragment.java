package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import java.net.UnknownHostException;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/26/15.
 */

public class TwitterAuthFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(TwitterAuthFragment.class);

    private static final String TOKEN_URL = "request_token_url";
    private static int counter = 0;

    private WebView webView;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        twitterAuthFragDelegate = new WeakReference<TwitterAuthFragDelegate>((TwitterAuthFragDelegate) getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if(menu != null) {
            menu.findItem(R.id.action_login_button).setVisible(false).setEnabled(false);
        }
    }

    // WebClient class that will use JavaScript to do its magic!

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v(TAG, "shouldOverrideUrlLoading() called");
            Log.v(TAG, "Current URL in shouldOverrideUrlLoading(): " + url);

            String dummyURL = "https://mobile.twitter.com/?oauth_token=";

            try {
                if(url.contains(dummyURL)) {

                    if(counter > 0) {
                        counter--;
                        Log.v(TAG, "Current Counter: " + counter);
                    }


                    String consumerKey = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                            .getString(BPUtils.TW_CONSUMER_KEY, null);
                    String consumerKeySecret = BPUtils.newSPrefInstance(BPUtils.FILE_NAME)
                            .getString(BPUtils.TW_CONSUMER_SECRET, null);
                    String token = getString(R.string.tat);
                    String tokenSecret = getString(R.string.tats);

                    BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                            BPUtils.TW_ACCESS_TOKEN, getString(R.string.tat));

                    BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                            BPUtils.TW_ACCESS_TOKEN_SECRET, getString(R.string.tats));

                    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

                    configurationBuilder
                            .setOAuthConsumerKey(consumerKey)
                            .setOAuthConsumerSecret(consumerKeySecret)
                            .setOAuthAccessToken(token)
                            .setOAuthAccessTokenSecret(tokenSecret);

                    TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
                    Twitter twitter = twitterFactory.getInstance();

                    if(twitter != null) {
                        BPUtils.putSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                BPUtils.TW_OBJECT, twitter);
                        getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                                LoginFragment.newInstance()).commit();
                        BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, true);
                    }

                    return true;
                }
                else if(url.contains("https://api.twitter.com/login/error?")) {
                    view.loadUrl("https://en.wikipedia.org/wiki/Uh_oh");
                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "Wrong Twitter username/password " +
                            "combination", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            catch(NullPointerException e) {
                Log.v(TAG, "NullPointerException in shouldOverrideUrlLoading. There was an issue " +
                        "in line: " + Thread.currentThread().getStackTrace()[2].getLineNumber());

                e.printStackTrace();

                BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                        BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, false);

                Toast.makeText(BlocpartyApplication.getSharedInstance(), "There was an issue during " +
                        "the Twitter authentication process", Toast.LENGTH_LONG).show();

                return true;
            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v(TAG, "Current URL in onPageFinished(): " + url);

            counter++;
            Log.v(TAG, "Current counter: " + counter);

            if(url.equals("https://api.twitter.com/oauth/authorize") && counter > 1) {
                getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                        LoginFragment.newInstance()).commit();

                SharedPreferences sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

                if(sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null) != null) {
                    BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
                    BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);
                }

                BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                        BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, false);
            }

            if(url.equals("https://en.m.wikipedia.org/wiki/Uh_oh")) {
                counter = 0;
                getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                        LoginFragment.newInstance()).commit();

                SharedPreferences sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

                if(sharedPreferences.getString(BPUtils.TW_ACCESS_TOKEN, null) != null) {
                    BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN);
                    BPUtils.delSPrefValue(sharedPreferences, BPUtils.FILE_NAME, BPUtils.TW_ACCESS_TOKEN_SECRET);
                }

                BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                        BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, false);
            }
        }
    }
}
