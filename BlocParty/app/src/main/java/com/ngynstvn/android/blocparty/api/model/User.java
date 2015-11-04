package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 10/27/15.
 */
public class User extends Model {

    private String userFullName;
    private String userSocNetwork;
    private long userProfileId;
    private String userProfilePicUrl;

    // The value associated with the collection. This prevents misnaming a collection later later.

    public User(long rowId) {
        super(rowId);
    }

    public User(long rowId, String userFullName, String userSocNetwork, long userProfileId,
                String userProfilePicUrl) {
        super(rowId);
        this.userFullName = userFullName;
        this.userSocNetwork = userSocNetwork;
        this.userProfileId = userProfileId;
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserSocNetwork(String userSocNetwork) {
        this.userSocNetwork = userSocNetwork;
    }

    public String getUserSocNetwork() {
        return userSocNetwork;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

}
