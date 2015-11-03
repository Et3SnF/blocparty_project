package com.ngynstvn.android.blocparty.ui.activity;

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
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.adapter.AddUserPagerAdapter;
import com.ngynstvn.android.blocparty.ui.fragment.CollectionModeDialog;
import com.ngynstvn.android.blocparty.ui.tabs.SlidingTabLayout;

/**
 * Created by Ngynstvn on 11/2/15.
 */

public class AddCollectionActivity extends AppCompatActivity {

    private static String TAG = BPUtils.classTag(AddCollectionActivity.class);

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private AddUserPagerAdapter addUserPagerAdapter;

    private TextView collectionInstr;
    private EditText collectionInputBox;
    private TextView collectionInputValue;
    private TextView collectionInputValueLimit;
    private TextView userInstr;

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
                    finish();
                    showCollectionModeDialog();
                    return true;
                } else if (item.getItemId() == R.id.action_add_collection_save) {
                    Log.v(TAG, "Save Add Collection Clicked");
                    finish();
                    showCollectionModeDialog();
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

    private void showCollectionModeDialog() {
        CollectionModeDialog collectionModeDialog = CollectionModeDialog.newInstance();
        collectionModeDialog.show(getFragmentManager(), "collection_mode_dialog");
    }

}
