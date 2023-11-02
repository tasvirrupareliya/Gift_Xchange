package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.saveSharedData;
import static com.app.giftxchange.utils.Utils.setToast;
import static com.app.giftxchange.utils.Utils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.app.giftxchange.utils.FetchAddressIntentServices;
import com.app.giftxchange.R;
import com.app.giftxchange.activity.LoginActivity;
import com.app.giftxchange.databinding.DialogAddgiftCardBinding;
import com.app.giftxchange.databinding.FragmentHomeBinding;
import com.app.giftxchange.model.Listing;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        binding.viewPager.setAdapter(new MyFragmentStateAdapter(this));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText(getString(R.string.buy));
                    } else if (position == 1) {
                        tab.setText(getString(R.string.exchange));
                    }
                }).attach();

        binding.btnAddlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery(newText);
                return false;
            }
        });
    }

    private void searchQuery(String query) {
        Fragment fragment = getChildFragmentManager().getFragments().get(binding.viewPager.getCurrentItem());

        if (fragment instanceof BuyFragment && binding.viewPager.getCurrentItem() == 0) {
            ((BuyFragment) fragment).filterData(query);
        } else if (fragment instanceof ExchangeFragment && binding.viewPager.getCurrentItem() == 1) {
            ((ExchangeFragment) fragment).filterData(query);
        }
    }

    private static class MyFragmentStateAdapter extends FragmentStateAdapter {

        public MyFragmentStateAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new BuyFragment();
            } else {
                return new ExchangeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Number of tabs
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void showDialog() {
        DialogAddgiftCardBinding dialogbinding = DialogAddgiftCardBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(dialogbinding.getRoot())
                .setTitle("Add Gift Card")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String cardPrice = dialogbinding.listprice.getText().toString();
                        String cardName = dialogbinding.cardName.getText().toString();
                        String cardNumber = dialogbinding.listgiftcardNumber.getText().toString();
                        String cardCVV = dialogbinding.listcardCVV.getText().toString();
                        String cardexpiryCard = dialogbinding.listexpirydate.getText().toString();
                        String listDate = getCurrentDate();

                        if (TextUtils.isEmpty(cardName)) {
                            setToast(getContext(), "Please fill in the CardName");
                        } else if (TextUtils.isEmpty(cardPrice)) {
                            setToast(getContext(), "Please fill in the Amount");
                        } else if (TextUtils.isEmpty(cardNumber)) {
                            setToast(getContext(), "Please fill in the Card Number");
                        } else if (TextUtils.isEmpty(cardCVV)) {
                            setToast(getContext(), "Please fill in the Card CVV");
                        } else if (TextUtils.isEmpty(cardexpiryCard)) {
                            setToast(getContext(), "Please fill in the Card Expiry");
                        } else {
                            String userID = getSharedData(getContext(), getString(R.string.key_userid), null);

                            String tabType;
                            int currentTabPosition = binding.tabLayout.getSelectedTabPosition();
                            if (currentTabPosition == 0) {
                                tabType = getString(R.string.buy);
                            } else {
                                tabType = getString(R.string.exchange);
                            }
                            String listStatus = "Active";

                            String location = getSharedData(getContext(), getString(R.string.addressforGiftCard), null);
                            Listing newItem = new Listing(userID, cardName, cardPrice, listDate, location, tabType, cardNumber, cardexpiryCard, cardCVV, listStatus, "");

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection(getString(R.string.c_giftcardlisting))
                                    .add(newItem)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Item added successfully
                                            String generatedDocumentID = documentReference.getId();
                                            newItem.setListID(generatedDocumentID);
                                            updateFirestoreDocument(db, generatedDocumentID, newItem);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle error
                                            setToast(getContext(), "Failed to add item");
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void updateFirestoreDocument(FirebaseFirestore db, String documentId, Listing newItem) {
        newItem.setListID(documentId);

        db.collection(getString(R.string.c_giftcardlisting))
                .document(documentId)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Document updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), "Failed to update document", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = Calendar.getInstance().getTime();
        return sdf.format(currentDate);
    }
}