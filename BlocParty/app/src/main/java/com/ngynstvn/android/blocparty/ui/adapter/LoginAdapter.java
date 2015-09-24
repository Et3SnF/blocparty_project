package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.LoginItem;
import com.ngynstvn.android.blocparty.ui.fragment.LoginFragment;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.LoginAdapterViewHolder> {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

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
        void onFBLogoutClicked(LoginAdapter loginAdapter);
        void onTwitterLoginClicked(LoginAdapter loginAdapter);
        void onTwitterLogoutClicked(LoginAdapter loginAdapter);
        void onIGLoginClicked(LoginAdapter loginAdapter);
        void onIGLogoutClicked(LoginAdapter loginAdapter);
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

    class LoginAdapterViewHolder extends RecyclerView.ViewHolder implements LoginFragment.LoginFragmentDelegate {

        private final String TAG = "(" + LoginAdapterViewHolder.class.getSimpleName() + "): ";

        TextView loginItemName;
        TextView loginItemDescription;
        ImageView loginItemLogo;
        Switch loginSwitch;

        LoginItem loginItem;

        public LoginAdapterViewHolder(View itemView) {
            super(itemView);
            loginItemName = (TextView) itemView.findViewById(R.id.tv_login_item_title);
            loginItemDescription = (TextView) itemView.findViewById(R.id.tv_login_item_info);
            loginItemLogo = (ImageView) itemView.findViewById(R.id.iv_login_item_logo);
            loginSwitch = (Switch) itemView.findViewById(R.id.sw_login);

            new LoginFragment().setLoginFragmentDelegate(this);

            loginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (getLoginAdapterDelegate() != null) {
                            if (getAdapterPosition() == 0) {
                                getLoginAdapterDelegate().onFBLoginClicked(LoginAdapter.this);
                            } else if (getAdapterPosition() == 1) {
                                getLoginAdapterDelegate().onTwitterLoginClicked(LoginAdapter.this);
                            } else if (getAdapterPosition() == 2) {
                                getLoginAdapterDelegate().onIGLoginClicked(LoginAdapter.this);
                            }
                        }
                    } else {
                        if (getLoginAdapterDelegate() != null) {
                            if (getAdapterPosition() == 0) {
                                getLoginAdapterDelegate().onFBLogoutClicked(LoginAdapter.this);
                            } else if (getAdapterPosition() == 1) {
                                getLoginAdapterDelegate().onTwitterLogoutClicked(LoginAdapter.this);
                            } else if (getAdapterPosition() == 2) {
                                getLoginAdapterDelegate().onIGLogoutClicked(LoginAdapter.this);
                            }
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
            loginSwitch.setChecked(loginItem.isLoggedIn());
        }

        /**
         *
         * LoginFragment.LoginFragmentDelegate methods
         *
         */

        @Override
        public void onFBLogin(LoginFragment loginFragment, boolean setCheck) {
            Log.v(LoginAdapter.TAG, "onFBLogin() called");
            loginItem.setIsLoggedIn(setCheck);
        }
    }

    // Method to add any additional social media items

    private LoginItem[] getLoginItems() {
        LoginItem[] loginItems = new LoginItem[3];
        loginItems[0] = new LoginItem("Facebook", "See all of the photos your Facebook friends post!",
                R.drawable.fb_logo, false);
        loginItems[1] = new LoginItem("Twitter", "View all photos on your Twitter feed.",
                R.drawable.twitter_logo, false);
        loginItems[2] = new LoginItem("Instagram", "Browse your Instagram feed.", R.drawable.ig_logo,
                false);
        return loginItems;
    }


}
