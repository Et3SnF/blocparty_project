package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ngynstvn.android.blocparty.R;

/**
 * Created by Ngynstvn on 9/25/15.
 */

public class TWAuthDialog extends DialogFragment {

    private static final String TAG = "(" + DialogFragment.class.getSimpleName() +"): ";

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private EditText editText;

    /**
     *
     *  Instantiation Method
     *
     */

    public static TWAuthDialog newInstance() {
        TWAuthDialog twAuthDialog = new TWAuthDialog();
        Bundle bundle = new Bundle();

        twAuthDialog.setArguments(bundle);
        return twAuthDialog;
    }

    //----- Lifecycle Methods ----- //

    @Override
    public void onAttach(Activity activity) {
        Log.v(TAG, "onAttach() called");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog() called");

        builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.twitter_auth, null);
        editText = (EditText) view.findViewById(R.id.et_twitter_auth);

        builder.setTitle("Authenticate Twitter")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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

        final AlertDialog alertDialog = (AlertDialog) getDialog();

        if(alertDialog != null) {

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            });


            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Positive Button Clicked");
                    boolean closeDialog = false;

                    String pin = editText.getText().toString();

                    if(closeDialog) {
                        dismiss();
                        getFragmentManager().beginTransaction().replace(R.id.fl_activity_blocparty,
                                LoginFragment.newInstance());
                    }
                }
            });
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.v(TAG, "onCancel() called");
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
        Log.v(TAG, "onAttach() called");
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

    // --------------------------- //
}
