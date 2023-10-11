package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityEditProfileViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileView extends AppCompatActivity {

    ActivityEditProfileViewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String userEmail = getSharedData(this, getString(R.string.key_email), null);
        String userAge = getSharedData(this, getString(R.string.key_age), null);
        String userName = getSharedData(this, getString(R.string.key_name), null);

        binding.userEmail.setText(userEmail);
        binding.userAge.setText(userAge);
        binding.userName.setText(userName);

        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDataInFirestore(binding.userName.getText().toString(), binding.userAge.getText().toString());
                addressUpdate();
            }
        });
    }

    private void addressUpdate() {
    }

    private void updateUserDataInFirestore(String updatedName, String age) {
        String userID = getSharedData(this, getString(R.string.key_userid), null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (userID != null) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put(getString(R.string.fs_name), updatedName);
            updatedData.put(getString(R.string.fs_age), age);

            db.collection(getString(R.string.c_registeruser))
                    .document(userID)
                    .set(updatedData, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data updated successfully
                            setToast(EditProfileView.this, "Profile updated successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure
                            setToast(EditProfileView.this, "Failed to update profile: " + e.getMessage());
                        }
                    });
        }
    }
}