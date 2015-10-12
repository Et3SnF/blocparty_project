package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        Log.v(TAG, "LoginAdapter() called");
    }

    @Override
    public PostItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item,
                viewGroup, false);
        return new PostItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(PostItemAdapterViewHolder postItemAdapterViewHolder, int i) {
        PostItem postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList().get(i);
        postItemAdapterViewHolder.updateViewHolder(postItem);
    }

    @Override
    public int getItemCount() {
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

        TextView postFirstName;
//        TextView postLastName;
        ImageView postProfileImage;
        ImageView postImage;
        TextView postImageCaption;
        TextView postPublishDate;

        PostItem postItem;

        public PostItemAdapterViewHolder(View itemView) {
            super(itemView);

            Log.v(TAG, "LoginAdapterViewHolder() called");

            postFirstName = (TextView) itemView.findViewById(R.id.tv_op_name);
            postProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            postImage = (ImageView) itemView.findViewById(R.id.iv_post_image);
            postImageCaption = (TextView) itemView.findViewById(R.id.tv_post_caption);
            postPublishDate = (TextView) itemView.findViewById(R.id.tv_publish_date);
        }

        void updateViewHolder(PostItem postItem) {
            this.postItem = postItem;
            postFirstName.setText(postItem.getOpFirstName());
            postImageCaption.setText(postItem.getPostCaption());
            postPublishDate.setText((int) postItem.getPostPublishDate());
            Picasso.with(BlocpartyApplication.getSharedInstance()).load(postItem.getOpProfilePicUrl()).into(postProfileImage);
            Picasso.with(BlocpartyApplication.getSharedInstance()).load(postItem.getPostImageUrl()).into(postImage);
        }
    }
}
