package com.app.giftxchange.activity;

import static android.content.ContentValues.TAG;
import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

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
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
    private final int REQUEST_LOCATION_PERMISSION = 1;

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

   /* public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case 0:
                replaceFragment(new ProfileFragment());
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/


    private void setDefaultFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.contentLayout.getId(), new HomeFragment())
                .commit();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_layout, fragment)
                .addToBackStack(null)
                .commit();
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
    public void onBackPressed() {

        ExitViewBinding dialogbinding = ExitViewBinding.inflate(LayoutInflater.from(MainActivity.this));
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogbinding.getRoot());

        AlertDialog exitDialog = builder.create();
        dialogbinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

        dialogbinding.btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exitDialog.show();
    }
}