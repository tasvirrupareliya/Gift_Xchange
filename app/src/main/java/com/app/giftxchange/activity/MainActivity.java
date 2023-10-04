package com.app.giftxchange.activity;

import static com.app.giftxchange.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMainBinding;
import com.app.giftxchange.fragment.AddFragment;
import com.app.giftxchange.fragment.ChatFragment;
import com.app.giftxchange.fragment.HomeFragment;
import com.app.giftxchange.fragment.MyListFragment;
import com.app.giftxchange.fragment.ProfileFragment;
import com.app.giftxchange.itemCard;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
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
              /*  if (item.getItemId() == R.id.navigation_plus) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(binding.contentLayout.getId(), new AddFragment())
                            .commit();
                }*/
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
                setToast(MainActivity.this,"Add Button");
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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}