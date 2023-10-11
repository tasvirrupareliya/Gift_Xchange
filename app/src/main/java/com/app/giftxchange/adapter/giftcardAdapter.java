package com.app.giftxchange.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.databinding.LayoutItemcardBinding;
import com.app.giftxchange.model.Listing;

import java.util.List;

public class giftcardAdapter extends RecyclerView.Adapter<giftcardAdapter.ViewHolder> {
    private List<Listing> itemList;

    public giftcardAdapter(List<Listing> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemcardBinding binding = LayoutItemcardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing item = itemList.get(position);

        holder.binding.lCardName.setText(item.getListTitle());
        holder.binding.lPriceText.setText(String.valueOf(item.getListPrice()));
        holder.binding.lDate.setText(item.getListDate());
        holder.binding.lLocation.setText(item.getListLocation());
        holder.binding.listStatus.setText(item.getListStatus());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LayoutItemcardBinding binding; // Use the View Binding object

        public ViewHolder(LayoutItemcardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
