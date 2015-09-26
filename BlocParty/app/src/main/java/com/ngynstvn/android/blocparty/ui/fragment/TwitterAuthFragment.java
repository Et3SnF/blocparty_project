package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.twitter.TwitterHelper;

import java.util.concurrent.ExecutionException;

import twitter4j.auth.RequestToken;

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

        try {
            RequestToken rt = new GetRequestTokenTask().execute().get();
            if (rt==null) {
                Toast.makeText(BlocpartyApplication.getSharedInstance(), "Failure to create token. " +
                        "Perhaps the server is down? Please retry later", Toast.LENGTH_LONG).show();
                return view;
            }
            String token = rt.getToken();

            webView.loadUrl("https://api.twitter.com/oauth/authorize?force_login=true&oauth_token="+token);
        } catch (InterruptedException e) {
            e.printStackTrace();  // TODO: Customise this generated block
        } catch (ExecutionException e) {
            e.printStackTrace();  // TODO: Customise this generated block
        }

        return view;
    }

    private class GetRequestTokenTask extends AsyncTask<Void,Void,RequestToken> {
        @Override
        protected RequestToken doInBackground(Void... voids) {
            RequestToken rt;
            try {
                TwitterHelper th = new TwitterHelper(getActivity(),null);
                rt = th.getRequestToken(true);
                return rt;
            } catch (Exception e) {
                e.printStackTrace();  // TODO: Customise this generated block
            }
            return null;
        }
    }

    // WebClient class that will use JavaScript to do its magic!

    /**
     * Class that injects the JavaScript callback when the 2nd auth
     * page was reached.
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.equals("https://api.twitter.com/oauth/authorize")) {
                webView.loadUrl("javascript:window.HTMLOUT.obtain(document.body.innerHTML);");
                // not sure why the following fails, but doesn't really matter
                // myWebView.loadUrl("javascript:window.HTMLOUT.setHTML(document.getElementById('code-desc'));");
            }
        }
    }

    /**
     * This class holds the callback that we inject in MyWebViewClient#onPageFinished
     */
    public class MyJavaScriptClient {

        /**
         * Called from the java script in the web view. We parse the
         * html obtained and then generate an account with the help of the
         * passed pin
         * @param html Html as evaluated by the javascript
         */
        @SuppressWarnings("unused")
        public void obtain(String html) {
            int i = html.indexOf("<code>");
            if (i!=-1) {
                html = html.substring(i + 6);
                i = html.indexOf("</code>");
                html = html.substring(0,i);

//                try {
//                    new GenerateAccountWithOauthTask().execute(html).get();
//                    Intent intent = new Intent().setClass(TwitterLoginActivity.this, TabWidget.class);
//                    startActivity(intent);
//                    finish();
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(),
//                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
            }
        }
    }

}
