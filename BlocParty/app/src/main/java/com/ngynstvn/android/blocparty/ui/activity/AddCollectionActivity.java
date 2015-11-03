package com.ngynstvn.android.blocparty.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_add_collection_dialog);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_add_collection_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_collection_add_pager);

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
