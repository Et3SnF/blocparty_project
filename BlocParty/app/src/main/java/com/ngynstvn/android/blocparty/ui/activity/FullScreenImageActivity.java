package com.ngynstvn.android.blocparty.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;

/**
 * Created by Ngynstvn on 11/22/15.
 */

public class FullScreenImageActivity extends AppCompatActivity {

    private static final String CLASS_TAG = BPUtils.classTag(FullScreenImageActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        BPUtils.logMethod(CLASS_TAG);
        super.onStart();
    }

    @Override
    protected void onResume() {
        BPUtils.logMethod(CLASS_TAG);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BPUtils.logMethod(CLASS_TAG);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        BPUtils.logMethod(CLASS_TAG);
        super.onPause();
    }

    @Override
    protected void onStop() {
        BPUtils.logMethod(CLASS_TAG);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        BPUtils.logMethod(CLASS_TAG);
        super.onDestroy();
    }
}
