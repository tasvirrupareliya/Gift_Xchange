package com.app.giftxchange.activity;

import static android.content.ContentValues.TAG;
import static com.app.giftxchange.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMainBinding;
import com.app.giftxchange.fragment.ChatFragment;
import com.app.giftxchange.fragment.HomeFragment;
import com.app.giftxchange.fragment.MyListFragment;
import com.app.giftxchange.fragment.ProfileFragment;
import com.app.giftxchange.model.itemCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<itemCard> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Add Gift Card");

                // Inflate a custom layout for the dialog
                DialogAddGiftcardBinding dialogView;
                dialogView = DialogAddGiftcardBinding.inflate(getLayoutInflater());
                builder.setView(dialogView.getRoot());

                // Set positive button to handle the input
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve the values from the EditText fields in the dialog
                        String cardName = dialogView.cardNameInput.getText().toString();
                        String amount = dialogView.amountInput.getText().toString();
                        String brandName = dialogView.brandNameInput.getText().toString();

                        // Validate the input (you can add more validation)
                        if (TextUtils.isEmpty(cardName) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(brandName)) {
                            // Handle validation error
                            Toast.makeText(HomeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            itemCard newItem = new itemCard(cardName, brandName, Double.parseDouble(amount));
                            itemList.add(newItem);
                            cardItemAdapter adapter = new cardItemAdapter(itemList);
                            binding.recyclerview.setAdapter(adapter);
                            binding.recyclerview.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            adapter.notifyDataSetChanged();
                            Toast.makeText(HomeActivity.this, "Gift card item added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(String.valueOf(R.string.share_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(getString(R.string.share_email));
                editor.clear();
                editor.commit();
                finish();
                Toast.makeText(HomeActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
            }
        });*/

        setDefaultFragment();

        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.navigation_home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(binding.contentLayout.getId(), new HomeFragment())
                            .commit();
                    setSelected(R.id.navigation_home);
                }
                if (item.getItemId() == R.id.navigation_chat) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(binding.contentLayout.getId(), new ChatFragment())
                            .commit();
                    setSelected(R.id.navigation_chat);
                }
                if (item.getItemId() == R.id.navigation_mylist) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(binding.contentLayout.getId(), new MyListFragment())
                            .commit();
                    setSelected(R.id.navigation_mylist);
                }
                if (item.getItemId() == R.id.navigation_profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(binding.contentLayout.getId(), new ProfileFragment())
                            .commit();
                    setSelected(R.id.navigation_profile);
                }
                return false;
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToast(MainActivity.this, "Add Button");
            }
        });
    }

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