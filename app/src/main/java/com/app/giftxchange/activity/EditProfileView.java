package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.saveSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.EditProfileViewBinding;
import com.app.giftxchange.model.Listing;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileView extends AppCompatActivity {

    EditProfileViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userEmail = getSharedData(this, getString(R.string.key_email), null);
        String userAge = getSharedData(this, getString(R.string.key_age), null);
        String userName = getSharedData(this, getString(R.string.key_name), null);

        binding.userEmail.setText(userEmail);
        binding.userAge.setText(userAge);
        binding.userName.setText(userName);

        String street = getSharedData(EditProfileView.this, getString(R.string.fulladdress_street), null);
        String city = getSharedData(EditProfileView.this, getString(R.string.fulladdress_city), null);
        String state = getSharedData(EditProfileView.this, getString(R.string.fulladdress_state), null);
        String country = getSharedData(EditProfileView.this, getString(R.string.fulladdress_country), null);
        String postal_code = getSharedData(EditProfileView.this, getString(R.string.fulladdress_postalcode_code), null);

        binding.userPostalcode.setText(postal_code);
        binding.userCity.setText(city);
        binding.userProvince.setText(state);
        binding.userStreetname.setText(street);

        addressUpdate();

        binding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDataInFirestore(binding.userName.getText().toString(), binding.userAge.getText().toString());
                updateAddress(binding.userAptno.getText().toString(), binding.userPostalcode.getText().toString(), binding.userStreetname.getText().toString(), binding.userCity.getText().toString(), binding.userProvince.getText().toString());
            }
        });
    }


    private void updateAddress(String unitNo, String postalCode, String streetName, String city, String province) {
        String userID = getSharedData(this, getString(R.string.key_userid), null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (userID != null) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put(getString(R.string.fs_userid), userID);
            updatedData.put(getString(R.string.fs_postalCode), postalCode);
            updatedData.put(getString(R.string.fs_city), city);
            updatedData.put(getString(R.string.fs_province), province);
            updatedData.put(getString(R.string.fs_streetName), streetName);
            updatedData.put(getString(R.string.fs_unitNo), unitNo);

            db.collection(getString(R.string.c_address))
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

    private void addressUpdate() {
        String userID = getSharedData(this, getString(R.string.key_userid), null);

        Utils.showProgressDialog(this, getString(R.string.please_wait));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (userID != null) {
            db.collection(getString(R.string.c_address))
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            hideProgressDialog(EditProfileView.this);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String city = document.getString("city");
                                String postalCode = document.getString("postalCode");
                                String province = document.getString("province");
                                String streetName = document.getString("streetName");
                                String unitNo = document.getString("unitNo");

                                binding.userPostalcode.setText(postalCode);
                                binding.userCity.setText(city);
                                binding.userProvince.setText(province);
                                binding.userStreetname.setText(streetName);
                                binding.userAptno.setText(unitNo);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            hideProgressDialog(EditProfileView.this);
                            String street = getSharedData(EditProfileView.this, getString(R.string.fulladdress_street), null);
                            String city = getSharedData(EditProfileView.this, getString(R.string.fulladdress_city), null);
                            String state = getSharedData(EditProfileView.this, getString(R.string.fulladdress_state), null);
                            String country = getSharedData(EditProfileView.this, getString(R.string.fulladdress_country), null);
                            String postal_code = getSharedData(EditProfileView.this, getString(R.string.fulladdress_postalcode_code), null);

                            binding.userPostalcode.setText(postal_code);
                            binding.userCity.setText(city);
                            binding.userProvince.setText(state);
                            binding.userStreetname.setText(street);
                        }
                    });
        }
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