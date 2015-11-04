package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 10/27/15.
 */
public class Collection extends Model {

    private String collectionName;
    private long userProfileId;

    public Collection(long rowId, String collectionName, long userProfileId) {
        super(rowId);
        this.collectionName = collectionName;
        this.userProfileId = userProfileId;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public long getUserProfileId() {
        return userProfileId;
    }
}
