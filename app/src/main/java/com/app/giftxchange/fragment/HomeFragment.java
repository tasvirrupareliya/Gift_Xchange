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
        /*if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            return true;
        }*/
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
                        String listDate = getCurrentDate();

                        if (TextUtils.isEmpty(cardName)) {
                            Toast.makeText(getContext(), "Please fill in the CardName", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(cardPrice)) {
                            Toast.makeText(getContext(), "Please fill in the Amount", Toast.LENGTH_SHORT).show();
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
                            Listing newItem = new Listing(userID, cardName, cardPrice, listDate, location, tabType, listStatus, "");

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
                                            Toast.makeText(getContext(), "Item added Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle error
                                            Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
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

    /*private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Utils.SUCCESS_RESULT) {

               *//* address.setText(resultData.getString(Utils.ADDRESS));
                locaity.setText(resultData.getString(Utils.LOCAITY));
                state.setText(resultData.getString(Utils.STATE));
                district.setText(resultData.getString(Utils.DISTRICT));
                country.setText(resultData.getString(Utils.COUNTRY));
                postcode.setText(resultData.getString(Utils.POST_CODE));*//*

                String city = resultData.getString(Utils.LOCAITY);
                String street = resultData.getString(Utils.ADDRESS);
                String state = resultData.getString(Utils.STATE);
                String country = resultData.getString(Utils.COUNTRY);
                String postalCode = resultData.getString(Utils.POST_CODE);

                saveSharedData(getContext(), getString(R.string.fulladdress_street), street);
                saveSharedData(getContext(), getString(R.string.fulladdress_city), city);
                saveSharedData(getContext(), getString(R.string.fulladdress_state), state);
                saveSharedData(getContext(), getString(R.string.fulladdress_country), country);
                saveSharedData(getContext(), getString(R.string.fulladdress_postalcode_code), postalCode);

                state = state.substring(0, 2);
                country = country.substring(0, 2);

                String address = city + ", " + state.toUpperCase() + ", " + country.toUpperCase();
                saveSharedData(getContext(), getString(R.string.addressforGiftCard), address);

                //return city + ", " + state.toUpperCase() + ", " + country.toUpperCase();
            } else {
                Toast.makeText(getContext(), resultData.getString(Utils.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            //hideProgressDialog(getActivity());
        }
    }


    private void getCurrentLocation() {
        //showProgressDialog(getActivity(), "Retrieve Current Location");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(getContext())
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            //isLocationObtained = true;
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();

                            Location location = new Location("providerNA");
                            location.setLongitude(longi);
                            location.setLatitude(lati);
                            fetchaddressfromlocation(location);

                        } else {
                            //hideProgressDialog(getActivity());
                        }
                    }
                }, Looper.getMainLooper());

    }

    private void fetchaddressfromlocation(Location location) {
        Intent intent = new Intent(getContext(), FetchAddressIntentServices.class);
        intent.putExtra(Utils.RECEVIER, resultReceiver);
        intent.putExtra(Utils.LOCATION_DATA_EXTRA, location);
        getContext().startService(intent);
    }*/
}