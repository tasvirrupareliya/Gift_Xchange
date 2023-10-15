package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.imageResources;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.adapter.giftcardAdapter;
import com.app.giftxchange.databinding.ActivityMyitemClickViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MyListItemClickViewActivity extends AppCompatActivity {

    ActivityMyitemClickViewBinding binding;
    giftcardAdapter adapter;
    int itemPosition;

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
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (adapter != null) {
                    adapter.removeItem(itemPosition);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    CollectionReference collectionReference = db.collection("GiftCardListing");
                    collectionReference
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        // Retrieve the document ID
                                        String documentId = document.getId();

                                        setToast(MyListItemClickViewActivity.this,documentId);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });


                    String documentId = String.valueOf(adapter.itemList.get(itemPosition));

                    setToast(MyListItemClickViewActivity.this, documentId);

                    // Delete the document from Firestore
                  /*  FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("your_collection_name")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document successfully deleted.
                                    adapter.removeItem(itemPosition);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error.
                                }
                            });*/
                }
                //finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

}