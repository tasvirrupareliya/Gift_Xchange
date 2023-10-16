package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.FireStoreHelper.loadDataSellFromFirestore;
import static com.app.giftxchange.utils.Utils.getSharedData;

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
import android.widget.Toast;

import com.app.giftxchange.ItemClickListenee;
import com.app.giftxchange.R;
import com.app.giftxchange.activity.MainItemClickViewActivity;
import com.app.giftxchange.activity.MyListItemClickViewActivity;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.databinding.FragmentBuyFragmentBinding;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BuyFragment extends Fragment implements ItemClickListenee {
    FragmentBuyFragmentBinding binding;
    public static ArrayList<Listing> list = new ArrayList<>();
    giftcardAdapter adapter;

    public BuyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBuyFragmentBinding.inflate(inflater, container, false);

        adapter = new giftcardAdapter(list, this);

        binding.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        );

        binding.swipeRefreshLayout.setRefreshing(true);
        loadDataSellFromFirestore(list, adapter, binding.swipeRefreshLayout, getString(R.string.buy));

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataSellFromFirestore(list, adapter, binding.swipeRefreshLayout, getString(R.string.buy));
            }
        });

        binding.recyclerviewSell.setAdapter(adapter);
        binding.recyclerviewSell.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    public void onItemClick(Listing item) {
        getIDfromItemList(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getIDfromItemList(Listing list) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("RegisterUser")
                .whereEqualTo("userID", list.getUserID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userID = document.getString("userID");
                            String userEmail = document.getString("userEmail");
                            String userName = document.getString("userName");

                            Intent intent = new Intent(requireContext(), MainItemClickViewActivity.class);

                            intent.putExtra("title", list.getListTitle());
                            intent.putExtra("price", list.getListPrice());
                            intent.putExtra("location", list.getListLocation());
                            intent.putExtra("date", list.getListDate());
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userID", userID);

                            startActivity(intent);

                            binding.swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                        // Handle any errors that occurred during the data retrieval
                        // You can log an error message or show a Toast message here
                    }
                });
    }

    public void filterData(String query) {
        ArrayList<Listing> filteredList = new ArrayList<>();

        for (Listing listing : list) {
            if (listing.getListTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(listing);
            }
        }
        adapter.filterList(filteredList);
    }
}