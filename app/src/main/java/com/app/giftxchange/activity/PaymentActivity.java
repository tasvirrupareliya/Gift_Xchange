package com.app.giftxchange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityChatBinding;
import com.app.giftxchange.databinding.ActivityMyitemClickViewBinding;
import com.app.giftxchange.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment");



    }
}