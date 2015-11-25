package com.ngynstvn.android.blocparty.ui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.ui.imageview.PanAndZoomListener;
import com.squareup.picasso.Picasso;

/**
 * Created by Ngynstvn on 11/22/15.
 */

public class FullScreenImageActivity extends AppCompatActivity {

    private static final String CLASS_TAG = BPUtils.classTag(FullScreenImageActivity.class);

    private FrameLayout imageContainer;
    private ImageView panZoomPostImage;

    private String postImageURL;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BPUtils.logMethod(CLASS_TAG);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        postImageURL = getIntent().getStringExtra(BPUtils.POST_IMAGE_URL);

        imageContainer = (FrameLayout) findViewById(R.id.fl_activity_full_screen_image);

        panZoomPostImage = (ImageView) findViewById(R.id.iv_post_image_pan_zoom);
        panZoomPostImage.setScaleType(ImageView.ScaleType.MATRIX);
        Picasso.with(BlocpartyApplication.getSharedInstance()).load(postImageURL).into(panZoomPostImage);

        imageContainer.setOnTouchListener(new PanAndZoomListener(imageContainer, panZoomPostImage,
                PanAndZoomListener.Anchor.CENTER));
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
