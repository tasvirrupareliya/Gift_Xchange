package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityMyGiftCardListBinding;
import com.app.giftxchange.databinding.ActivityPremiumViewBinding;
import com.app.giftxchange.fragment.CheckoutBottomSheetDialog;
import com.app.giftxchange.fragment.PremiumBottomDialog;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PremiumView extends AppCompatActivity {

    ActivityPremiumViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String currentID = getSharedData(PremiumView.this, getString(R.string.key_userid),null);

        fetchpremiumCheck(currentID);
    }

    private void fetchpremiumCheck(String currentuserID) {
        Utils.showProgressDialog(PremiumView.this, getString(R.string.please_wait));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.c_premium))
                .whereEqualTo("userID", currentuserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideProgressDialog(PremiumView.this);

                        if (!queryDocumentSnapshots.isEmpty()) {
                            // User with current userID found in Premium collection
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String premiumID = document.getId();
                                binding.purchased.setVisibility(View.VISIBLE);
                                binding.buyNow.setVisibility(View.GONE);

                                Log.e("PremiumCheck", "PremiumID: " + premiumID);
                            }

                        } else {
                            binding.purchased.setVisibility(View.GONE);
                            binding.buyNow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PremiumBottomDialog bottomSheetDialog =
                                            PremiumBottomDialog.newInstance(currentuserID);
                                    bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
                                }
                            });
                            // User with current userID not found in Premium collection
                            Log.e("PremiumCheck", "User is not a premium user");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        hideProgressDialog(PremiumView.this);
                        Log.e("PremiumCheck", "Error: " + e.getMessage());
                    }
                });
    }
}