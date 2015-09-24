package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.LoginAdapter;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

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
        void onFBLogin(LoginFragment loginFragment, boolean setCheck);
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

        // Place the recyclerview stuff here in order for LoginAdapterViewHolder to instantiate

        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(loginAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
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
    public void onFBLoginClicked(LoginAdapter loginAdapter) {
        fbLogin(simpleFacebook);
    }

    @Override
    public void onFBLogoutClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onTwitterLoginClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onTwitterLogoutClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onIGLoginClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onIGLogoutClicked(LoginAdapter loginAdapter) {

    }

    // ----- Facebook Methods ----- //

    private void fbLogin(SimpleFacebook simpleFacebook) {
        final OnLoginListener onLoginListener = new OnLoginListener() {
            @Override
            public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                Log.i(TAG, "Logged in");

                if(getLoginFragmentDelegate() != null) {
                    getLoginFragmentDelegate().onFBLogin(LoginFragment.this, true);
                }

            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Login Cancelled");

                if(getLoginFragmentDelegate() != null) {
                    getLoginFragmentDelegate().onFBLogin(LoginFragment.this, false);
                }

            }

            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "Login Exception");

                if(getLoginFragmentDelegate() != null) {
                    getLoginFragmentDelegate().onFBLogin(LoginFragment.this, false);
                }

            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "Login Failed");

                if(getLoginFragmentDelegate() != null) {
                    getLoginFragmentDelegate().onFBLogin(LoginFragment.this, false);
                }

            }
        };

        simpleFacebook.login(onLoginListener);
    }

    // ----- Twitter Methods ----- //



    // ----- Instagram Methods ----- //
}
