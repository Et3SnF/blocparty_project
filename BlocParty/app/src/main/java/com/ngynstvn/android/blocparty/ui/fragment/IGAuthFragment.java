package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;

import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;

/**
 * Created by Ngynstvn on 9/27/15.
 */
public class IGAuthFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(IGAuthFragment.class);

    private static final String AUTH_URL = "ig_auth_url";

    // Instagram Static Variables

    private static final Token EMPTY_TOKEN = null;

    private WebView webView;

    public static IGAuthFragment newInstance(String value) {
        IGAuthFragment igAuthFragment = new IGAuthFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AUTH_URL, value);
        igAuthFragment.setArguments(bundle);
        return igAuthFragment;
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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getArguments().getString(AUTH_URL));

        return view;
    }

    // WebClient class that will use JavaScript to do its magic!

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains(getString(R.string.igcu) + "?code=")) {

                final String token = url.substring(getString(R.string.igcu).length() + 6);
                final Verifier verifier = new Verifier(token);

                new AsyncTask<Boolean, Void, Token>() {
                    @Override
                    protected Token doInBackground(Boolean... params) {
                        Token token = BlocpartyApplication.getSharedInstagramService().getAccessToken(EMPTY_TOKEN, verifier);
                        return token;
                    }

                    @Override
                    protected void onPostExecute(Token token) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fl_activity_blocparty, LoginFragment.newInstance(token.getToken()));
                        fragmentTransaction.commit();
                    }
                }.execute();
            }
        }
    }
}
