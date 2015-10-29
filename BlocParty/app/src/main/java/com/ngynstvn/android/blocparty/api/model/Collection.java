package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 10/27/15.
 */
public class Collection extends Model {

    private String collectionName;

    public Collection(long rowId, String collectionName) {
        super(rowId);
        this.collectionName = collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}
