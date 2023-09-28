package com.app.giftxchange;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.giftxchange.databinding.ActivityHomeBinding;
import com.app.giftxchange.databinding.DialogAddGiftcardBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    List<itemCard> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
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
        });
    }
}