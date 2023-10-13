package com.app.giftxchange.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.app.giftxchange.databinding.SpinnerItemviewBinding;
import com.app.giftxchange.model.cardItem;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<cardItem> cardItems;
    private List<cardItem> filteredCardItems;
    private cardItem selectedItem; // Add this field


    public CustomSpinnerAdapter(Context context, List<cardItem> cardItems) {
        this.context = context;
        this.cardItems = cardItems;
        this.filteredCardItems = new ArrayList<>(cardItems);
    }

    @Override
    public int getCount() {
        return filteredCardItems.size();
    }

    public cardItem getSelectedItem() {
        return selectedItem;
    }

    @Override
    public cardItem getItem(int position) {
        return filteredCardItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            SpinnerItemviewBinding binding = SpinnerItemviewBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        }

        SpinnerItemviewBinding binding = (SpinnerItemviewBinding) convertView.getTag();
        cardItem item = filteredCardItems.get(position);

        binding.spinnerImageView.setImageResource(item.getLogoResId());
        binding.title.setText(item.getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<cardItem> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(cardItems);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (cardItem item : cardItems) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCardItems.clear();
                filteredCardItems.addAll((List<cardItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
