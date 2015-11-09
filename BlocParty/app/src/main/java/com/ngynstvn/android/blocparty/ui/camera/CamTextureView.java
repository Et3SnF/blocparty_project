package com.ngynstvn.android.blocparty.ui.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Ngynstvn on 11/8/15.
 */
public class CamTextureView extends TextureView {

    public CamTextureView(Context context) {
        super(context);
    }

    public CamTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CamTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CamTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
