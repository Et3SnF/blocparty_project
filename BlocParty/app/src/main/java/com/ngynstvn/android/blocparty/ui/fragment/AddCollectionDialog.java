package com.ngynstvn.android.blocparty.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.AddUserPagerAdapter;

/**
 * Created by Ngynstvn on 10/27/15.
 */

public class AddCollectionDialog extends DialogFragment {

    private static String TAG = BPUtils.classTag(AddCollectionDialog.class);

    private Toolbar toolbar;

    private TextView collectionInstr;
    private EditText collectionInputBox;
    private TextView collectionInputValue;
    private TextView collectionInputValueLimit;
    private TextView userInstr;

    private AddUserPagerAdapter addUserPagerAdapter;
    private FragmentTabHost fragmentTabHost;
    private ViewPager viewPager;

    // Character Tracking anonymous inner class

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Nothing here for now
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            collectionInputValue.setText(String.valueOf(s.length()));
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Nothing here for now
        }
    };

    public static AddCollectionDialog newInstance() {
        AddCollectionDialog addCollectionDialog = new AddCollectionDialog();
        Bundle bundle = new Bundle();
        addCollectionDialog.setArguments(bundle);
        return addCollectionDialog;
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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog() called");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_collection_add, null);

        addUserPagerAdapter = new AddUserPagerAdapter(getFragmentManager());
        toolbar = (Toolbar) view.findViewById(R.id.tb_add_collection_dialog);

        collectionInstr = (TextView) view.findViewById(R.id.tv_add_collection_instruction);
        collectionInputBox = (EditText) view.findViewById(R.id.et_collection_input);
        collectionInputValue = (TextView) view.findViewById(R.id.tv_collection_edittext_counter_value);
        collectionInputValueLimit = (TextView) view.findViewById(R.id.tv_collection_edittext_counter_limit);
        userInstr = (TextView) view.findViewById(R.id.tv_add_user_instructions);
        fragmentTabHost = (FragmentTabHost) view.findViewById(R.id.stl_add_collection_tabs);
        viewPager = (ViewPager) view.findViewById(R.id.vp_collection_add_pager);

        toolbar.setTitle("Add Collection");

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Save Collection Button Clicked");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Cancel Collection Button Clicked");
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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Counter related code

        final int collectionCounterLimit = 30;

        collectionInputBox.setInputType(InputType.TYPE_CLASS_TEXT);
        collectionInputBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(collectionCounterLimit)});
        collectionInputBox.addTextChangedListener(textWatcher);
        collectionInputBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        collectionInputValueLimit.setText(String.valueOf(collectionCounterLimit));
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

}
