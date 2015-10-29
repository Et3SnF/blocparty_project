package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 10/27/15.
 */
public class User {

    private String fullName;
    private long userId;
    private String userSocNetwork;
    private String userProfilePicUrl;

    public User(String fullName, long userId, String userSocNetwork) {
        this.fullName = fullName;
        this.userId = userId;
        this.userSocNetwork = userSocNetwork;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserSocNetwork(String userSocNetwork) {
        this.userSocNetwork = userSocNetwork;
    }

    public String getUserSocNetwork() {
        return userSocNetwork;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }
}
