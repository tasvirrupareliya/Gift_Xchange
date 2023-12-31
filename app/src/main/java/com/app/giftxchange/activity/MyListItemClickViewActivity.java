package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.imageResources;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMyitemClickViewBinding;
import com.app.giftxchange.databinding.DialogAddgiftCardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MyListItemClickViewActivity extends AppCompatActivity {

    ActivityMyitemClickViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyitemClickViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        String listID = intent.getStringExtra("listID");
        String listType = intent.getStringExtra("listType");
        String cardNumber = intent.getStringExtra(getString(R.string.fs_cardNumber));
        String cardCVV = intent.getStringExtra(getString(R.string.fs_cardCVV));
        String cardExpiry = intent.getStringExtra(getString(R.string.fs_cardExpiryDate));

        String username = getSharedData(MyListItemClickViewActivity.this, getString(R.string.key_name), null);
        String userEmail = getSharedData(MyListItemClickViewActivity.this, getString(R.string.key_email), null);

        binding.tvItemPrice.setText(price);
        binding.tvItemTitle.setText(title);
        binding.itemLocation.setText(location);
        binding.itemUsername.setText(username);
        binding.tvItemdate.setText(formatDate(date));
        binding.itemImageview.setImageResource(getRandomImageResource());
        binding.itemUseremail.setText(formatEmail(userEmail));

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(listID);
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(listID, price, title, listType, cardNumber, cardCVV, cardExpiry);
            }
        });
    }

    private void showEditDialog(String listID, String price, String title, String listType, String cardNumber, String cardCVV, String cardExpiry) {

        DialogAddgiftCardBinding dialogbinding = DialogAddgiftCardBinding.inflate(LayoutInflater.from(MyListItemClickViewActivity.this));
        Dialog builder = new Dialog(MyListItemClickViewActivity.this);
        builder.setContentView(dialogbinding.getRoot());
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        price = price.replace("$", "");

        dialogbinding.listprice.setText(price);
        dialogbinding.cardName.setText(title);
        dialogbinding.spinnerListType.setVisibility(View.VISIBLE);
        dialogbinding.listcardCVV.setText(cardCVV);
        dialogbinding.listgiftcardNumber.setText(cardNumber);
        dialogbinding.listexpirydate.setText(cardExpiry);

        String[] listTypes = {getString(R.string.buy), getString(R.string.exchange)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogbinding.spinnerListType.setAdapter(adapter);

        int index = -1;
        for (int i = 0; i < listTypes.length; i++) {
            if (listTypes[i].equals(listType)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            dialogbinding.spinnerListType.setSelection(index);
        }

        dialogbinding.btnAdd.setText(getString(R.string.edit));

        dialogbinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                editGiftCardFirebase(listID, dialogbinding.listprice.getText().toString(), dialogbinding.cardName.getText().toString(), dialogbinding.spinnerListType.getSelectedItem().toString(), dialogbinding.listgiftcardNumber.getText().toString(), dialogbinding.listcardCVV.getText().toString(), dialogbinding.listexpirydate.getText().toString());
            }
        });

        dialogbinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.cancel();
            }
        });
        builder.show();
    }

    private void editGiftCardFirebase(String listID, String price, String title, String listType, String cardnumber, String cardCVV, String expiryDate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (listID != null) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put(getString(R.string.fs_listname), title);
            updatedData.put(getString(R.string.fs_cardAmount), price);
            updatedData.put(getString(R.string.fs_listType), listType);
            updatedData.put(getString(R.string.fs_listDate), getCurrentDate());
            updatedData.put(getString(R.string.fs_cardNumber), cardnumber);
            updatedData.put(getString(R.string.fs_cardCVV), cardCVV);
            updatedData.put(getString(R.string.fs_cardExpiryDate), expiryDate);

            db.collection(getString(R.string.c_giftcardlisting))
                    .document(listID)
                    .set(updatedData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            binding.tvItemTitle.setText(title);
                            binding.tvItemPrice.setText(price);
                            binding.tvItemdate.setText(getCurrentDate());

                            setToast(MyListItemClickViewActivity.this, "GiftCard updated successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure
                            setToast(MyListItemClickViewActivity.this, "Failed to update GiftCard: " + e.getMessage());
                        }
                    });
        }
    }

    private void showDeleteConfirmationDialog(String listID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteListItem(listID);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteListItem(String listID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.c_giftcardlisting)).document(listID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private String formatEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex > 0) {
            int hiddenCharsCount = Math.min(atIndex - 2, 3);
            StringBuilder hiddenPart = new StringBuilder();
            for (int i = 0; i < hiddenCharsCount; i++) {
                hiddenPart.append("*");
            }
            String visiblePart = email.substring(hiddenCharsCount, atIndex);
            String domainPart = email.substring(atIndex);
            return hiddenPart + visiblePart + domainPart;
        }
        return email; // Return the original email if it doesn't match the expected format
    }

    private int getRandomImageResource() {
        // Use a random number generator to select a random image resource
        Random random = new Random();
        int randomIndex = random.nextInt(imageResources.length);
        return imageResources[randomIndex];
    }

    private String formatDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        try {
            Date parsedDate = inputFormat.parse(inputDate);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // Return the original date if parsing fails
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = Calendar.getInstance().getTime();
        return sdf.format(currentDate);
    }

    private boolean isValidExpiryDate(String expiryDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
            Date enteredDate = sdf.parse(expiryDate);
            Date currentDate = Calendar.getInstance().getTime();

            return enteredDate != null && enteredDate.after(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}