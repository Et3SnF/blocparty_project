package com.ngynstvn.android.blocparty.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.Collection;
import com.ngynstvn.android.blocparty.api.model.User;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * Created by Ngynstvn on 10/28/15.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionAdapterViewHolder> {

    private static final String TAG = "(" + PostItemAdapter.class.getSimpleName() + "): ";

    public CollectionAdapter() {
        Log.v(TAG, "CollectionAdapter() called");
    }

    @Override
    public CollectionAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        Log.v(TAG, "CollectionAdapterViewHolder() called");
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.collection_item,
                viewGroup, false);
        return new CollectionAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(CollectionAdapterViewHolder collectionAdapterViewHolder, int i) {
//        Log.v(TAG, "onBindViewHolder() called");
        Collection collection = BlocpartyApplication.getSharedDataSource().getCollectionUserArrayList().get(i);
        collectionAdapterViewHolder.updateViewHolder(collection);
    }

    @Override
    public int getItemCount() {
//        Log.v(TAG, "getItemCount() called");
        return BlocpartyApplication.getSharedDataSource().getCollectionUserArrayList().size();
    }

    /*
     * Interface Material
     */

    public interface CollectionAdapteraDelegate {

    }

    private WeakReference<CollectionAdapteraDelegate> collectionAdapteraDelegate;

    public void setCollectionAdapteraDelegate(CollectionAdapteraDelegate collectionAdapteraDelegate) {
        this.collectionAdapteraDelegate = new WeakReference<CollectionAdapteraDelegate>(collectionAdapteraDelegate);
    }

    public CollectionAdapteraDelegate getCollectionAdapterDelegate() {

        if(collectionAdapteraDelegate == null) {
            return null;
        }

        return collectionAdapteraDelegate.get();
    }

    // --------------------- //

    // ----- Inner Class ----- //

    class CollectionAdapterViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = "(" + CollectionAdapterViewHolder.class.getSimpleName() + "): ";

        TextView collectionName;
        TextView collectionUserNum;
        TextView collectionUserText;

        ImageView topUserLeftPic;
        ImageView topUserRightPic;
        ImageView botUserLeftPic;
        ImageView botUserRightPic;

        Collection collection;

        public CollectionAdapterViewHolder(View itemView) {
            super(itemView);
            Log.v(TAG, "CollectionAdapterViewHolder() called");

            collectionName = (TextView) itemView.findViewById(R.id.tv_collection_name);
            collectionUserNum = (TextView) itemView.findViewById(R.id.tv_collection_user_num_value);
            collectionUserText = (TextView) itemView.findViewById(R.id.tv_collection_users_text);
            topUserLeftPic = (ImageView) itemView.findViewById(R.id.iv_top_left_pic);
            topUserRightPic = (ImageView) itemView.findViewById(R.id.iv_top_right_pic);
            botUserLeftPic = (ImageView) itemView.findViewById(R.id.iv_bot_left_pic);
            botUserRightPic = (ImageView) itemView.findViewById(R.id.iv_bot_right_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Collection Item Clicked");
                }
            });
        }

        void updateViewHolder(Collection collection) {
            this.collection = collection;
            collectionName.setText(collection.getCollectionName());
            collectionUserNum.setText(String.valueOf(collection.getUserList().size()));

            User user1 = collection.getUserList().get(0);
            User user2 = collection.getUserList().get(1);
            User user3 = collection.getUserList().get(2);
            User user4 = collection.getUserList().get(3);

            if(user1 != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user1.getUserProfilePicUrl())
                        .into(topUserLeftPic);
            }
            else {
                topUserLeftPic.setBackgroundColor(Color.TRANSPARENT);
            }

            if(user2 != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user2.getUserProfilePicUrl())
                        .into(topUserRightPic);
            }
            else {
                topUserRightPic.setBackgroundColor(Color.TRANSPARENT);
            }

            if(user3 != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user3.getUserProfilePicUrl())
                        .into(botUserLeftPic);
            }
            else {
                botUserLeftPic.setBackgroundColor(Color.TRANSPARENT);
            }

            if(user4 != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user4.getUserProfilePicUrl())
                        .into(botUserRightPic);
            }
            else {
                botUserRightPic.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}

