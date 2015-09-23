package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.LoginItem;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.ViewHolder> {

    private LoginItem[] loginItems = getLoginItems();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.login_item,
                viewGroup, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        LoginItem loginItem = loginItems[i];
        viewHolder.updateViewHolder(loginItem);
    }

    @Override
    public int getItemCount() {
        return loginItems.length;
    }

    private LoginItem[] getLoginItems() {
        LoginItem[] loginItems = new LoginItem[3];
        loginItems[0] = new LoginItem("Facebook", "See all of the photos your Facebook friends post!", R.drawable.fb_logo);
        loginItems[1] = new LoginItem("Twitter", "View all photos on your Twitter feed.", R.drawable.twitter_logo);
        loginItems[2] = new LoginItem("Instagram", "Browse your Instagram feed.", R.drawable.ig_logo);
        return loginItems;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView loginItemName;
        TextView loginItemDescription;
        ImageView loginItemLogo;

        LoginItem loginItem;

        public ViewHolder(View itemView) {
            super(itemView);
            loginItemName = (TextView) itemView.findViewById(R.id.tv_login_item_title);
            loginItemDescription = (TextView) itemView.findViewById(R.id.tv_login_item_info);
            loginItemLogo = (ImageView) itemView.findViewById(R.id.v_login_item_logo);
        }

        void updateViewHolder(LoginItem loginItem) {
            this.loginItem = loginItem;
            loginItemName.setText(loginItem.getItemName());
            loginItemDescription.setText(loginItem.getItemDescription());
            loginItemLogo.setBackgroundResource(loginItem.getItemLogoPath());
        }

    }

}
