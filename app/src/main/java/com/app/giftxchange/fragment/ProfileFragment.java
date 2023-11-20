package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.Utils.getSharedData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.AboutActivity;
import com.app.giftxchange.activity.ContactUsView;
import com.app.giftxchange.activity.EditProfileView;
import com.app.giftxchange.activity.LoginActivity;
import com.app.giftxchange.activity.MyGiftCardListActivity;
import com.app.giftxchange.activity.PremiumView;
import com.app.giftxchange.databinding.DialogRatingBinding;
import com.app.giftxchange.databinding.FragmentProfileBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String policyUrl = "https://giftxchange06.blogspot.com/2023/11/privacy-policy.html";

    public ProfileFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        String userName = getSharedData(getContext(), getString(R.string.key_name), null);

        binding.username.setText(userName);

        binding.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), EditProfileView.class));
            }
        });

        binding.contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ContactUsView.class));
            }
        });

        binding.privacypolicyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl));
                startActivity(intent);
            }
        });

        binding.mycardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyGiftCardListActivity.class));
            }
        });

        binding.rateusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogRatingBinding dialogBinding = DialogRatingBinding.inflate(getLayoutInflater());
                Dialog builder = new Dialog(getContext());
                builder.setContentView(dialogBinding.getRoot());
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                dialogBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.cancel();
                    }
                });

                dialogBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.cancel();
                    }
                });

                builder.show();
            }
        });

        binding.aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
        });

        binding.premiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PremiumView.class));
            }
        });

        binding.logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.clear();
                editor.apply();

                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }
}