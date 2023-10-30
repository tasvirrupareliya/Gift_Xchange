package com.app.giftxchange.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.app.giftxchange.databinding.ActivitySplashBinding;
import com.google.firebase.FirebaseApp;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler().postDelayed(() -> {
            NetworkInfo activeNetwork = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

                if (checkLocationPermission()) {
                    // Location permission is granted. You can perform location-related actions here.
                    startLoginActivity();
                } else {
                    // Location permission is not granted, request it.
                    requestLocationPermission();
                }


            } else {
                showErrorDialog("Please check your network connection");
            }
        }, 1200);
    }

    private boolean checkLocationPermission() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    // Request location permission
    private void requestLocationPermission() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showPermissionRationaleDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void showPermissionRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Needed");
        builder.setMessage("This app needs your location permission for feature. Please grant the permission.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            finish();
            // Handle the case when the user denies permission.
            // You may choose to show an error message or exit the app gracefully.
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission is granted. Continue with your location-related actions.
                startLoginActivity();
            } else {
                //requestLocationPermission();
                showPermissionRationaleDialog();
            }
        }
    }
    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Close", (dialog, which) -> finish());
        builder.show();
    }

    private void startLoginActivity() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();

    }
}