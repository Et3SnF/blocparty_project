package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.DataSource;
import com.ngynstvn.android.blocparty.api.model.Collection;
import com.ngynstvn.android.blocparty.api.model.User;
import com.ngynstvn.android.blocparty.ui.activity.AddCollectionActivity;
import com.ngynstvn.android.blocparty.ui.activity.MainActivity;
import com.ngynstvn.android.blocparty.ui.adapter.CollectionAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ngynstvn on 10/27/15.
 */

public class CollectionModeDialog extends DialogFragment implements CollectionAdapter.CollectionAdapterDelegate,
        CollectionAdapter.CollectionAdapterDataSource {

    private static String CLASS_TAG = BPUtils.classTag(CollectionModeDialog.class);

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView emptyCollectionText;

    private CollectionAdapter collectionAdapter;

    private ArrayList<Collection> collectionArrayList;
    private HashMap<String, ArrayList<User>> collectionUsersMap;

    // ---- Instantiation Method with Bundle ----- //

    public static CollectionModeDialog newInstance() {
        CollectionModeDialog collectionModeDialog = new CollectionModeDialog();
        Bundle bundle = new Bundle();
        collectionModeDialog.setArguments(bundle);
        return collectionModeDialog;
    }

    // ----- Lifecycle Methods ------ //

    @Override
    public void onAttach(Context context) {
        BPUtils.logMethod(CLASS_TAG, "API > 23");
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        BPUtils.logMethod(CLASS_TAG, "API <= 23");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onCreate(savedInstanceState);
        collectionArrayList = new ArrayList<>();
        collectionUsersMap = new HashMap<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_collection_dialog, null);

        toolbar = (Toolbar) view.findViewById(R.id.tb_collection_dialog);

        collectionAdapter = new CollectionAdapter();
        collectionAdapter.setCollectionAdapterDelegate(this);
        collectionAdapter.setCollectionAdapterDataSource(this);

        emptyCollectionText = (TextView) view.findViewById(R.id.tv_empty_collection);

        linearLayoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rl_collection_items);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(collectionAdapter);

        builder.setView(view)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(CLASS_TAG, "Collection Close Button Clicked");
                    }
                });

        toolbar.setTitle("Choose Collection");
        toolbar.inflateMenu(R.menu.menu_collection);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add_collection) {
                    Log.v(CLASS_TAG, "Add Collection Button Clicked");
                    dismiss();
                    getActivity().startActivity(new Intent(getActivity(), AddCollectionActivity.class));
                    return true;
                }

                return false;
            }
        });

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        BPUtils.logMethod(CLASS_TAG);
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onResume() {
        BPUtils.logMethod(CLASS_TAG);
        super.onResume();

        BlocpartyApplication.getSharedDataSource().fetchCollections(new DataSource.Callback<List<Collection>>() {
            @Override
            public void onFetchingComplete(List<Collection> collections) {
                collectionArrayList.addAll(collections);

                if (collections.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyCollectionText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyCollectionText.setVisibility(View.GONE);
                }

                if (collectionArrayList != null && collectionArrayList.size() != 0) {
                    for (final Collection collection : collectionArrayList) {
                        BlocpartyApplication.getSharedDataSource().fetchCollectionUsers(collection.getCollectionName(),
                                new DataSource.Callback<List<User>>() {
                                    @Override
                                    public void onFetchingComplete(List<User> users) {
                                        collectionUsersMap.put(collection.getCollectionName(), (ArrayList<User>) users);
                                        collectionAdapter.notifyItemRangeChanged(0, users.size());
                                    }
                                });
                    }
                }

                collectionAdapter.notifyItemRangeInserted(0, collections.size());
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        BPUtils.logMethod(CLASS_TAG);
        super.onCancel(dialog);
        // This is called only if I touched outside of the dialog to dismiss
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        BPUtils.logMethod(CLASS_TAG);
        super.onDismiss(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        BPUtils.logMethod(CLASS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        BPUtils.logMethod(CLASS_TAG);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        BPUtils.logMethod(CLASS_TAG);
        super.onDetach();
    }

    /**
     *
     * CollectionAdapter.CollectionAdapterDelegate Methods
     *
     */

    @Override
    public void onItemClicked(CollectionAdapter collectionAdapter, int position) {
        Log.v(CLASS_TAG, "onItemClicked() called");
        BPUtils.putSPrefStrValue(BPUtils.newSPrefInstance(BPUtils.FILE_NAME),
                BPUtils.FILE_NAME, BPUtils.CURRENT_COLLECTION,
                collectionArrayList.get(position).getCollectionName());
        restartActivity();
        dismiss();
    }

    /**
     *
     * CollectionAdapter.CollectionAdapterDataSource Methods
     *
     */

    @Override
    public int getCollectionItemCount(CollectionAdapter collectionAdapter) {
        return collectionArrayList.size();
    }

    @Override
    public Collection getCollection(CollectionAdapter collectionAdapter, int position) {
        return collectionArrayList.get(position);
    }

    @Override
    public ArrayList<User> getCollectionUsersList(CollectionAdapter collectionAdapter, int position) {
        return collectionUsersMap.get(collectionArrayList.get(position).getCollectionName());
    }

// ---- Other Methods ---- //

    private void restartActivity() {
        // only use this if you're not in MainActivity itself!
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
