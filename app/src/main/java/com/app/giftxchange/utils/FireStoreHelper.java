package com.app.giftxchange.utils;

import static com.app.giftxchange.utils.Utils.getSharedData;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.MainItemClickViewActivity;
import com.app.giftxchange.adapter.MyGiftCardAdapter;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.model.Listing;
import com.app.giftxchange.model.MyGiftCards;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
                            String cardPrice = document.getString("cardAmount");
                            String listDate = document.getString("listDate");
                            String location = document.getString("listLocation");
                            String tabType = document.getString("listType");
                            String listStatus = document.getString("listStatus");
                            String listID = document.getString("listID");
                            String cardNumber = document.getString("cardNumber");
                            String cardCVV = document.getString("cardCVV");
                            String cardExpiry = document.getString("cardExpiryDate");

                            if (type.equals(tabType)) {
                                String formattedPrice = "$" + cardPrice;

                                Listing newItem = new Listing(userID, cardName, formattedPrice, listDate, location, tabType, cardNumber, cardExpiry, cardCVV, listStatus, listID);
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
                            String cardPrice = document.getString("cardAmount");
                            String listDate = document.getString("listDate");
                            String location = document.getString("listLocation");
                            String tabType = document.getString("listType");
                            String listStatus = document.getString("listStatus");
                            String listID = document.getString("listID");
                            String cardNumber = document.getString("cardNumber");
                            String cardCVV = document.getString("cardCVV");
                            String cardExpiry = document.getString("cardExpiryDate");

                            if (type.equals(tabType)) {
                                String formattedPrice = "$" + cardPrice;
                                Listing newItem = new Listing(userID, cardName, formattedPrice, listDate, location, tabType, cardNumber, cardExpiry, cardCVV, listStatus, listID);
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


    public static void loadDataMyGiftcardFromFirestore(final List<MyGiftCards> list, final MyGiftCardAdapter adapter, final SwipeRefreshLayout swipeRefreshLayout) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MyGiftCard")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        list.clear();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String cardNumber = document.getString("cardNumber");
                            String cardCVV = document.getString("cardCVV");
                            String cardExpiry = document.getString("cardExpiryDate");
                            String cardName = document.getString("cardName");
                            String cardAmount = document.getString("cardAmount");

                            MyGiftCards newItem = new MyGiftCards(cardAmount, cardCVV, cardNumber, cardName, cardExpiry);
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
                            String cardPrice = document.getString("cardAmount");
                            String listDate = document.getString("listDate");
                            String location = document.getString("listLocation");
                            String tabType = document.getString("listType");
                            String listStatus = document.getString("listStatus");
                            String listID = document.getString("listID");
                            String cardNumber = document.getString("cardNumber");
                            String cardCVV = document.getString("cardCVV");
                            String cardExpiry = document.getString("cardExpiryDate");

                            String formattedPrice = "$" + cardPrice;
                            Listing newItem = new Listing(userID, cardName, formattedPrice, listDate, location, tabType, cardNumber, cardExpiry, cardCVV, listStatus, listID);
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


    public static void fetchUsernameFromFire(String userID, TextView userNameText, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection(context.getString(R.string.c_registeruser)); // Replace "users" with your Firestore collection name

        Query query = usersCollection.whereEqualTo("userID", userID); // Replace "userID" with the actual field name in your Firestore
        String fetchedUsername;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Assuming there's only one user with the provided userID
                        DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                        String username = userDocument.getString("userName");

                        if (username != null) {
                            // You've got the username for the given userID
                            // Use the username as needed
                            String fetchedUsername = username;
                            userNameText.setText(fetchedUsername);
                        } else {
                            // Handle the case where the username is not found
                        }
                    } else {
                        // Handle the case where no user document matches the provided userID
                    }
                } else {
                    // Handle errors when querying Firestore
                }
            }
        });
    }
}
