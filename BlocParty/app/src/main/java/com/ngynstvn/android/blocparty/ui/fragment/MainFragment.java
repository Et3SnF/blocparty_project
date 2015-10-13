package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ngynstvn.android.blocparty.ui.adapter.PostItemAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 10/12/15.
 */
public class MainFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(MainFragment.class);

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PostItemAdapter postItemAdapter;
    private SharedPreferences sharedPreferences;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    /*
     * Interface Material
     */

    public interface MainFragmentDelegate {
        void onFetchingFacebookInfo(MainFragment mainFragment);
        void onFetchingTwitterInfo(MainFragment mainFragment);
        void onFetchingInstagramInfo(MainFragment mainFragment);
    }

    private WeakReference<MainFragmentDelegate> mainFragmentDelegate;

    public void setMainFragmentDelegate(MainFragmentDelegate mainFragmentDelegate) {
        this.mainFragmentDelegate = new WeakReference<MainFragmentDelegate>(mainFragmentDelegate);
    }

    public MainFragmentDelegate getMainFragmentDelegate() {
        if(mainFragmentDelegate == null) {
            return null;
        }

        return mainFragmentDelegate.get();
    }

    /**
     *
     * Lifecycle Methods
     *
     */

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach() API <= 22 called");
        super.onAttach(activity);
        mainFragmentDelegate = new WeakReference<MainFragmentDelegate>((MainFragmentDelegate) activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach() API > 23 called");
        super.onAttach(context);
        mainFragmentDelegate = new WeakReference<MainFragmentDelegate>((MainFragmentDelegate) getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

        if(getMainFragmentDelegate() != null) {

            if(sharedPreferences.getBoolean(BPUtils.FB_LOGIN, false)) {
                getMainFragmentDelegate().onFetchingFacebookInfo(this);
            }

            if(sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false)) {
                getMainFragmentDelegate().onFetchingTwitterInfo(this);
            }

            if(sharedPreferences.getBoolean(BPUtils.IG_LOGIN, false)) {
                getMainFragmentDelegate().onFetchingInstagramInfo(this);
            }
        }

        postItemAdapter = new PostItemAdapter();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_main_fragment);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_main_fragment);
//        swipeRefreshLayout.setColorSchemeColors(R.color.material_indigo_500);
        return view;
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postItemAdapter);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }
}
