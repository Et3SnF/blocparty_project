package com.ngynstvn.android.blocparty.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.PostItem;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

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
//        Log.v(TAG, "CollectionAdapterViewHolder() called");
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item,
                viewGroup, false);
        return new PostItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(PostItemAdapterViewHolder postItemAdapterViewHolder, int i) {
//        Log.v(TAG, "onBindViewHolder() called");
        PostItem postItem = BlocpartyApplication.getSharedDataSource().getPostItemArrayList().get(i);
        postItemAdapterViewHolder.updateViewHolder(postItem);
    }

    @Override
    public int getItemCount() {
//        Log.v(TAG, "getItemCount() called");
//        Log.v(TAG, "Array Size in PostItemAdapter: " + BlocpartyApplication.getSharedDataSource()
//                .getPostItemArrayList().size());
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
        CircleImageView postProfileImage;
        ImageView postImage;
        TextView postImageCaption;
        TextView postPublishDate;
        CheckBox postLikedBtn;
        CheckBox postMoreSettings;
        TextView postMediaType;
        LinearLayout postCaptionArea;

        PostItem postItem;

        public PostItemAdapterViewHolder(View itemView) {
            super(itemView);

//            Log.v(TAG, "CollectionAdapterViewHolder() called");

            postOPName = (TextView) itemView.findViewById(R.id.tv_op_name);
            postProfileImage = (CircleImageView) itemView.findViewById(R.id.civ_profile_pic);
            postImage = (ImageView) itemView.findViewById(R.id.iv_post_image);
            postMediaType = (TextView) itemView.findViewById(R.id.tv_social_media_type);
            postCaptionArea = (LinearLayout) itemView.findViewById(R.id.ll_caption_area);
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
            postOPName.setText(postItem.getOpFullName());
            postImageCaption.setText(postItem.getPostCaption());
            postPublishDate.setText(BPUtils.dateConverter(postItem.getPostPublishDate()));

            if(postItem.getPostCaption().length() == 0) {
                postCaptionArea.setVisibility(View.GONE);
            }
            else {
                postCaptionArea.setVisibility(View.VISIBLE);
            }

            if(postItem.getOpProfilePicUrl().length() != 0) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(postItem.getOpProfilePicUrl()).into(postProfileImage);
            }

            if(postItem.getPostImageUrl().length() != 0) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(postItem.getPostImageUrl()).into(postImage);
            }

            // For media text at bottom left corner

            if(postItem.getPostImageUrl().contains("https://scontent.cdninstagram.com/hphotos")) {
                postMediaType.setText(R.string.instagram_text);
                postMediaType.setTextColor(Color.parseColor("#FF663300"));
            }
            else if(postItem.getPostImageUrl().contains("http://pbs.twimg.com")) {
                postMediaType.setText(R.string.twitter_text);
                postMediaType.setTextColor(Color.parseColor("#FF55ACEE"));
            }
            else if(postItem.getPostImageUrl().contains("fbcdn.net")) {
                postMediaType.setText(R.string.facebook_text);
                postMediaType.setTextColor(Color.parseColor("#FF3B5998"));
            }
        }
    }
}
