package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 10/6/15.
 */

public class Image {

    private String imageUrl = "";
    private String imageCaption = "";

    public Image(String imageUrl, String imageCaption) {
        this.imageUrl = imageUrl;
        this.imageCaption = imageCaption;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getImageCaption() {
        return imageCaption;
    }
}
