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
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.TWUserAdapter;

/**
 * Created by Ngynstvn on 11/2/15.
 */
public class TwitterUserFragment extends Fragment {

    private static final String TAG = TwitterUserFragment.class.getSimpleName();

    private TextView emptyListText;
    private RecyclerView recyclerView;
    private TWUserAdapter twUserAdapter;

    public static TwitterUserFragment newInstance(int position) {
        TwitterUserFragment facebookUserFragment = new TwitterUserFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        facebookUserFragment.setArguments(bundle);
        return facebookUserFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach() API <= 22 called");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach() API > 23 called");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        twUserAdapter = new TWUserAdapter();

        if(BlocpartyApplication.getSharedDataSource().getTwUserArrayList().size() > 0) {
            BlocpartyApplication.getSharedDataSource().getTwUserArrayList().clear();
        }

        BlocpartyApplication.getSharedDataSource().fetchUsers("Twitter");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_twitter_users, container, false);

        emptyListText = (TextView) view.findViewById(R.id.tv_empty_users_tw);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_twitter_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(twUserAdapter);

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

        if(BlocpartyApplication.getSharedDataSource().getTwUserArrayList().size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyListText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListText.setVisibility(View.GONE);
        }
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

}
