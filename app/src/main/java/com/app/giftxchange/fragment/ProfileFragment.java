package com.app.giftxchange.fragment;

import static android.content.ContentValues.TAG;
import static com.app.giftxchange.Utils.getSharedData;
import static com.app.giftxchange.Utils.saveSharedData;
import static com.app.giftxchange.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.EditProfileView;
import com.app.giftxchange.activity.LoginActivity;
import com.app.giftxchange.databinding.FragmentMyListBinding;
import com.app.giftxchange.databinding.FragmentProfileBinding;
import com.app.giftxchange.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

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