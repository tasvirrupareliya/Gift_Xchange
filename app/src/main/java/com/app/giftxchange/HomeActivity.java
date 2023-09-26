package com.app.giftxchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.app.giftxchange.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedPreferences sharedPreference = getSharedPreferences(String.valueOf(R.string.share_key), MODE_PRIVATE);
        String sharusername = sharedPreference.getString(String.valueOf(R.string.share_email), null);
       // username_whologin.setText(sharusername);
    }
}