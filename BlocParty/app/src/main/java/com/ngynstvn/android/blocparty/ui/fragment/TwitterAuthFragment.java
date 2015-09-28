package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

        webView.loadUrl(getArguments().getString(TOKEN_URL));

        return view;
    }

    // WebClient class that will use JavaScript to do its magic!

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v(TAG, "Current URL: " + url);
            if (url.equals("https://api.twitter.com/oauth/authenticate")) {
                // Parse the HTML code of this website
                Log.v(TAG, url);
                parseHTML(url);
            }
        }
    }

    private class MyJavaScriptClient {

        public void obtain(String html) {
            int i = html.indexOf("<code>");
            if (i != -1) {
                html = html.substring(i + 6);
                i = html.indexOf("</code>");
                html = html.substring(0, i);
            }
        }
    }

    private void parseHTML(final String url) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL yahoo = new URL(url);

                    URLConnection yc = yahoo.openConnection();

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            yc.getInputStream(), "UTF-8"));

                    String inputLine;

                    StringBuilder a = new StringBuilder();

                    while ((inputLine = in.readLine()) != null)
                        a.append(inputLine);

                    in.close();
                    Log.v(TAG, a.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
