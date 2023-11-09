package com.app.giftxchange.activity;

import static android.content.ContentValues.TAG;
import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

import static com.app.giftxchange.utils.Utils.clearSession;
import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.saveSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMainBinding;
import com.app.giftxchange.databinding.DialogAddgiftCardBinding;
import com.app.giftxchange.databinding.ExitViewBinding;
import com.app.giftxchange.fragment.ChatFragment;
import com.app.giftxchange.fragment.HomeFragment;
import com.app.giftxchange.fragment.MyListFragment;
import com.app.giftxchange.fragment.ProfileFragment;
import com.app.giftxchange.utils.FetchAddressIntentServices;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ResultReceiver resultReceiver;
    private boolean isHomeFragmentVisible = true; // Track if the home fragment is visible
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        setDefaultFragment();
        resultReceiver = new AddressResultReceiver(new Handler());

        getCurrentLocation();

        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.navigation_home) {
                    setTitle("Dashboard");
                    replaceFragment(new HomeFragment());
                }
                if (item.getItemId() == R.id.navigation_chat) {
                    setTitle("Chat");
                    replaceFragment(new ChatFragment());
                }
                if (item.getItemId() == R.id.navigation_mylist) {
                    setTitle("My List");
                    replaceFragment(new MyListFragment());
                }
                if (item.getItemId() == R.id.navigation_profile) {
                    setTitle("Profile");
                    replaceFragment(new ProfileFragment());
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setDefaultFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.contentLayout.getId(), new HomeFragment())
                .commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItem logoutMenuItem = menu.findItem(R.id.action_logout);

        if (isHomeFragmentVisible) {
            // Show the menu items in the home fragment
            searchMenuItem.setVisible(true);
            logoutMenuItem.setVisible(true);
        } else {
            // Hide the menu items in other fragments
            searchMenuItem.setVisible(false);
            logoutMenuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_layout, fragment)
                .addToBackStack(null)
                .commit();

        if (fragment instanceof HomeFragment) {
            isHomeFragmentVisible = true;
        } else {
            isHomeFragmentVisible = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        handler.removeCallbacks(runnable);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setuserData_SharedPreference();

            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    public void setuserData_SharedPreference() {

        String userID = getSharedData(this, getString(R.string.key_userid), null);

        DocumentReference userDocRef = db.collection(getString(R.string.c_registeruser)).document(userID);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString(getString(R.string.fs_name));
                        String userEmail = document.getString(getString(R.string.fs_email));
                        String userPassword = document.getString(getString(R.string.fs_password));
                        String userAge = document.getString(getString(R.string.fs_age));

                        saveSharedData(MainActivity.this, getString(R.string.key_name), userName);
                        saveSharedData(MainActivity.this, getString(R.string.key_password), userPassword);
                        saveSharedData(MainActivity.this, getString(R.string.key_age), userAge);
                        saveSharedData(MainActivity.this, getString(R.string.key_email), userEmail);
                    } else {
                        // The document doesn't exist.
                        Log.d(TAG, "No such document");
                    }
                } else {
                    // An error occurred while fetching the document.
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearSession();
    }

    @Override
    public void onBackPressed() {
        ExitViewBinding dialogbinding = ExitViewBinding.inflate(LayoutInflater.from(MainActivity.this));
        Dialog builder = new Dialog(MainActivity.this);
        builder.setContentView(dialogbinding.getRoot());
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        dialogbinding.btnNotnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        dialogbinding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                clearSession();
            }
        });
        builder.show();
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Utils.SUCCESS_RESULT) {

               /* address.setText(resultData.getString(Utils.ADDRESS));
                locaity.setText(resultData.getString(Utils.LOCAITY));
                state.setText(resultData.getString(Utils.STATE));
                district.setText(resultData.getString(Utils.DISTRICT));
                country.setText(resultData.getString(Utils.COUNTRY));
                postcode.setText(resultData.getString(Utils.POST_CODE));*/

                String city = resultData.getString(Utils.LOCAITY);
                String street = resultData.getString(Utils.ADDRESS);
                String state = resultData.getString(Utils.STATE);
                String country = resultData.getString(Utils.COUNTRY);
                String postalCode = resultData.getString(Utils.POST_CODE);

                saveSharedData(MainActivity.this, getString(R.string.fulladdress_street), street);
                saveSharedData(MainActivity.this, getString(R.string.fulladdress_city), city);
                saveSharedData(MainActivity.this, getString(R.string.fulladdress_state), state);
                saveSharedData(MainActivity.this, getString(R.string.fulladdress_country), country);
                saveSharedData(MainActivity.this, getString(R.string.fulladdress_postalcode_code), postalCode);

                state = state.substring(0, 2);
                country = country.substring(0, 2);

                String address = city + ", " + state.toUpperCase() + ", " + country.toUpperCase();
                saveSharedData(MainActivity.this, getString(R.string.addressforGiftCard), address);

            } else {
                Toast.makeText(MainActivity.this, resultData.getString(Utils.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
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

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
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
        Intent intent = new Intent(MainActivity.this, FetchAddressIntentServices.class);
        intent.putExtra(Utils.RECEVIER, resultReceiver);
        intent.putExtra(Utils.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }
}