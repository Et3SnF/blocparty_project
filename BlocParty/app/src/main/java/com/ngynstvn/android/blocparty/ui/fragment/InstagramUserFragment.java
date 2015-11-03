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
import com.ngynstvn.android.blocparty.ui.adapter.IGUserAdapter;

/**
 * Created by Ngynstvn on 11/2/15.
 */
public class InstagramUserFragment extends Fragment {

    private static final String TAG = InstagramUserFragment.class.getSimpleName();

    private TextView emptyListText;
    private RecyclerView recyclerView;
    private IGUserAdapter igUserAdapter;

    public static InstagramUserFragment newInstance(int position) {
        InstagramUserFragment facebookUserFragment = new InstagramUserFragment();
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
        igUserAdapter = new IGUserAdapter();

        if(BlocpartyApplication.getSharedDataSource().getIgUserArrayList().size() > 0) {
            BlocpartyApplication.getSharedDataSource().getIgUserArrayList().clear();
        }

        BlocpartyApplication.getSharedDataSource().fetchIGUsers("user_social_network", "Instagram");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_instagram_users, container, false);

        emptyListText = (TextView) view.findViewById(R.id.tv_empty_users_ig);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_instagram_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlocpartyApplication.getSharedInstance()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(igUserAdapter);

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

        if(BlocpartyApplication.getSharedDataSource().getIgUserArrayList().size() == 0) {
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
