package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.User;
import com.squareup.picasso.Picasso;

/**
 * Created by Ngynstvn on 10/29/15.
 */

public class FBUserAdapter extends RecyclerView.Adapter<FBUserAdapter.FBUserAdapterViewHolder> {

    private static final String TAG = BPUtils.classTag(FBUserAdapter.class);

    @Override
    public FBUserAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_item, viewGroup, false);
        return new FBUserAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(FBUserAdapterViewHolder holder, int position) {
        User user = BlocpartyApplication.getSharedDataSource().getFbUserArrayList().get(position);
        holder.updateViewHolder(user);
    }

    @Override
    public int getItemCount() {
        return BlocpartyApplication.getSharedDataSource().getFbUserArrayList().size();
    }

    class FBUserAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView userProfilePic;
        TextView userName;
        CheckBox userSelected;

        User user;

        public FBUserAdapterViewHolder(View itemView) {
            super(itemView);
            userProfilePic = (ImageView) itemView.findViewById(R.id.civ_user_profile_pic);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            userSelected = (CheckBox) itemView.findViewById(R.id.cb_user_select);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "User item clicked");
                }
            });
        }

        private void updateViewHolder(User user) {
            this.user = user;

            if (user.getUserProfilePicUrl() != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user
                        .getUserProfilePicUrl()).into(userProfilePic);
            }

            userName.setText(user.getUserFullName());
        }
    }
}
