package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

    // Fields

    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;

    /**
     *
     * Instantiation Method
     *
     */

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    public static LoginFragment newInstance(String igValue) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BPUtils.IG_AUTH_CODE, igValue);
        return loginFragment;
    }

    /*
     * Interface Material
     */

    public interface LoginFragmentDelegate {
        void onFBLogin(LoginFragment loginFragment, int adapterPosition);
        void onFBLogout(LoginFragment loginFragment, int adapterPosition);
        void onTWLogin(LoginFragment loginFragment, int adapterPosition);
        void onTWLogout(LoginFragment loginFragment, int adapterPosition);
        void onIGLogin(LoginFragment loginFragment, int adapterPosition);
        void onIGLogout(LoginFragment loginFragment, int adapterPosition);
    }

    private WeakReference<LoginFragmentDelegate> loginFragmentDelegate;

    public void setLoginFragmentDelegate(LoginFragmentDelegate loginFragmentDelegate) {
        this.loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>(loginFragmentDelegate);
    }

    public LoginFragmentDelegate getLoginFragmentDelegate() {
        if(loginFragmentDelegate == null) {
            return null;
        }

        return loginFragmentDelegate.get();
    }

    // ----- Lifecycle Methods ----- //

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach() API <= 22 called");
        super.onAttach(activity);
        loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>((LoginFragmentDelegate) activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.v(TAG, "onAttach() API > 23 called");
        super.onAttach(context);
        loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>((LoginFragmentDelegate) getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
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
    public void onLoginSwitchActivated(LoginAdapter loginAdapter, int adapterPosition, boolean isChecked) {

        switch(adapterPosition) {
            case 0:
                if(isChecked) {

                    Log.v(TAG, "Position " + adapterPosition + " : isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        Log.v(TAG, "onFbLogin() called");
                        getLoginFragmentDelegate().onFBLogin(this, adapterPosition);
                    }
                    break;
                }
                else {

                    Log.v(TAG, "Position " + adapterPosition + " : !isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        Log.v(TAG, "onFBLogout() called");
                        getLoginFragmentDelegate().onFBLogout(this, adapterPosition);
                    }
                    break;
                }
            case 1:
                if(isChecked) {

                    Log.v(TAG, "Position " + adapterPosition + " : isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        Log.v(TAG, "onTwLogin() called");
                        getLoginFragmentDelegate().onTWLogin(this, adapterPosition);
                    }
                    break;
                }
                else {

                    Log.v(TAG, "Position " + adapterPosition + " : !isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        Log.v(TAG, "onTwLogout() called");
                        getLoginFragmentDelegate().onTWLogout(this, adapterPosition);
                    }
                    break;
                }

            case 2:
                if(isChecked) {

                    Log.v(TAG, "Position " + adapterPosition + " : isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        Log.v(TAG, "onIGLogin() called");
                        getLoginFragmentDelegate().onIGLogin(this, adapterPosition);
                    }
                    break;
                }
                else {

                    Log.v(TAG, "Position " + adapterPosition + " : !isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        Log.v(TAG, "onIGLogout() called");
                        getLoginFragmentDelegate().onIGLogout(this, adapterPosition);
                    }
                    break;
                }
        }
    }
}
