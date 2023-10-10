package com.app.giftxchange.activity;

import static android.content.ContentValues.TAG;
import static com.app.giftxchange.Utils.getSharedData;
import static com.app.giftxchange.Utils.saveSharedData;
import static com.app.giftxchange.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.cardItemAdapter;
import com.app.giftxchange.databinding.ActivityMainBinding;
import com.app.giftxchange.fragment.AddFragment;
import com.app.giftxchange.fragment.ChatFragment;
import com.app.giftxchange.fragment.HomeFragment;
import com.app.giftxchange.fragment.MyListFragment;
import com.app.giftxchange.fragment.ProfileFragment;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
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

    public void setSelected(int optionID) {
        binding.navigation.getMenu().findItem(optionID).setChecked(true);
        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putInt("selectedNav", optionID).commit();
    }

    public int getSelectedNav() {
        return getSharedPreferences(getPackageName(), MODE_PRIVATE).getInt("selectedNav", R.id.navigation_home);
    }

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = sharedPreferences.getString(getString(R.string.key_userid), null);

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

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.key_name), userName);
                        editor.putString(getString(R.string.key_password), userPassword);
                        editor.putString(getString(R.string.key_age), userAge);
                        editor.putString(getString(R.string.key_email), userEmail);
                        editor.apply();
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
}