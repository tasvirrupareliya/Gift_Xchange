package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.FireStoreHelper.loadDataExchangeFromFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.databinding.FragmentExchangeFragmentBinding;
import com.app.giftxchange.model.Listing;

import java.util.ArrayList;

public class ExchangeFragment extends Fragment {

    FragmentExchangeFragmentBinding binding;
    public static ArrayList<Listing> list = new ArrayList<>();
    giftcardAdapter adapter;

    public ExchangeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExchangeFragmentBinding.inflate(inflater, container, false);

        adapter = new giftcardAdapter(list);

        binding.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        );

        binding.swipeRefreshLayout.setRefreshing(true);
        loadDataExchangeFromFirestore(list, adapter, binding.swipeRefreshLayout, "Exchange");

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataExchangeFromFirestore(list, adapter, binding.swipeRefreshLayout,"Exchange");
            }
        });


        binding.recyclerviewExchage.setAdapter(adapter);
        binding.recyclerviewExchage.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}