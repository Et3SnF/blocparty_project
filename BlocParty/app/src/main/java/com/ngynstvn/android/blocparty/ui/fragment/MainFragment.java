package com.ngynstvn.android.blocparty.ui.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
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

/**
 * Created by Ngynstvn on 10/12/15.
 */
public class MainFragment extends Fragment {

    private static final String TAG = BPUtils.classTag(MainFragment.class);

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PostItemAdapter postItemAdapter;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();

        mainFragment.setArguments(bundle);

        return mainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        postItemAdapter = new PostItemAdapter();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_main_fragment);
//        swipeRefreshLayout.setColorSchemeColors(R.color.material_indigo_500);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_main_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postItemAdapter);

        return view;
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause() called");
        super.onPause();
    }
}
