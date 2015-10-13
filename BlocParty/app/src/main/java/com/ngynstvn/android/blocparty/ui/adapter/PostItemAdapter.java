package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class PostItemAdapter extends RecyclerView.Adapter<PostItemAdapter.PostItemAdapterViewHolder> {

    private static final String TAG = "(" + PostItemAdapter.class.getSimpleName() + "): ";

    public PostItemAdapter() {
        Log.v(TAG, "PostItemAdapter() called");
    }

    @Override
    public PostItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.v(TAG, "PostItemAdapterViewHolder() called");
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item,
                viewGroup, false);
        return new PostItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(PostItemAdapterViewHolder postItemAdapterViewHolder, int i) {
        Log.v(TAG, "onBindViewHolder() called");
        PostItem postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList().get(i);
        postItemAdapterViewHolder.updateViewHolder(postItem);
    }

    @Override
    public int getItemCount() {
        Log.v(TAG, "getItemCount() called");
        Log.v(TAG, "Array Size in PostItemAdapter: " + BlocpartyApplication.getSharedDataSource()
                .getPostItemArrayList().size());
        return BlocpartyApplication.getSharedDataSource().getPostItemArrayList().size();
    }

    /*
     * Interface Material
     */

    public interface PostItemAdapterDelegate {
        void onLoginSwitchActivated(PostItemAdapter postItemAdapter, int adapterPosition);
    }

    private WeakReference<PostItemAdapterDelegate> postItemAdapterDelegate;

    public void setPostItemAdapterDelegate(PostItemAdapterDelegate postItemAdapterDelegate) {
        this.postItemAdapterDelegate = new WeakReference<PostItemAdapterDelegate>(postItemAdapterDelegate);
    }

    public PostItemAdapterDelegate getLoginAdapterDelegate() {

        if(postItemAdapterDelegate == null) {
            return null;
        }

        return postItemAdapterDelegate.get();
    }

    // --------------------- //

    // ----- Inner Class ----- //

    class PostItemAdapterViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "(" + PostItemAdapterViewHolder.class.getSimpleName() + "): ";

        TextView postOPName;
        ImageView postProfileImage;
        ImageView postImage;
        TextView postImageCaption;
        TextView postPublishDate;
        CheckBox postLikedBtn;
        CheckBox postMoreSettings;

        PostItem postItem;

        public PostItemAdapterViewHolder(View itemView) {
            super(itemView);

            Log.v(TAG, "LoginAdapterViewHolder() called");

            postOPName = (TextView) itemView.findViewById(R.id.tv_op_name);
            postProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            postImage = (ImageView) itemView.findViewById(R.id.iv_post_image);
            postImageCaption = (TextView) itemView.findViewById(R.id.tv_post_caption);
            postPublishDate = (TextView) itemView.findViewById(R.id.tv_publish_date);
            postLikedBtn = (CheckBox) itemView.findViewById(R.id.btn_like_button);
            postMoreSettings = (CheckBox) itemView.findViewById(R.id.btn_more_settings);

            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Post Image clicked");
                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "Image Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            postLikedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "Liked Button Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            postMoreSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "More Settings Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        void updateViewHolder(PostItem postItem) {
            this.postItem = postItem;
            postOPName.setText(postItem.getOpFirstName());
            postImageCaption.setText(postItem.getPostCaption());
            postPublishDate.setText(String.format("%d", postItem.getPostPublishDate()));

            if(postItem.getOpProfilePicUrl().length() != 0) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(postItem.getOpProfilePicUrl()).into(postProfileImage);
            }

            if(postItem.getPostImageUrl().length() != 0) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(postItem.getPostImageUrl()).into(postImage);
            }
        }
    }
}
