package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.imageResources;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMainItemClickViewBinding;

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

        binding.tvItemPrice.setText(getSharedData(this, getString(R.string.ll_price), null));
        binding.tvItemTitle.setText(getSharedData(this, getString(R.string.ll_title), null));
        binding.itemLocation.setText(getSharedData(this, getString(R.string.ll_location), null));
        binding.itemUsername.setText(getSharedData(this, getString(R.string.ll_userName), null));
        binding.tvItemdate.setText(formatDate(getSharedData(this, getString(R.string.ll_date), null)));
        binding.itemImageview.setImageResource(getRandomImageResource());
        binding.itemUseremail.setText(formatEmail(getSharedData(this, getString(R.string.ll_userEmail), null)));

        String currentuserID = getSharedData(MainItemClickViewActivity.this, getString(R.string.key_userid), null);
        String otheruserID = getSharedData(MainItemClickViewActivity.this, getString(R.string.ll_userID), null);

        if (currentuserID.equals(getSharedData(this, getString(R.string.ll_userID), null))) {
            binding.btnChat.setVisibility(View.GONE);
            binding.btnPayment.setVisibility(View.GONE);
        }

        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainItemClickViewActivity.this, ChatActivity.class);

                intent.putExtra("status", "User");
                intent.putExtra("userId", currentuserID);
                intent.putExtra("clientId", otheruserID);
                startActivity(intent);
            }
        });

        binding.btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckoutBottomSheetDialog bottomSheetDialog =
                        CheckoutBottomSheetDialog.newInstance(binding.tvItemPrice.getText().toString(), currentuserID, otheruserID);
                bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
                //startActivity(new Intent(MainItemClickViewActivity.this, CheckOutActivity.class));
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