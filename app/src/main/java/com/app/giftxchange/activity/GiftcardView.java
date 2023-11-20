package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.GiftcardViewBinding;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class GiftcardView extends AppCompatActivity {

    GiftcardViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GiftcardViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String listID = getSharedData(getApplicationContext(), "listID", null);

        openDialogwithGiftcard(listID);
    }

    private void openDialogwithGiftcard(String listID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.c_giftcardlisting))
                .whereEqualTo("listID", listID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //hideProgressDialog(.this);
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String cardAmount = document.getString("cardAmount");
                            String cardCVV = document.getString("cardCVV");
                            String cardExpiryDate = document.getString("cardExpiryDate");
                            String cardName = document.getString("cardName");
                            String cardNumber = document.getString("cardNumber");

                            binding.textCardHolderName.setText(cardName);
                            binding.textCardNumber.setText(cardNumber);
                            binding.textCVVValue.setText(cardCVV);
                            binding.textExpiryDateValue.setText(cardExpiryDate);
                            binding.valueTextView.setText("Value : $" + cardAmount);

                            binding.copyicon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    copyToClipboard("", binding.textCardNumber.getText().toString());
                                }
                            });

                            saveinFirebase(cardAmount, cardCVV, cardNumber, cardName, cardExpiryDate);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        setToast(GiftcardView.this, e.getMessage().toString());
                    }
                });
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        setToast(GiftcardView.this, "Card Number copied to clipboard");
    }

    private void saveinFirebase(String cardAmount, String cardCVV, String cardNumber, String cardName, String cardExpiryDate) {

        String userid = getSharedData(GiftcardView.this, getString(R.string.key_userid), null);
        Utils.showProgressDialog(GiftcardView.this, getString(R.string.please_wait));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a data object with the user's information
        Map<String, Object> giftcardData = new HashMap<>();
        giftcardData.put(getString(R.string.fs_cardAmount), cardAmount);
        giftcardData.put(getString(R.string.fs_cardCVV), cardCVV);
        giftcardData.put(getString(R.string.fs_cardNumber), cardNumber);
        giftcardData.put(getString(R.string.fs_cardname), cardName);
        giftcardData.put(getString(R.string.fs_cardExpiryDate), cardExpiryDate);
        giftcardData.put(getString(R.string.fs_userid), userid);

        // Add the user's contact information to FireStore
        db.collection(getString(R.string.c_mygiftcard))
                .add(giftcardData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Utils.hideProgressDialog(GiftcardView.this);
                        // Data was successfully added to FireStore
                        setToast(GiftcardView.this, "GiftCard Details Saved Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while adding data to FireStore
                        Utils.hideProgressDialog(GiftcardView.this);
                        setToast(GiftcardView.this, "Error: " + e.getMessage());
                    }
                });
    }
}