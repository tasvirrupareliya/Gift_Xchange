package com.app.giftxchange.utils;

import static com.app.giftxchange.utils.Utils.getSharedData;

import android.content.Context;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FireStoreHelper {
    public static void loadDataSellFromFirestore(final List<Listing> list, final giftcardAdapter adapter, final SwipeRefreshLayout swipeRefreshLayout, final String type) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("GiftCardListing")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        list.clear();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            String userID = document.getString("userID");
                            String cardName = document.getString("listTitle");
                            String cardPrice = document.getString("listPrice");
                            String listDate = document.getString("listDate");
                            String location = document.getString("listLocation");
                            String tabType = document.getString("listType");
                            String listStatus = document.getString("listStatus");

                            if (type.equals(tabType)) {
                                String formattedPrice = "$" + cardPrice;
                                Listing newItem = new Listing(userID, cardName, formattedPrice, listDate, location, tabType, listStatus);
                                list.add(newItem);
                            }
                            swipeRefreshLayout.setRefreshing(false);

                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public static void loadDataExchangeFromFirestore(final List<Listing> list, final giftcardAdapter adapter, final SwipeRefreshLayout swipeRefreshLayout, final String type) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("GiftCardListing")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        list.clear();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userID = document.getString("userID");
                            String cardName = document.getString("listTitle");
                            String cardPrice = document.getString("listPrice");
                            String listDate = document.getString("listDate");
                            String location = document.getString("listLocation");
                            String tabType = document.getString("listType");
                            String listStatus = document.getString("listStatus");

                            if (type.equals(tabType)) {
                                String formattedPrice = "$" + cardPrice;
                                Listing newItem = new Listing(userID, cardName, formattedPrice, listDate, location, tabType, listStatus);
                                list.add(newItem);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public static void loadDataMyListFromFirestore(final List<Listing> list, final giftcardAdapter adapter, final SwipeRefreshLayout swipeRefreshLayout, Context context) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid = getSharedData(context, context.getString(R.string.key_userid), null);

        db.collection("GiftCardListing")
                .whereEqualTo("userID", userid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        list.clear();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userID = document.getString("userID");
                            String cardName = document.getString("listTitle");
                            String cardPrice = document.getString("listPrice");
                            String listDate = document.getString("listDate");
                            String location = document.getString("listLocation");
                            String tabType = document.getString("listType");
                            String listStatus = document.getString("listStatus");

                            String formattedPrice = "$" + cardPrice;
                            Listing newItem = new Listing(userID, cardName, formattedPrice, listDate, location, tabType, listStatus);
                            list.add(newItem);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                        // Handle any errors that occurred during the data retrieval
                        // You can log an error message or show a Toast message here
                    }
                });
    }
}
