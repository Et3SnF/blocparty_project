package com.ngynstvn.android.blocparty.api.model;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class LoginItem {

    private String itemName;
    private String itemDescription;
    private int itemLogoPath;
    private boolean isLoggedIn;

    public LoginItem(String itemName, String itemDescription, int itemLogoPath, boolean isLoggedIn) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemLogoPath = itemLogoPath;
        this.isLoggedIn = isLoggedIn;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemLogoPath(int itemLogoPath) {
        this.itemLogoPath = itemLogoPath;
    }

    public int getItemLogoPath() {
        return itemLogoPath;
    }


    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

}
