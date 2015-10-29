package com.ngynstvn.android.blocparty.api.model;

import com.ngynstvn.android.blocparty.BPUtils;

/**
 * Created by Ngynstvn on 10/6/15.
 */

public class PostItem extends Model {

    private static final String TAG = BPUtils.classTag(PostItem.class);

    private String opFullName;
    private long opProfileId;
    private String opProfilePicUrl;
    private long postId;
    private String postImageUrl;
    private String postCaption;
    private long postPublishDate;
    private boolean isLiked;

    // Constructors

    public PostItem(long rowId) {
        super(rowId);
        opFullName = "";
        opProfileId = 0L;
        opProfilePicUrl = "";
        postId = 0L;
        postImageUrl = "";
        postCaption = "";
        postPublishDate = 0;
        isLiked = false;
    }

    public PostItem(long rowId, String opFullName, long opProfileId, String opProfilePicUrl, long postId,
                    String postImageUrl, String postCaption, long postPublishDate, boolean isLiked) {
        super(rowId);
        this.opFullName = opFullName;
        this.opProfileId = opProfileId;
        this.opProfilePicUrl = opProfilePicUrl;
        this.postId = postId;
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

    public void setOpProfileId(long opProfileId) {
        this.opProfileId = opProfileId;
    }

    public long getOpProfileId() {
        return opProfileId;
    }

    public void setOpProfilePicUrl(String opProfilePicUrl) {
        this.opProfilePicUrl = opProfilePicUrl;
    }

    public String getOpProfilePicUrl() {
        return opProfilePicUrl;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getPostId() {
        return postId;
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
