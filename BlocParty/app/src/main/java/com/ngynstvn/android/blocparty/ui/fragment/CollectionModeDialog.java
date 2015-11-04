package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import com.ngynstvn.android.blocparty.ui.activity.AddCollectionActivity;
import com.ngynstvn.android.blocparty.ui.adapter.CollectionAdapter;

/**
 * Created by Ngynstvn on 10/27/15.
 */

public class CollectionModeDialog extends DialogFragment {

    private static String TAG = BPUtils.classTag(CollectionModeDialog.class);

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TextView emptyCollectionText;

    private CollectionAdapter collectionAdapter;

    // ---- Instantiation Method with Bundle ----- //

    public static CollectionModeDialog newInstance() {
        CollectionModeDialog collectionModeDialog = new CollectionModeDialog();

        Bundle bundle = new Bundle();

        collectionModeDialog.setArguments(bundle);

        return collectionModeDialog;
    }

    // ----- Lifecycle Methods ------ //

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach() called");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);

        BlocpartyApplication.getSharedDataSource().getCollectionArrayList().clear();
        BlocpartyApplication.getSharedDataSource().fetchCollections();

        for(int i = 0; i < BlocpartyApplication.getSharedDataSource().getCollectionArrayList().size(); i++) {
            BlocpartyApplication.getSharedDataSource().fetchCollectionUser(BPUtils.USER_TABLE,
                    BPUtils.COLLECTION_TABLE, BPUtils.USER_PROFILE_ID, "collection_name",
                    BlocpartyApplication.getSharedDataSource().getCollectionArrayList().get(i).getCollectionName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog() called");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_collection_dialog, null);

        toolbar = (Toolbar) view.findViewById(R.id.tb_collection_dialog);

        emptyCollectionText = (TextView) view.findViewById(R.id.tv_empty_collection);

        linearLayoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rl_collection_items);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        collectionAdapter = new CollectionAdapter();
        recyclerView.setAdapter(collectionAdapter);

        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Collection Mode Save Button Clicked");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Collection Mode Cancel Button Clicked");
                    }
                });

        toolbar.setTitle("Choose Collection");
        toolbar.inflateMenu(R.menu.menu_collection);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_add_collection) {
                    Log.v(TAG, "Add Collection Button Clicked");
                    dismiss();
                    getActivity().startActivity(new Intent(getActivity(), AddCollectionActivity.class));
                    return true;
                }

                return false;
            }
        });

        if(BlocpartyApplication.getSharedDataSource().getCollectionArrayList().size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyCollectionText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyCollectionText.setVisibility(View.GONE);
        }

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(TAG, "onActivityCreated() called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart() called");
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Override any positive and negative buttons here
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.v(TAG, "onCancel() called");
        // This is called only if I touched outside of the dialog to dismiss
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.v(TAG, "onDismiss() called");
        super.onDismiss(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.v(TAG, "onDestroyView() called");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.v(TAG, "onDetach() called");
        super.onDetach();
    }
}
