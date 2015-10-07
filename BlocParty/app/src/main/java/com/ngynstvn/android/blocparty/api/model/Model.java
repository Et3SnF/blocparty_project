package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 10/6/15.
 */

// For database purposes only

public abstract class Model {

    private final long rowId;

    public Model(long rowId) {
        this.rowId = rowId;
    }

    public long getRowId() {
        return rowId;
    }
}
