package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.FireStoreHelper.loadDataExchangeFromFirestore;
import static com.app.giftxchange.utils.Utils.saveSharedData;

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
import com.app.giftxchange.activity.MainItemClickViewActivity;
import com.app.giftxchange.activity.MyListItemClickViewActivity;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.databinding.FragmentExchangeFragmentBinding;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExchangeFragment extends Fragment implements ItemClickListenee {

    FragmentExchangeFragmentBinding binding;
    public static ArrayList<Listing> list = new ArrayList<>();
    giftcardAdapter adapter;

    public ExchangeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExchangeFragmentBinding.inflate(inflater, container, false);

        adapter = new giftcardAdapter(list, this);

        binding.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        );

        binding.swipeRefreshLayout.setRefreshing(true);
        loadDataExchangeFromFirestore(list, adapter, binding.swipeRefreshLayout, getString(R.string.exchange));

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataExchangeFromFirestore(list, adapter, binding.swipeRefreshLayout, getString(R.string.exchange));
            }
        });

        binding.recyclerviewExchage.setAdapter(adapter);
        binding.recyclerviewExchage.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(Listing item) {
        getIDfromItemList(item);
    }

    private void getIDfromItemList(Listing list) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.c_registeruser))
                .whereEqualTo("userID", list.getUserID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userID = document.getString("userID");
                            String userEmail = document.getString("userEmail");
                            String userName = document.getString("userName");

                            saveSharedData(getActivity(), getString(R.string.ll_title), list.getListTitle());
                            saveSharedData(getActivity(), getString(R.string.ll_price), list.getListPrice());
                            saveSharedData(getActivity(), getString(R.string.ll_location), list.getListLocation());
                            saveSharedData(getActivity(), getString(R.string.ll_date), list.getListDate());
                            saveSharedData(getActivity(), getString(R.string.ll_userEmail), userEmail);
                            saveSharedData(getActivity(), getString(R.string.ll_userName), userName);
                            saveSharedData(getActivity(), getString(R.string.ll_userID), userID);
                            saveSharedData(getActivity(), getString(R.string.ll_listID), list.getListID());

                            Intent intent = new Intent(requireContext(), MainItemClickViewActivity.class);
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