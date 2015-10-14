package com.ngynstvn.android.blocparty.api.model;

import com.ngynstvn.android.blocparty.BPUtils;

/**
 * Created by Ngynstvn on 10/6/15.
 */

public class PostItem extends Model {

    private static final String TAG = BPUtils.classTag(PostItem.class);

    private String opFullName;
    private String opProfilePicUrl;
    private String postImageUrl;
    private String postCaption;
    private long postPublishDate;
    private boolean isLiked;

    // Constructors

    public PostItem(long rowId) {
        super(rowId);
        opFullName = "";
        opProfilePicUrl = "";
        postImageUrl = "";
        postCaption = "";
        postPublishDate = 0;
        isLiked = false;
    }

    public PostItem(long rowId, String opFullName, String opProfilePicUrl,
                    String postImageUrl, String postCaption, long postPublishDate, boolean isLiked) {
        super(rowId);
        this.opFullName = opFullName;
        this.opProfilePicUrl = opProfilePicUrl;
        this.postImageUrl = postImageUrl;
        this.postCaption = postCaption;
        this.postPublishDate = postPublishDate;
        this.isLiked = isLiked;
    }

    // Setters and Getters

    public void setOpFullName(String opFullName) {
        this.opFullName = opFullName;
    }

    public String getOpFullName() {
        return opFullName;
    }

    public void setOpProfilePicUrl(String opProfilePicUrl) {
        this.opProfilePicUrl = opProfilePicUrl;
    }

    public String getOpProfilePicUrl() {
        return opProfilePicUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostPublishDate(long postPublishDate) {
        this.postPublishDate = postPublishDate;
    }

    public long getPostPublishDate() {
        return postPublishDate;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public boolean isLiked() {
        return isLiked;
    }
}
