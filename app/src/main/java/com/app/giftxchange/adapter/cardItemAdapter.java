package com.app.giftxchange.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.R;
import com.app.giftxchange.itemCard;

import java.util.List;

public class cardItemAdapter extends RecyclerView.Adapter<cardItemAdapter.ViewHolder> {
    private List<itemCard> itemList;

    public cardItemAdapter(List<itemCard> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_itemcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        itemCard item = itemList.get(position);
        holder.cardNameTextView.setText(item.getCardName());
        holder.brandNameTextView.setText(item.getBrandName());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardNameTextView;
        TextView brandNameTextView;
        TextView priceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardNameTextView = itemView.findViewById(R.id.card_name_text);
            brandNameTextView = itemView.findViewById(R.id.brand_name_text);
            priceTextView = itemView.findViewById(R.id.price_text);
        }
    }
}
