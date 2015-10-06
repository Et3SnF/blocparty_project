package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.activity.MainActivity;

import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;

/**
 * Created by Ngynstvn on 9/27/15.
 */
public class IGAuthFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(IGAuthFragment.class);

    private static final String AUTH_URL = "ig_auth_url";

    private Token accessToken;

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
        View view = inflater.inflate(R.layout.auth_webview, container, false);

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
            Log.v(TAG, "onPageFinished() called");

            super.onPageFinished(view, url);

            final String authCode = url.substring(getString(R.string.igcu).length() + 6);

            if (url.contains(getString(R.string.igcu) + "?code=")) {

                Log.v(TAG, "Current URL: " + url);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                BPUtils.IG_AUTH_CODE, authCode);

                        Log.v(TAG, "Code: " + authCode);

                        BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                                BPUtils.IG_POSITION, 2, BPUtils.IG_LOGIN, true);

                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Log.v(TAG, "Fragment attach status: " + isAdded());

                        if(isAdded()) {
                            getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
                                    LoginFragment.newInstance(authCode)).commit();
                        }
                    }
                }.execute();
            }

        }
    }
}
