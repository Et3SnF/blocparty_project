package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
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

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.exceptions.InstagramException;

/**
 * Created by Ngynstvn on 9/27/15.
 */

public class IGAuthFragment extends Fragment {

    private static final String CLASS_TAG = BPUtils.classTag(IGAuthFragment.class);

    private static final String AUTH_URL = "ig_auth_url";

    // Authentication Handler;
    private Handler instagramAuthHandler;

    // Instagram Static Variables

    private static final Token EMPTY_TOKEN = null;

    private Token accessToken;
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
        Log.v(CLASS_TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        InstagramAuthTestTask instagramAuthTestTask = new InstagramAuthTestTask();
        instagramAuthTestTask.start();
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(CLASS_TAG, "onCreateView() called");
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
            Log.v(CLASS_TAG, "shouldOverrideUrlLoading() method called");

            if (url.contains(getString(R.string.igcu) + "?code=")) {

                final String authCode = url.substring(getString(R.string.igcu).length() + 6);
                Log.v(CLASS_TAG, "Current Code: " + authCode);
                Log.v(CLASS_TAG, "Current URL: " + url);

                instagramAuthHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (isAuthCodeValid(authCode)) {
                                Log.v(CLASS_TAG, "Code: " + authCode);

                                BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                                        BPUtils.FILE_NAME, BPUtils.IG_AUTH_CODE, authCode);

                                BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                                        BPUtils.FILE_NAME, BPUtils.IG_TOKEN, authCode);

                                BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                                        BPUtils.FILE_NAME, BPUtils.IG_POSITION, 2, BPUtils.IG_LOGIN, true);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.v(CLASS_TAG, "Fragment attach status: " + isAdded());

                                        getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                                                LoginFragment.newInstance(authCode)).commit();
                                    }
                                });
                            } else {
                                Log.e(CLASS_TAG, "Authorization code is not valid. Returning...");
                                BPUtils.putSPrefLoginValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                                        BPUtils.FILE_NAME, BPUtils.IG_POSITION, 2, BPUtils.IG_LOGIN, false);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.v(CLASS_TAG, "Fragment attach status: " + isAdded());

                                        getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty_login,
                                                LoginFragment.newInstance(authCode)).commit();
                                    }
                                });
                            }
                        } catch (IllegalStateException e) {
                            Log.v(CLASS_TAG, "IGAuthFragment is not attached to LoginActivity. Exception suppressed.");
                            e.printStackTrace();
                        }
                    }
                });

                return true;
            }

            return false;
        }
    }

    /**
     *
     * Thread class
     *
     */

    private class InstagramAuthTestTask extends Thread {
        @Override
        public void run() {
            BPUtils.logMethod(CLASS_TAG, "InstagramAuthTestTask");
            Looper.prepare();
            instagramAuthHandler = new Handler();
            Looper.loop();
        }
    }

    /**
     *
     * ENSURE THIS IS RUNNING ANYWHERE BUT UI THREAD
     *
     */

    private boolean isAuthCodeValid(String authCode) {
        BPUtils.logMethod(CLASS_TAG);

        Verifier verifier = new Verifier(authCode);
        accessToken = BlocpartyApplication.getSharedInstagramService().getAccessToken(null, verifier);
        Instagram instagram = new Instagram(accessToken);

        try {
            UserInfo userInfo = instagram.getCurrentUserInfo();
            String userName = userInfo.getData().getUsername();
            String userFullName = userInfo.getData().getFullName();
            String userId = userInfo.getData().getId();

            if(userName == null || userId == null) {
                Log.e(CLASS_TAG, "One entry of user information is null. Test failed.");
                return false;
            }

            Log.v(CLASS_TAG, "Username: " + userName);
            Log.v(CLASS_TAG, "Full Name: " + userFullName);
            Log.v(CLASS_TAG, "User ID: " + userId);
            Log.v(CLASS_TAG, "Information printed successfully. TEST PASSED");

            Log.v(CLASS_TAG, "Storing Instagram Object");

            BPUtils.putSPrefObject(BPUtils.newSPrefInstance(BPUtils.FILE_NAME), BPUtils.FILE_NAME,
                    BPUtils.IG_OBJECT, instagram);

            return true;
        }
        catch (InstagramException e) {
            Log.e(CLASS_TAG, "Returned InstagramException. Test Failed");
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e) {
            Log.e(CLASS_TAG, "Returned Instagram NullPointerException. Test Failed");
            return false;
        }
    }
}
