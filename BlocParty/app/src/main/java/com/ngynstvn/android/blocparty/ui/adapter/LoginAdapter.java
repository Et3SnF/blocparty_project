package com.ngynstvn.android.blocparty.ui.adapter;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.LoginItem;
import com.ngynstvn.android.blocparty.ui.fragment.LoginFragment;
import com.zcw.togglebutton.ToggleButton;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.LoginAdapterViewHolder> {

    private static final String TAG = "(" + LoginFragment.class.getSimpleName() + "): ";

    private LoginItem[] loginItems;

    public LoginAdapter() {
        Log.v(TAG, "LoginAdapter() called");
    }

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
        return getLoginItems().length;
    }

    /*
     * Interface Material
     */

    public interface LoginAdapterDelegate {
        void onLoginSwitchActivated(LoginAdapter loginAdapter, int adapterPosition, boolean isChecked);
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

        private final String TAG = "(" + LoginAdapterViewHolder.class.getSimpleName() + "): ";

        TextView loginItemName;
        TextView loginItemDescription;
        ImageView loginItemLogo;
        ToggleButton loginSwitch;

        LoginItem loginItem;

        public LoginAdapterViewHolder(View itemView) {
            super(itemView);

            Log.v(TAG, "LoginAdapterViewHolder() called");

            loginItems = getLoginItems();

            loginItemName = (TextView) itemView.findViewById(R.id.tv_login_item_title);
            loginItemDescription = (TextView) itemView.findViewById(R.id.tv_login_item_info);
            loginItemLogo = (ImageView) itemView.findViewById(R.id.iv_login_item_logo);
            loginSwitch = (ToggleButton) itemView.findViewById(R.id.tog_login);

            loginSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    if (getLoginAdapterDelegate() != null) {
                        getLoginAdapterDelegate().onLoginSwitchActivated(LoginAdapter.this, getAdapterPosition(), on);
                    }
                }
            });
        }

        void updateViewHolder(LoginItem loginItem) {
            this.loginItem = loginItem;
            loginItemName.setText(loginItem.getItemName());
            loginItemDescription.setText(loginItem.getItemDescription());
            loginItemLogo.setBackgroundResource(loginItem.getItemLogoPath());

            if(loginItem.isLoggedIn()) {
                loginSwitch.toggleOn();
            }
            else {
                loginSwitch.toggleOff();
            }
        }
    }

    // Method to add any additional social media items

    private LoginItem[] getLoginItems() {

        LoginItem[] loginItems = new LoginItem[3];

        SharedPreferences sharedPreferences = BPUtils.newSPrefInstance(BPUtils.FILE_NAME);

        int fbPosition = sharedPreferences.getInt(BPUtils.FB_POSITION, 0);
        boolean isFBLoggedIn = sharedPreferences.getBoolean(BPUtils.FB_LOGIN, false);

        int twPosition = sharedPreferences.getInt(BPUtils.TW_POSITION, 1);
        boolean isTWLoggedIn = sharedPreferences.getBoolean(BPUtils.TW_LOGIN, false);

        int igPosition = sharedPreferences.getInt(BPUtils.IG_POSITION, 2);
        boolean isIGLoggedIn = sharedPreferences.getBoolean(BPUtils.IG_LOGIN, false);

        loginItems[fbPosition] = new LoginItem("Facebook", "See all of the photos your Facebook friends post!",
                R.drawable.fb_logo, isFBLoggedIn);
        loginItems[twPosition] = new LoginItem("Twitter", "View all photos on your Twitter feed.",
                R.drawable.twitter_logo, isTWLoggedIn);
        loginItems[igPosition] = new LoginItem("Instagram", "Browse your Instagram feed.", R.drawable.ig_logo,
                isIGLoggedIn);
        return loginItems;
    }

}
