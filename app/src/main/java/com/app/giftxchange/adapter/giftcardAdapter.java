package com.app.giftxchange.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.giftxchange.ItemClickListenee;
import com.app.giftxchange.R;
import com.app.giftxchange.databinding.LayoutItemcardBinding;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class giftcardAdapter extends RecyclerView.Adapter<giftcardAdapter.ViewHolder> {
    public List<Listing> itemList;

    private int[] imageResources = {
            R.drawable.g1,
            R.drawable.g2,
            R.drawable.g3,
            R.drawable.g4,
            R.drawable.g5,
            R.drawable.g6,
            R.drawable.g7
    };
    private ItemClickListenee itemClickListener;

    public giftcardAdapter(List<Listing> itemList, ItemClickListenee itemClickListener) {
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
    }

    public giftcardAdapter() {

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
        holder.binding.lPriceText.setText(item.getListPrice());
        holder.binding.lDate.setText(item.getListDate());
        holder.binding.lLocation.setText(item.getListLocation());
        holder.binding.listStatus.setText(item.getListStatus());
        // holder.binding.imageview.setImageResource(item.getDrawable());


        int randomImageResource = getRandomImageResource();
        holder.binding.imageview.setImageResource(randomImageResource);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(item);
            }
        });
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

    private int getRandomImageResource() {
        // Use a random number generator to select a random image resource
        Random random = new Random();
        int randomIndex = random.nextInt(imageResources.length);
        return imageResources[randomIndex];
    }

    public void filterList(ArrayList<Listing> filteredList) {
        itemList = filteredList;
        notifyDataSetChanged();
    }
}
