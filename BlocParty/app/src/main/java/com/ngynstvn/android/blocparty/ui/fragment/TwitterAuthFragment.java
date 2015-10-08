package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/26/15.
 */

public class TwitterAuthFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(TwitterAuthFragment.class);

    private static final String TOKEN_URL = "request_token_url";
    private static final String TOKEN = "request_token";

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

    // WebClient class that will use JavaScript to do its magic!

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v(TAG, "Current URL: " + url);

            if (url.equals("https://api.twitter.com/oauth/authorize")) {
                getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
                        LoginFragment.newInstance()).commit();
                BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                        BPUtils.TW_POSITION, 1, BPUtils.TW_LOGIN, true);
            }
        }
    }
}
