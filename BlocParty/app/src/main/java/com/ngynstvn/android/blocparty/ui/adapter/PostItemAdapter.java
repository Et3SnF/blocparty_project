package com.ngynstvn.android.blocparty.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    /*
     * Interface Material
     */

    /* This is for decoupling the DataSource from the adapter. */

    public interface DataSource {
        PostItem getPostItem(PostItemAdapter postItemAdapter, int position);
        int getItemCount(PostItemAdapter postItemAdapter);
    }

    private WeakReference<DataSource> dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = new WeakReference<DataSource>(dataSource);
    }

    public DataSource getDataSource() {

        /**
         *
         * Delegated to MainActivity.java
         * @see com.ngynstvn.android.blocparty.ui.activity.MainActivity#getPostItem(PostItemAdapter, int)
         * @see com.ngynstvn.android.blocparty.ui.activity.MainActivity#getItemCount(PostItemAdapter)
         *
         */

        if(dataSource == null) {
            return null;
        }

        return dataSource.get();
    }

    public interface PostItemAdapterDelegate {
        void onPostItemImagePanZoomed(PostItemAdapter postItemAdapter, int adapterPosition);
        void onPostItemImageDownloaded(PostItemAdapter postItemAdapter, int adapterPosition);
        void onPostItemLiked(PostItemAdapter postItemAdapter, int adapterPosition, boolean isLiked);
    }

    private WeakReference<PostItemAdapterDelegate> postItemAdapterDelegate;

    public void setPostItemAdapterDelegate(PostItemAdapterDelegate postItemAdapterDelegate) {
        this.postItemAdapterDelegate = new WeakReference<PostItemAdapterDelegate>(postItemAdapterDelegate);
    }

    public PostItemAdapterDelegate getPostItemAdapterDelegate() {

        /**
         * Delegated to MainActivity.java
         * @see com.ngynstvn.android.blocparty.ui.activity.MainActivity#onPostItemImageDownloaded(PostItemAdapter, int)
         * @see com.ngynstvn.android.blocparty.ui.activity.MainActivity#onPostItemLiked(PostItemAdapter, int, boolean)
         * @see com.ngynstvn.android.blocparty.ui.activity.MainActivity#onPostItemImagePanZoomed(PostItemAdapter, int)
         */

        if(postItemAdapterDelegate == null) {
            return null;
        }

        return postItemAdapterDelegate.get();
    }

    // --------------------- //

    @Override
    public PostItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        Log.v(TAG, "CollectionAdapterViewHolder() called");
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item,
                viewGroup, false);
        return new PostItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(PostItemAdapterViewHolder postItemAdapterViewHolder, int i) {
        if(getDataSource() == null) {
            return;
        }

        PostItem postItem = getDataSource().getPostItem(this, i);
        postItemAdapterViewHolder.updateViewHolder(postItem);
    }

    @Override
    public int getItemCount() {
        if(getDataSource() == null) {
            return 0;
        }

        return getDataSource().getItemCount(this);
    }

    // ----- Inner Class ----- //

    class PostItemAdapterViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "(" + PostItemAdapterViewHolder.class.getSimpleName() + "): ";

        TextView postOPName;
        CircleImageView postProfileImage;
        ImageView postImage;
        TextView postImageCaption;
        TextView postPublishDate;
        CheckBox postLikedBtn;
        CheckBox postImgDownload;
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
            postImgDownload = (CheckBox) itemView.findViewById(R.id.btn_download_post_image);

            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Post Image clicked");
                    if (postItemAdapterDelegate.get() != null) {
                        getPostItemAdapterDelegate().onPostItemImagePanZoomed(PostItemAdapter.this, getAdapterPosition());
                    }
                }
            });

            postImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (postItemAdapterDelegate.get() != null) {
                        getPostItemAdapterDelegate().onPostItemImageDownloaded(PostItemAdapter.this, getAdapterPosition());
                    }
                    return true;
                }
            });

            postLikedBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(postItemAdapterDelegate.get() != null) {
                        getPostItemAdapterDelegate().onPostItemLiked(PostItemAdapter.this, getAdapterPosition(), isChecked);
                    }
                }
            });

            postImgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(postItemAdapterDelegate.get() != null) {
                        getPostItemAdapterDelegate().onPostItemImageDownloaded(PostItemAdapter.this, getAdapterPosition());
                    }
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

            postLikedBtn.setChecked(postItem.isLiked());

            // For media text at bottom left corner

            if(postItem.getPostImageUrl().contains(BPUtils.IG_IMG_BASE_URL)) {
                postMediaType.setText(R.string.instagram_text);
                postMediaType.setTextColor(Color.parseColor("#FF663300"));
            }
            else if(postItem.getPostImageUrl().contains(BPUtils.TW_IMG_BASE_URL)) {
                postMediaType.setText(R.string.twitter_text);
                postMediaType.setTextColor(Color.parseColor("#FF55ACEE"));
            }
            else if(postItem.getPostImageUrl().contains(BPUtils.FB_IMG_BASE_URL)) {
                postMediaType.setText(R.string.facebook_text);
                postMediaType.setTextColor(Color.parseColor("#FF3B5998"));
            }
        }
    }
}
