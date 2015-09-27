package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;

/**
 * Created by Ngynstvn on 9/26/15.
 */

public class TwitterAuthFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(TwitterAuthFragment.class);

    private static final String TOKEN_URL = "request_token_url";
    private static final String TOKEN = "request_token";

    private WebView webView;
    private Button button;

    public static TwitterAuthFragment newInstance(String value) {
        TwitterAuthFragment twitterAuthFragment = new TwitterAuthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, value);
        twitterAuthFragment.setArguments(bundle);
        return twitterAuthFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.twitter_auth_webview, container, false);

        webView = (WebView) view.findViewById(R.id.wv_twitter_auth);

        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new MyJavaScriptClient(), "HTMLOUT");

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("https://api.twitter.com/oauth/authorize?force_login=true&oauth_token=" + getArguments().get(TOKEN));

        return view;
    }

    // WebClient class that will use JavaScript to do its magic!

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.equals("https://api.twitter.com/oauth/authorize")) {
                webView.loadUrl("javascript:window.HTMLOUT.obtain(document.body.innerHTML);");
            }
        }
    }

    public class MyJavaScriptClient {

        public void obtain(String html) {
            int i = html.indexOf("<code>");
            if (i != -1) {
                html = html.substring(i + 6);
                i = html.indexOf("</code>");
                html = html.substring(0, i);
            }
        }
    }
}
