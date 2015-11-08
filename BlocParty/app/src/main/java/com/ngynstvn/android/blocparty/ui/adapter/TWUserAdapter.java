package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ngynstvn on 10/29/15.
 */

public class TWUserAdapter extends RecyclerView.Adapter<TWUserAdapter.TWUserAdapterViewHolder> {

    private static final String TAG = BPUtils.classTag(TWUserAdapter.class);

    @Override
    public TWUserAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_item, viewGroup, false);
        return new TWUserAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(TWUserAdapterViewHolder holder, int position) {
        User user = BlocpartyApplication.getSharedDataSource().getTwUserArrayList().get(position);
        holder.updateViewHolder(user);
    }

    @Override
    public int getItemCount() {
        return BlocpartyApplication.getSharedDataSource().getTwUserArrayList().size();
    }

    static class TWUserAdapterViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userProfilePic;
        TextView userName;
        CheckBox userSelected;

        User user;

        public TWUserAdapterViewHolder(View itemView) {
            super(itemView);
            userProfilePic = (CircleImageView) itemView.findViewById(R.id.civ_user_profile_pic);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            userSelected = (CheckBox) itemView.findViewById(R.id.cb_user_select);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "User item clicked");
                }
            });
        }

        private void updateViewHolder(final User user) {
            this.user = user;

            if (user.getUserProfilePicUrl() != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user
                        .getUserProfilePicUrl()).into(userProfilePic);
            }

            userSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Log.v(TAG, "Checkbox Checked");
                        BPUtils.putSPrefBooleanValue(BPUtils.newSPrefInstance(BPUtils.CHECKED_STATE),
                                BPUtils.CHECKED_STATE, String.valueOf(user.getUserProfileId()), true);
                    }
                    else {
                        Log.v(TAG, "Checkbox Unchecked");
                        BPUtils.delSPrefValue(BPUtils.newSPrefInstance(BPUtils.CHECKED_STATE),
                                BPUtils.CHECKED_STATE, String.valueOf(user.getUserProfileId()));
                    }
                }
            });

            userName.setText(user.getUserFullName());
        }
    }
}
