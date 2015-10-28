package com.ngynstvn.android.blocparty.api.model;

import java.util.ArrayList;

/**
 * Created by Ngynstvn on 10/27/15.
 */
public class Collection {

    private String collectionName;
    private ArrayList<User> userList;

    public Collection(String collectionName, ArrayList<User> userList) {
        this.collectionName = collectionName;
        this.userList = new ArrayList<User>();
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void addUser(User user) {
        userList.add(user);
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
}
