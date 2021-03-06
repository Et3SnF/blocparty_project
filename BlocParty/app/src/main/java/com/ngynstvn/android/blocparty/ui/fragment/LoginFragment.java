package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.LoginAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class LoginFragment extends Fragment implements LoginAdapter.LoginAdapterDelegate {

    private static final String CLASS_TAG = "(" + LoginFragment.class.getSimpleName() + ") ";

    private static SharedPreferences sharedPreferences;
    private static int instance_counter = 0;

    // Fields

    private LoginAdapter loginAdapter;
    private RecyclerView recyclerView;
    private TextView welcomeMessage;

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
        BPUtils.logMethod(CLASS_TAG, "API <= 23");
        super.onAttach(activity);
        loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>((LoginFragmentDelegate) activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.v(CLASS_TAG, "API > 23");
        super.onAttach(context);
        loginFragmentDelegate = new WeakReference<LoginFragmentDelegate>((LoginFragmentDelegate) getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onCreate(savedInstanceState);

        loginAdapter = new LoginAdapter();
        loginAdapter.setLoginAdapterDelegate(this);

        sharedPreferences = getActivity().getSharedPreferences(BPUtils.FILE_NAME, Context.MODE_PRIVATE);

        if(sharedPreferences != null) {
            instance_counter = sharedPreferences.getInt("counter", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        welcomeMessage = (TextView) view.findViewById(R.id.tv_login_message);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_login_items);

        if(instance_counter > 0 && welcomeMessage != null) {
            welcomeMessage.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        BPUtils.logMethod(CLASS_TAG);
        super.onResume();

        // Place the recyclerview stuff here in order for LoginAdapterViewHolder to instantiate

        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(loginAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onSaveInstanceState(outState);
        outState.putInt("counter", instance_counter);
    }

    @Override
    public void onPause() {
        BPUtils.logMethod(CLASS_TAG);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        BPUtils.logMethod(CLASS_TAG);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        BPUtils.logMethod(CLASS_TAG);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        BPUtils.logMethod(CLASS_TAG);
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

                    Log.v(CLASS_TAG, "Position " + adapterPosition + " : isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        getLoginFragmentDelegate().onFBLogin(this, adapterPosition);
                    }
                    break;
                }
                else {

                    Log.v(CLASS_TAG, "Position " + adapterPosition + " : !isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        getLoginFragmentDelegate().onFBLogout(this, adapterPosition);
                    }
                    break;
                }
            case 1:
                if(isChecked) {

                    Log.v(CLASS_TAG, "Position " + adapterPosition + " : isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        getLoginFragmentDelegate().onTWLogin(this, adapterPosition);
                    }
                    break;
                }
                else {

                    Log.v(CLASS_TAG, "Position " + adapterPosition + " : !isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        getLoginFragmentDelegate().onTWLogout(this, adapterPosition);
                    }
                    break;
                }

            case 2:
                if(isChecked) {

                    Log.v(CLASS_TAG, "Position " + adapterPosition + " : isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        getLoginFragmentDelegate().onIGLogin(this, adapterPosition);
                    }
                    break;
                }
                else {

                    Log.v(CLASS_TAG, "Position " + adapterPosition + " : !isChecked activated");

                    if(getLoginFragmentDelegate() != null) {
                        getLoginFragmentDelegate().onIGLogout(this, adapterPosition);
                    }
                    break;
                }
        }
    }
}
