package com.app.giftxchange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityAboutBinding;
import com.app.giftxchange.databinding.ActivityLoginBinding;

import java.util.List;

public class AboutActivity extends AppCompatActivity {


    ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Us");

        binding.txtversion.setText("Version : " + getVersionName());
        binding.txtdesc.setText(R.string.experience_a_seamless_exchange_of_heartfelt_gifts_making_every_occasion_a_celebration_of_connection);

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });

        binding.contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactUS();
            }
        });

        binding.privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPrivacyPolicy();
            }
        });
    }

    private String getVersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out GiftXchange App - a seamless platform for meaningful gift exchanges! Download it now: https://play.google.com/store/apps/details?id=com.app.giftxchange");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void openContactUS() {
        Intent intent = new Intent(AboutActivity.this, ContactUsView.class);
        startActivity(intent);
    }

    private void openPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policyUrl)));
        startActivity(intent);
    }
}