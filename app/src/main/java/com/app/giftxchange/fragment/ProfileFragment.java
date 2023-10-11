package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.Utils.getSharedData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.EditProfileView;
import com.app.giftxchange.databinding.FragmentProfileBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userName = getSharedData(getContext(), getString(R.string.key_name), null);

        binding.username.setText(userName);

        binding.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), EditProfileView.class));
            }
        });
    }
}