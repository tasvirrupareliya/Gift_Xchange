package com.app.giftxchange.fragment;


import static com.app.giftxchange.utils.FireStoreHelper.loadDataMyListFromFirestore;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.giftxchange.ItemClickListenee;
import com.app.giftxchange.R;
import com.app.giftxchange.activity.MyListItemClickViewActivity;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.databinding.FragmentMyListBinding;
import com.app.giftxchange.model.Listing;

import java.util.ArrayList;

public class MyListFragment extends Fragment implements ItemClickListenee {

    FragmentMyListBinding binding;
    public static ArrayList<Listing> list = new ArrayList<>();
    giftcardAdapter adapter;

    public MyListFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentMyListBinding.inflate(inflater, container, false);
        adapter = new giftcardAdapter(list, this);

        binding.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        );

        loadDataMyListFromFirestore(list, adapter, binding.swipeRefreshLayout, getContext());

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataMyListFromFirestore(list, adapter, binding.swipeRefreshLayout, getContext());
            }
        });

        binding.myListRecyclerview.setAdapter(adapter);
        binding.myListRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(Listing item) {
        // Create a new fragment to display the item details.
        Intent intent = new Intent(requireContext(), MyListItemClickViewActivity.class);

        // Pass the item data as extras in the intent.
        intent.putExtra("title", item.getListTitle());
        intent.putExtra("price", item.getListPrice());
        intent.putExtra("location", item.getListLocation());
        intent.putExtra("date", item.getListDate());
        intent.putExtra("listID", item.getListID());
        intent.putExtra("listType", item.getListType());
        intent.putExtra(getString(R.string.fs_cardNumber), item.getCardNumber());
        intent.putExtra(getString(R.string.fs_cardExpiryDate), item.getCardExpiryDate());
        intent.putExtra(getString(R.string.fs_cardCVV), item.getCardCVV());
        // Start the activity.
        startActivity(intent);
    }
}