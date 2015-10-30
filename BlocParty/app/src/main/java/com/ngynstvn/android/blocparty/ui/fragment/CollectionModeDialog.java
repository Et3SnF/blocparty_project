package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.CollectionAdapter;

/**
 * Created by Ngynstvn on 10/27/15.
 */

public class CollectionModeDialog extends DialogFragment {

    private static String TAG = BPUtils.classTag(CollectionModeDialog.class);

    private RecyclerView recyclerView;
    private Button addButton;

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
        collectionAdapter = new CollectionAdapter();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog() called");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_collection_dialog, null);

        addButton = (Button) view.findViewById(R.id.btn_add_collection);

        recyclerView = (RecyclerView) view.findViewById(R.id.rl_collection_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(collectionAdapter);

        if(BlocpartyApplication.getSharedDataSource().getCollectionArrayList().size() == 0) {
            recyclerView.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Add Button Clicked");
                showAddCollectionDialog();
            }
        });

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
        // Override any positive and negative buttons here
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

    // ----------------------------- //

    private void showAddCollectionDialog() {
        AddCollectionDialog addCollectionDialog = AddCollectionDialog.newInstance();
        addCollectionDialog.show(getFragmentManager(), "add_collection_dialog");
    }

}
