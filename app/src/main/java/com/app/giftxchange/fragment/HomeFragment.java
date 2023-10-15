package com.app.giftxchange.fragment;

import static com.app.giftxchange.activity.MainActivity.LOCATION_PERMISSION_REQUEST_CODE;
import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.LoginActivity;
import com.app.giftxchange.activity.MainActivity;
import com.app.giftxchange.databinding.DialogAddgiftCardBinding;
import com.app.giftxchange.databinding.FragmentHomeBinding;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    FusedLocationProviderClient fusedLocationClient;
    private final static int REQUEST_CODE = 100;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        binding.viewPager.setAdapter(new MyFragmentStateAdapter(this));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

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
                //showDialog();
                getLastLocation();
            }
        });

        return binding.getRoot();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    setToast(getContext(), addresses.get(0).getAddressLine(0));
                                    Log.e("111", addresses.get(0).getAddressLine(0));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                setToast(getContext(), "Required Permission");
            }
        }
    }

    /* private void getUserLocationAndSave() {


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        // Store the location in SharedPreferences
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_LATITUDE, String.valueOf(location.getLatitude()));
                        editor.putString(KEY_LONGITUDE, String.valueOf(location.getLongitude()));
                        editor.apply();
                    }
                });
    }*/

    /*private void locationEnabled() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }*/

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {

            return true;
        } else if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        // String location = "";//getCurrentLocation(getContext());

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

                            Listing newItem = new Listing(userID, cardName, cardPrice, listDate, "location", tabType, listStatus);

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

}