package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.imageResources;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMainItemClickViewBinding;
import com.app.giftxchange.model.Listing;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainItemClickViewActivity extends AppCompatActivity {

    ActivityMainItemClickViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainItemClickViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        String userEmail = intent.getStringExtra("userEmail");
        String userName = intent.getStringExtra("userName");
        String userID = intent.getStringExtra("userID");

        binding.tvItemPrice.setText(price);
        binding.tvItemTitle.setText(title);
        binding.itemLocation.setText(location);
        binding.itemUsername.setText(userName);
        binding.tvItemdate.setText(formatDate(date));
        binding.itemImageview.setImageResource(getRandomImageResource());
        binding.itemUseremail.setText(formatEmail(userEmail));

        String currentuserID = getSharedData(MainItemClickViewActivity.this, getString(R.string.key_userid), null);

        if (currentuserID.equals(userID)) {
            binding.btnChat.setVisibility(View.GONE);
            binding.btnPayment.setVisibility(View.GONE);
        }
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
        return email;
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