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

import java.util.List;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

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

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

    private SimpleFacebook simpleFacebook;
    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(loginAdapter);
        return view;
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        simpleFacebook = SimpleFacebook.getInstance();
    }

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
    public void onFBDismissClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onTwitterLoginClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onTwitterDismissClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onIGLoginClicked(LoginAdapter loginAdapter) {

    }

    @Override
    public void onIGDismissClicked(LoginAdapter loginAdapter) {

    }

    // ----- Separate Methods ----- //

    private void fbLogin(SimpleFacebook simpleFacebook) {
        final OnLoginListener onLoginListener = new OnLoginListener() {
            @Override
            public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                Log.i(TAG, "Logged in");
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Login Cancelled");
            }

            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "Login Exception");
            }

            @Override
            public void onFail(String s) {
                Log.i(TAG, "Login Failed");
            }
        };

        simpleFacebook.login(onLoginListener);
    }
}
