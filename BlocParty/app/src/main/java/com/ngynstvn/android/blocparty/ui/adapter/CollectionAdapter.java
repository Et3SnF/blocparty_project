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
import com.ngynstvn.android.blocparty.api.model.Collection;

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
            collectionUserNum.setText(String.valueOf(0));
        }
    }
}

