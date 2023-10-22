package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivitySplashBinding;
import com.app.giftxchange.databinding.ContactUsViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ContactUsView extends AppCompatActivity {

    ContactUsViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ContactUsViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.userName.getText().toString().equals("") || binding.userEmail.getText().toString().equals("") || binding.userMessage.getText().toString().equals("")) {
                    setToast(ContactUsView.this, "Please Enter Valid Data or Something is Missing");
                } else if (!isValidEmail(binding.userEmail.getText().toString())) {
                    setToast(ContactUsView.this, "Please Enter Valid Email");
                } else {
                    updateUserContactInFirestore(binding.userName.getText().toString(), binding.userEmail.getText().toString(), binding.userMessage.getText().toString());
                }
            }
        });
    }

    private void updateUserContactInFirestore(String userName, String userEmail, String userMessage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a data object with the user's information
        Map<String, Object> userContactData = new HashMap<>();
        userContactData.put(getString(R.string.fs_userName), userName);
        userContactData.put(getString(R.string.fs_userEmail), userEmail);
        userContactData.put(getString(R.string.fs_userMessage), userMessage);

        // Add the user's contact information to Firestore
        db.collection(getString(R.string.c_contactus))
                .add(userContactData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Data was successfully added to Firestore
                        binding.userName.setText("");
                        binding.userEmail.setText("");
                        binding.userMessage.setText("");
                        setToast(ContactUsView.this, "Contact information saved to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while adding data to Firestore
                        setToast(ContactUsView.this, "Error: " + e.getMessage());
                    }
                });
    }

    private boolean isValidEmail(String email) {
        // Define a regular expression pattern for a strong password
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}