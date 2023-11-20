package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMyGiftCardListBinding;
import com.app.giftxchange.databinding.ActivityPremiumViewBinding;
import com.app.giftxchange.fragment.CheckoutBottomSheetDialog;
import com.app.giftxchange.fragment.PremiumBottomDialog;

public class PremiumView extends AppCompatActivity {

    ActivityPremiumViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userID = getSharedData(this, getString(R.string.key_userid), null);

        binding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PremiumBottomDialog bottomSheetDialog =
                        PremiumBottomDialog.newInstance(userID);
                bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
            }
        });
    }
}