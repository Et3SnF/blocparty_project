package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.LoginAdapter;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

    private static final String FILE_NAME = "log_states";
    private static final String FB_LOGIN = "isFBLoggedIn";
    private static final String FB_POSITION = "adapterPosition";
    private static final String COUNTER = "counter";

    private static int instance_counter = 0;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    /**
     *
     * Instantiation Method
     *
     */

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        Bundle bundle = new Bundle();
        // nothing to put in here for now
        return loginFragment;
    }

    /*
     * Interface Material
     */

    public interface LoginFragmentDelegate {
        void onFBLogin(LoginFragment loginFragment, int adapterPosition, boolean setCheck);
    }

    private WeakReference<LoginFragmentDelegate> loginFragmentDelegate;

    public void setLoginFragmentDelegate(LoginFragmentDelegate loginFragmentDelegate) {
        this.loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>(loginFragmentDelegate);
    }

    public LoginFragmentDelegate getLoginFragmentDelegate() {

        // Delegate #1: MainActivity (Placed to prevent circular communication between LoginFragment
        // and LoginAdapter)

        if(loginFragmentDelegate == null) {
            return null;
        }

        return loginFragmentDelegate.get();
    }

    // --------------------- //

    private SimpleFacebook simpleFacebook;
    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;

    // ----- Lifecycle Methods ----- //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        simpleFacebook = SimpleFacebook.getInstance();
        loginAdapter = new LoginAdapter();

        if(savedInstanceState != null) {
            instance_counter = savedInstanceState.getInt("counter");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginAdapter = new LoginAdapter();
        loginAdapter.setLoginAdapterDelegate(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_login_items);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated() called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance();

        // Must place sharedPreference here for it to save states properly
        sharedPreferences = BPUtils.newSPrefInstance("log_states");

        // Keeping track of counter
        if(sharedPreferences != null) {
            instance_counter = sharedPreferences.getInt(COUNTER, 0);
        }

        // Place the recyclerview stuff here in order for LoginAdapterViewHolder to instantiate

        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(loginAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
        outState.putInt("counter", instance_counter);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView() called");
        super.onDestroyView();
        BPUtils.putSharedPrefCounter(sharedPreferences, FILE_NAME, COUNTER, instance_counter);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDetach() called");
        super.onDetach();
    }

    // ----- ----- ---- ----- //

    /**
     *
     * LoginAdapter.LoginAdapterDelegate implemented methods
     *
     */

    @Override
    public void onLoginSwitchActivated(LoginAdapter loginAdapter, int adapterPosition, boolean isChecked) {
        switch(adapterPosition) {
            case 0:
                if(isChecked) {
                    Log.v(TAG, "Logged into Facebook");
                    fbLogin(simpleFacebook, adapterPosition);
                    break;
                }
                else {
                    Log.v(TAG, "Logged out of Facebook");
                    fbLogout(simpleFacebook, adapterPosition);
                    break;
                }
            case 1:
                if(isChecked) {
                    Log.v(TAG, "Logged into Twitter");
                    break;
                }
                else {
                    Log.v(TAG, "Logged out of Twitter");
                    break;
                }
            case 2:
                if(isChecked) {
                    Log.v(TAG, "Logged into Instagram");
                    break;
                }
                else {
                    Log.v(TAG, "Logged out of Instagram");
                    break;
                }
        }
    }

    // ----- Facebook Methods ----- //

    private void fbLogin(SimpleFacebook simpleFacebook, final int adapterPosition) {

        editor = BPUtils.sharePrefEditor("log_states");

        final OnLoginListener onLoginListener = new OnLoginListener() {
            @Override
            public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                Log.i(TAG, "Logged in");
                BPUtils.putSharedPrefValues(sharedPreferences, FILE_NAME, FB_POSITION, adapterPosition,
                        FB_LOGIN, true);
                BPUtils.toast("Logged into Facebook");
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Login Cancelled");
                BPUtils.putSharedPrefValues(sharedPreferences, FILE_NAME, FB_POSITION, adapterPosition,
                        FB_LOGIN, false);
            }

            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "Login Exception");
                BPUtils.putSharedPrefValues(sharedPreferences, FILE_NAME, FB_POSITION, adapterPosition,
                        FB_LOGIN, false);
            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "Login Failed");
                BPUtils.putSharedPrefValues(sharedPreferences, FILE_NAME, FB_POSITION, adapterPosition,
                        FB_LOGIN, false);
                BPUtils.toast("Unable to log into Facebook");
            }
        };

        simpleFacebook.login(onLoginListener);
    }

    private void fbLogout(final SimpleFacebook simpleFacebook, final int adapterPosition) {

        instance_counter++;
        Log.v(TAG, "Instance Counter: " + instance_counter);

        editor = BPUtils.sharePrefEditor("log_states");

        final OnLogoutListener onLogoutListener = new OnLogoutListener() {
            @Override
            public void onLogout() {

                Log.i(TAG, "Logged out of Facebook");

                BPUtils.putSharedPrefValues(sharedPreferences, FILE_NAME, FB_POSITION, adapterPosition,
                        FB_LOGIN, false);

                if(instance_counter > 1) {
                    BPUtils.toast("Logged out of Facebook");
                }
            }
        };

        simpleFacebook.logout(onLogoutListener);
    }

    // ----- Twitter Methods ----- //



    // ----- Instagram Methods ----- //
}
