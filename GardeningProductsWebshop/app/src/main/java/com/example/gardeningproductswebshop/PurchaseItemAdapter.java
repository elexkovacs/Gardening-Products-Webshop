package com.example.gardeningproductswebshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PurchaseItemAdapter extends RecyclerView.Adapter<PurchaseItemAdapter.ViewHolder> {
    // Member variables.
    public ArrayList<ShoppingItem> mPurchaseItemData;

    private Context mContext;
    private int lastPosition = -1;

    PurchaseItemAdapter(Context context, ArrayList<ShoppingItem> itemsData) {
        this.mPurchaseItemData = itemsData;
        this.mContext = context;
    }

    @Override
    public PurchaseItemAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_purchase, parent, false));
    }

    @Override
    public void onBindViewHolder(PurchaseItemAdapter.ViewHolder holder, int position) {
        // Get current sport.
        ShoppingItem currentItem = mPurchaseItemData.get(position);
        //System.out.println(currentItem.getName());
        // Populate the textviews with data.
        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.back_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }

    }

    @Override
    public int getItemCount() {
        return mPurchaseItemData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.itemTitlePurchase);
            mInfoText = itemView.findViewById(R.id.subTitlePurchase);
            mItemImage = itemView.findViewById(R.id.itemImagePurchase);
            mRatingBar = itemView.findViewById(R.id.ratingBarPurchase);
            mPriceText = itemView.findViewById(R.id.pricePurchase);

        }

        void bindTo(ShoppingItem currentItem){
            System.out.println(currentItem.getName());
            mTitleText.setText(currentItem.getName());
            System.out.println(currentItem.getInfo());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mRatingBar.setRating(currentItem.getRatedInfo());

            // Load the images into the ImageView using the Glide library.
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            //itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((ShoppingActivity)mContext).updateAlertIcon(currentItem));
            itemView.findViewById(R.id.deletePurchase).setOnClickListener(view -> ((ShoppingActivity)mContext).deleteItem(currentItem));
        }
    }
}

