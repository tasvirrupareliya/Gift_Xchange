package com.app.giftxchange.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.app.giftxchange.utils.FireStoreHelper.loadDataMyGiftcardFromFirestore;
import static com.app.giftxchange.utils.FireStoreHelper.loadDataSellFromFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.MyGiftCardAdapter;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.databinding.ActivityMyGiftCardListBinding;
import com.app.giftxchange.model.Listing;
import com.app.giftxchange.model.MyGiftCards;

import java.util.ArrayList;

public class MyGiftCardListActivity extends AppCompatActivity {

    ActivityMyGiftCardListBinding binding;
    public static ArrayList<MyGiftCards> list = new ArrayList<>();
    MyGiftCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyGiftCardListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your GiftCards");

        adapter = new MyGiftCardAdapter(list);

        loadDataMyGiftcardFromFirestore(list, adapter, binding.swipeRefreshLayout);

        binding.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(MyGiftCardListActivity.this, R.color.colorPrimary)
        );

        binding.swipeRefreshLayout.setRefreshing(true);
        loadDataMyGiftcardFromFirestore(list, adapter, binding.swipeRefreshLayout);

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataMyGiftcardFromFirestore(list, adapter, binding.swipeRefreshLayout);
            }
        });

        binding.myListRecyclerview.setAdapter(adapter);
        binding.myListRecyclerview.setLayoutManager(new LinearLayoutManager(MyGiftCardListActivity.this));
        adapter.notifyDataSetChanged();
    }
}