package com.ngynstvn.android.blocparty.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.BlocpartyApplication;
import com.ngynstvn.android.blocparty.R;
import com.ngynstvn.android.blocparty.api.model.Collection;
import com.ngynstvn.android.blocparty.api.model.User;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ngynstvn on 10/28/15.
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionAdapterViewHolder> {

    private static final String TAG = "(" + PostItemAdapter.class.getSimpleName() + "): ";

    /**
     *
     * CollectionAdapter's Delegation
     *
     */

    public interface CollectionAdapterDelegate {
        void onItemClicked(CollectionAdapter collectionAdapter, int position);
    }

    private WeakReference<CollectionAdapterDelegate> collectionAdapteraDelegate;

    public void setCollectionAdapterDelegate(CollectionAdapterDelegate collectionAdapteraDelegate) {
        this.collectionAdapteraDelegate = new WeakReference<CollectionAdapterDelegate>(collectionAdapteraDelegate);
    }

    public CollectionAdapterDelegate getCollectionAdapterDelegate() {
        if(collectionAdapteraDelegate == null) {
            return null;
        }

        return collectionAdapteraDelegate.get();
    }

    /**
     *
     * CollectionAdapter's DataSource
     *
     */

    public interface CollectionAdapterDataSource {
        int getCollectionItemCount(CollectionAdapter collectionAdapter);
        Collection getCollection(CollectionAdapter collectionAdapter, int position);
        ArrayList<User> getCollectionUsersList(CollectionAdapter collectionAdapter, int position);
    }

    private WeakReference<CollectionAdapterDataSource> collectionAdapterDataSource;

    public void setCollectionAdapterDataSource(CollectionAdapterDataSource collectionAdapterDataSource) {
        this.collectionAdapterDataSource = new WeakReference<CollectionAdapterDataSource>(collectionAdapterDataSource);
    }

    public CollectionAdapterDataSource getCollectionAdapterDataSource() {

        /**
         * @see com.ngynstvn.android.blocparty.ui.fragment#getCollectionItemSoun
         */

        if(collectionAdapterDataSource == null) {
            return null;
        }

        return collectionAdapterDataSource.get();
    }

    public CollectionAdapter() {
        Log.v(TAG, "CollectionAdapter() called");
    }

    @Override
    public CollectionAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.collection_item,
                viewGroup, false);
        return new CollectionAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(CollectionAdapterViewHolder collectionAdapterViewHolder, int position) {
        if(getCollectionAdapterDataSource() == null) {
            return;
        }

        Collection collection = getCollectionAdapterDataSource().getCollection(this, position);
        collectionAdapterViewHolder.updateViewHolder(collection);
    }

    @Override
    public int getItemCount() {
        if(getCollectionAdapterDataSource() == null) {
            return 0;
        }

        return getCollectionAdapterDataSource().getCollectionItemCount(this);
    }

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
                    Log.v(TAG, "Collection Filter Activated");

                    if (collectionAdapteraDelegate != null) {
                        getCollectionAdapterDelegate().onItemClicked(CollectionAdapter.this, getAdapterPosition());
                    }
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
                    .getDBItemCount(BPUtils.COLLECTION_TABLE, BPUtils.COLLECTION_NAME, collection.getCollectionName());

            collectionUserNum.setText(String.valueOf(count));

            if(count == 1) {
                collectionUserText.setText(BlocpartyApplication.getSharedInstance().getResources()
                        .getString(R.string.user));
            }
            else {
                collectionUserText.setText(BlocpartyApplication.getSharedInstance().getResources()
                        .getString(R.string.users));
            }

            try {
                ArrayList<User> userArrayList = getCollectionAdapterDataSource()
                        .getCollectionUsersList(CollectionAdapter.this, getAdapterPosition());

                if(userArrayList.size() == 1) {
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(0)
                            .getUserProfilePicUrl()).into(topUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                            .into(topUserRightPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                            .into(botUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                            .into(botUserRightPic);
                }
                else if(userArrayList.size() == 2) {
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(0)
                            .getUserProfilePicUrl()).into(topUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(1)
                            .getUserProfilePicUrl()).into(topUserRightPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                            .into(botUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                            .into(botUserRightPic);
                }
                else if(userArrayList.size() == 3) {
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(0)
                            .getUserProfilePicUrl()).into(topUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(1)
                            .getUserProfilePicUrl()).into(topUserRightPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(2)
                            .getUserProfilePicUrl()).into(botUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                            .into(botUserRightPic);
                }
                else {
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(0)
                            .getUserProfilePicUrl()).into(topUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(1)
                            .getUserProfilePicUrl()).into(topUserRightPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(2)
                            .getUserProfilePicUrl()).into(botUserLeftPic);
                    Picasso.with(BlocpartyApplication.getSharedInstance()).load(userArrayList.get(3)
                            .getUserProfilePicUrl()).into(botUserRightPic);
                }
            }
            catch (NullPointerException e) {
                Log.v(TAG, "UserArrayList is null");
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                        .into(topUserLeftPic);
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                        .into(topUserRightPic);
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                        .into(botUserLeftPic);
                Picasso.with(BlocpartyApplication.getSharedInstance()).load(R.drawable.default_photo)
                        .into(botUserRightPic);
            }
        }
    }
}

