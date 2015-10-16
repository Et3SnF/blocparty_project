package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;

import org.jinstagram.auth.model.Token;

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
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.auth_webview, container, false);

        webView = (WebView) view.findViewById(R.id.wv_twitter_auth);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getArguments().getString(AUTH_URL));

        return view;
    }

    // ----- Menu Related Methods ----- //

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
            Log.v(TAG, "shouldOverrideUrlLoading() method called");

            try {
                String authCode = url.substring(getString(R.string.igcu).length() + 6);

                if (url.contains(getString(R.string.igcu) + "?code=")) {

                    Log.v(TAG, "Current URL: " + url);

                    BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                            BPUtils.IG_AUTH_CODE, authCode);

                    Log.v(TAG, "Code: " + authCode);

                    BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                            BPUtils.IG_POSITION, 2, BPUtils.IG_LOGIN, true);

                    Log.v(TAG, "Fragment attach status: " + isAdded());

                    BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                            BPUtils.FILE_NAME, BPUtils.IG_TOKEN, authCode);

                    getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                            LoginFragment.newInstance(authCode)).commit();

                    return true;
                }
            }
            catch(IllegalStateException e) {
                Log.v(TAG, "IGAuthFragment is not attached to LoginActivity. Exception suppressed.");
            }

            return false;
        }
    }
}
