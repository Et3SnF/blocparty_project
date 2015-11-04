package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.Collection;
import com.ngynstvn.android.blocparty.api.model.User;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

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
        Collection collection = BlocpartyApplication.getSharedDataSource().getCollectionArrayList().get(i);
        collectionAdapterViewHolder.updateViewHolder(collection);
    }

    @Override
    public int getItemCount() {
//        Log.v(TAG, "getItemCount() called");
        return BlocpartyApplication.getSharedDataSource().getCollectionArrayList().size();
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

        CircleImageView topUserLeftPic;
        CircleImageView topUserRightPic;
        CircleImageView botUserLeftPic;
        CircleImageView botUserRightPic;

        Collection collection;

        public CollectionAdapterViewHolder(View itemView) {
            super(itemView);
            Log.v(TAG, "CollectionAdapterViewHolder() called");

            collectionName = (TextView) itemView.findViewById(R.id.tv_collection_name);
            collectionUserNum = (TextView) itemView.findViewById(R.id.tv_collection_user_num_value);
            collectionUserText = (TextView) itemView.findViewById(R.id.tv_collection_users_text);
            topUserLeftPic = (CircleImageView) itemView.findViewById(R.id.iv_top_left_pic);
            topUserRightPic = (CircleImageView) itemView.findViewById(R.id.iv_top_right_pic);
            botUserLeftPic = (CircleImageView) itemView.findViewById(R.id.iv_bot_left_pic);
            botUserRightPic = (CircleImageView) itemView.findViewById(R.id.iv_bot_right_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Collection Item Clicked");
                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "Long press to modify",
                            Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.v(TAG, "Collection Item Long Clicked");
                    Toast.makeText(BlocpartyApplication.getSharedInstance(), "I have been long pressed",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        void updateViewHolder(Collection collection) {
            this.collection = collection;
            collectionName.setText(collection.getCollectionName());

            int count = BlocpartyApplication.getSharedDataSource()
                    .getCollectionItemCount("collection_name", collection.getCollectionName());

            collectionUserNum.setText(String.valueOf(count));

            if(count == 1) {
                collectionUserText.setText(BlocpartyApplication.getSharedInstance().getResources()
                        .getString(R.string.user));
            }
            else {
                collectionUserText.setText(BlocpartyApplication.getSharedInstance().getResources()
                        .getString(R.string.users));
            }

            User user1 = null;
            User user2 = null;
            User user3 = null;
            User user4 = null;

            if(user1.getUserProfilePicUrl() != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user1.getUserProfilePicUrl()).into(topUserLeftPic);
            }
            else {
                topUserLeftPic.setBackgroundColor(BlocpartyApplication.getSharedInstance()
                        .getResources().getColor(android.R.color.white));
            }

            if(user2.getUserProfilePicUrl() != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user2.getUserProfilePicUrl()).into(topUserRightPic);
            }
            else {
                topUserRightPic.setBackgroundColor(BlocpartyApplication.getSharedInstance()
                        .getResources().getColor(android.R.color.white));
            }

            if(user3.getUserProfilePicUrl() != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user3.getUserProfilePicUrl()).into(botUserLeftPic);
            }
            else {
                botUserLeftPic.setBackgroundColor(BlocpartyApplication.getSharedInstance()
                        .getResources().getColor(android.R.color.white));
            }

            if(user4.getUserProfilePicUrl() != null) {
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(user4.getUserProfilePicUrl()).into(botUserRightPic);
            }
            else {
                botUserRightPic.setBackgroundColor(BlocpartyApplication.getSharedInstance()
                        .getResources().getColor(android.R.color.white));
            }
        }
    }
}

