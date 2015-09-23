package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.LoginItem;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.LoginAdapterViewHolder> {

    private LoginItem[] loginItems = getLoginItems();

    @Override
    public LoginAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.login_item,
                viewGroup, false);
        return new LoginAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(LoginAdapterViewHolder loginAdapterViewHolder, int i) {
        LoginItem loginItem = loginItems[i];
        loginAdapterViewHolder.updateViewHolder(loginItem);
    }

    @Override
    public int getItemCount() {
        return loginItems.length;
    }

    /*
     * Interface Material
     */

    public interface LoginAdapterDelegate {
        void onFBLoginClicked(LoginAdapter loginAdapter);
        void onFBDismissClicked(LoginAdapter loginAdapter);
        void onTwitterLoginClicked(LoginAdapter loginAdapter);
        void onTwitterDismissClicked(LoginAdapter loginAdapter);
        void onIGLoginClicked(LoginAdapter loginAdapter);
        void onIGDismissClicked(LoginAdapter loginAdapter);
    }

    private WeakReference<LoginAdapterDelegate> loginAdapterDelegate;

    public void setLoginAdapterDelegate(LoginAdapterDelegate loginAdapterDelegate) {
        this.loginAdapterDelegate = new WeakReference<LoginAdapterDelegate>(loginAdapterDelegate);
    }

    public LoginAdapterDelegate getLoginAdapterDelegate() {

        if(loginAdapterDelegate == null) {
            return null;
        }

        return loginAdapterDelegate.get();
    }

    // --------------------- //

    // ----- Inner Class ----- //

    class LoginAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView loginItemName;
        TextView loginItemDescription;
        ImageView loginItemLogo;
        Button loginButton;
        Button dismissButton;

        LoginItem loginItem;

        public LoginAdapterViewHolder(View itemView) {
            super(itemView);
            loginItemName = (TextView) itemView.findViewById(R.id.tv_login_item_title);
            loginItemDescription = (TextView) itemView.findViewById(R.id.tv_login_item_info);
            loginItemLogo = (ImageView) itemView.findViewById(R.id.v_login_item_logo);
            loginButton = (Button) itemView.findViewById(R.id.btn_login);
            dismissButton = (Button) itemView.findViewById(R.id.btn_dismiss_login);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getLoginAdapterDelegate() != null) {
                        if(getAdapterPosition() == 0) {
                            getLoginAdapterDelegate().onFBLoginClicked(LoginAdapter.this);
                        }
                        else if(getAdapterPosition() == 1) {
                            getLoginAdapterDelegate().onTwitterLoginClicked(LoginAdapter.this);
                        }
                        else if(getAdapterPosition() == 2) {
                            getLoginAdapterDelegate().onIGLoginClicked(LoginAdapter.this);
                        }
                    }
                }
            });

            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getLoginAdapterDelegate() != null) {
                        if(getAdapterPosition() == 0) {
                            getLoginAdapterDelegate().onFBDismissClicked(LoginAdapter.this);
                        }
                        else if(getAdapterPosition() == 1) {
                            getLoginAdapterDelegate().onTwitterDismissClicked(LoginAdapter.this);
                        }
                        else if(getAdapterPosition() == 2) {
                            getLoginAdapterDelegate().onIGDismissClicked(LoginAdapter.this);
                        }
                    }
                }
            });
        }

        void updateViewHolder(LoginItem loginItem) {
            this.loginItem = loginItem;
            loginItemName.setText(loginItem.getItemName());
            loginItemDescription.setText(loginItem.getItemDescription());
            loginItemLogo.setBackgroundResource(loginItem.getItemLogoPath());
        }

    }

    // Method to add any additional social media items

    private LoginItem[] getLoginItems() {
        LoginItem[] loginItems = new LoginItem[3];
        loginItems[0] = new LoginItem("Facebook", "See all of the photos your Facebook friends post!", R.drawable.fb_logo);
        loginItems[1] = new LoginItem("Twitter", "View all photos on your Twitter feed.", R.drawable.twitter_logo);
        loginItems[2] = new LoginItem("Instagram", "Browse your Instagram feed.", R.drawable.ig_logo);
        return loginItems;
    }

}
