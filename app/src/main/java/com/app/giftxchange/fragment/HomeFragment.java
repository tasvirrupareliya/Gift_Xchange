package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.FireStoreHelper.loadDataSellFromFirestore;
import static com.app.giftxchange.utils.Utils.getSharedData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.DialogAddgiftCardBinding;
import com.app.giftxchange.databinding.FragmentHomeBinding;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private boolean isLocationPermissionRequested = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        /*DesignToolbarBinding designToolbarBinding = DesignToolbarBinding.inflate(getLayoutInflater());
        designToolbarBinding.toolbar.setTitle("dvdvd");
        designToolbarBinding.getRoot();*/

        /*binding.swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        );
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Load data when SwipeRefreshLayout is triggered
                loadDataFromFirestore(list, new cardItemAdapter(list), binding.swipeRefreshLayout);
                // Hide the refreshing animation
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });*/

        binding.viewPager.setAdapter(new MyFragmentStateAdapter(this));

        // Connect the TabLayout with the ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Sell");
                    } else if (position == 1) {
                        tab.setText("Exchange");
                    }
                }).attach();

        binding.btnAddlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasLocationPermission()) {
                    showDialog();
                } else {
                    if (isLocationPermissionRequested) {
                        // Explain to the user why location permission is needed
                        Toast.makeText(getContext(), "Location permission is required for this feature.", Toast.LENGTH_SHORT).show();
                    }
                    requestLocationPermissions();
                }
            }
        });

        return binding.getRoot();
    }

    private static class MyFragmentStateAdapter extends FragmentStateAdapter {

        public MyFragmentStateAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Create and return SellFragment for position 0 or ExchangeFragment for position 1
            if (position == 0) {
                return new SellFragment();
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

        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle location updates here
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                // Use latitude and longitude as needed
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    public void showDialog() {

        DialogAddgiftCardBinding dialogbinding = DialogAddgiftCardBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(dialogbinding.getRoot())
                .setTitle("Add Gift Card")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cardName = dialogbinding.cardName.getText().toString();
                        String cardPrice = dialogbinding.listprice.getText().toString();
                        String listDate = getCurrentDate();
                        String location = getCurrentLocation();

                        if (TextUtils.isEmpty(cardName) || TextUtils.isEmpty(cardPrice)) {
                            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            String userID = getSharedData(getContext(), getString(R.string.key_userid), null);

                            String tabType;
                            int currentTabPosition = binding.tabLayout.getSelectedTabPosition();
                            if (currentTabPosition == 0) {
                                tabType = "Sell";
                            } else {
                                tabType = "Exchange";
                            }
                            String listStatus = "Active";

                            Listing newItem = new Listing(userID, cardName, cardPrice, listDate, location, tabType, listStatus);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection(getString(R.string.c_giftcardlisting))
                                    .add(newItem)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Item added successfully
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

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = Calendar.getInstance().getTime();
        return sdf.format(currentDate);
    }

    private boolean hasLocationPermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, show the dialog
                showDialog();
            } else {
                // Location permission denied, set a flag to indicate that it was requested
                isLocationPermissionRequested = true;
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getCurrentLocation() {
        if (hasLocationPermission()) {
            // Retrieve the last known location
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String city = address.getLocality();
                        String street = address.getThoroughfare();
                        String state = address.getAdminArea();
                        String country = address.getCountryName();

                        state = state.substring(0, 2);
                        country = country.substring(0, 2);

                        return city + ", " + state.toUpperCase() + ", " + country.toUpperCase();
                    } else {
                        return "Address not found";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error fetching address";
                }
            } else {
                return "Location not available";
            }
        } else {
            requestLocationPermissions();
            return "Location permission not granted";
        }
    }
}