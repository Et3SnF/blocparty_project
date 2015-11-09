package com.ngynstvn.android.blocparty.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.AddUserPagerAdapter;
import com.ngynstvn.android.blocparty.ui.tabs.SlidingTabLayout;

import java.util.Map;

/**
 * Created by Ngynstvn on 11/2/15.
 */

public class AddCollectionActivity extends AppCompatActivity {

    private static String TAG = BPUtils.classTag(AddCollectionActivity.class);

    private SharedPreferences sPrefChkStates;
    private Map<String, ?> sPrefUserKeys;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private AddUserPagerAdapter addUserPagerAdapter;

    private EditText collectionInputBox;
    private TextView collectionInputValue;
    private TextView collectionInputValueLimit;
    private TextView collectionInstr;
    private TextView userInstr;

    private int errorCode = -1;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_add);

        sPrefChkStates = BPUtils.newSPrefInstance(BPUtils.CHECKED_STATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_add_collection_dialog);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_add_collection_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_collection_add_pager);
        collectionInstr = (TextView) findViewById(R.id.tv_add_collection_instruction);
        collectionInputBox = (EditText) findViewById(R.id.et_collection_input);
        collectionInputValue = (TextView) findViewById(R.id.tv_collection_edittext_counter_value);
        collectionInputValueLimit = (TextView) findViewById(R.id.tv_collection_edittext_counter_limit);
        userInstr = (TextView) findViewById(R.id.tv_add_user_instructions);

        toolbar.setTitle("Add Collection");
        toolbar.inflateMenu(R.menu.menu_add_collection);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add_collection_close) {
                    Log.v(TAG, "Cancel Add Collection Clicked");
                    BPUtils.clearSPrefTable(BPUtils.newSPrefInstance(BPUtils.CHECKED_STATE), BPUtils.CHECKED_STATE);

                    showMainActivity();

                    return true;
                }
                else if (item.getItemId() == R.id.action_add_collection_save) {
                    Log.v(TAG, "Save Add Collection Clicked");

                    if(collectionInputBox != null) {

                        sPrefUserKeys = sPrefChkStates.getAll();

                        String input = collectionInputBox.getText().toString();

                        if(collectionInputBox.getText().toString().length() == 0) {
                            errorCode = 0;
                            ErrorDialog.newInstance(errorCode).show(getFragmentManager(), "0");
                            return false;
                        }

                        if(BlocpartyApplication.getSharedDataSource().isValueInDB(BPUtils.COLLECTION_TABLE,
                                "collection_name", input)) {
                            errorCode = 1;
                            ErrorDialog.newInstance(errorCode, input).show(getFragmentManager(), "1");
                            return false;
                        }

                        if(sPrefUserKeys.size() == 0) {
                            errorCode = 2;
                            ErrorDialog.newInstance(errorCode, input).show(getFragmentManager(), "2");
                            return false;
                        }

                        // Insert into collection_table with user ids

                        for(Map.Entry<String, ?> key : sPrefUserKeys.entrySet()) {
                            BlocpartyApplication.getSharedDataSource().addCollectionToDB(input, key.getKey());
                        }

                        showMainActivity();
                        BPUtils.clearSPrefTable(BPUtils.newSPrefInstance(BPUtils.CHECKED_STATE), BPUtils.CHECKED_STATE);
                    }

                    return true;
                }

                return false;
            }
        });

        addUserPagerAdapter = new AddUserPagerAdapter(getFragmentManager());
        viewPager.setAdapter(addUserPagerAdapter);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.material_indigo_500);
            }
        });

        viewPager.setAdapter(addUserPagerAdapter);
        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();

        // Counter related code

        final int collectionCounterLimit = 30;

        collectionInputBox.setInputType(InputType.TYPE_CLASS_TEXT);
        collectionInputBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(collectionCounterLimit)});
        collectionInputBox.addTextChangedListener(textWatcher);
        collectionInputBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        collectionInputValueLimit.setText(String.valueOf(collectionCounterLimit));
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }

    // -----   -----  -----  -----  ----- //

    private void showMainActivity() {
        finish();
        Intent intent = new Intent(AddCollectionActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //clears back stack
        intent.putExtra("show_dialog", true);
        startActivity(intent);
    }

    public static class ErrorDialog extends DialogFragment {

        int errorCode = 0;
        String variableValue;
        String errorMessage;

        public static ErrorDialog newInstance(int errorCode, String value) {
            ErrorDialog errorDialog = new ErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("errorCode", errorCode);
            bundle.putString("value", value);
            errorDialog.setArguments(bundle);
            return errorDialog;
        }

        public static ErrorDialog newInstance(int errorCode) {
            ErrorDialog errorDialog = new ErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("errorCode", errorCode);
            errorDialog.setArguments(bundle);
            return errorDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            errorCode = getArguments().getInt("errorCode");
            variableValue = getArguments().getString("value");

            switch (errorCode) {
                case 0:
                    errorMessage = "Collection name cannot be empty.";
                    break;
                case 1:

                    if(variableValue != null) {
                        errorMessage = variableValue + " is already a Collection name.";
                    }

                    break;
                case 2:
                    errorMessage = "You must add at least one user to the collection.";
                    break;
            }

            return new AlertDialog.Builder(getActivity())
                    .setMessage(errorMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .create();
        }
    }
}
