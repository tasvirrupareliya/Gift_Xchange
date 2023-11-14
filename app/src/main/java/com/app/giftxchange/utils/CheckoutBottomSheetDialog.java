package com.app.giftxchange.utils;

import static com.app.giftxchange.utils.Utils.setToast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.CheckoutviewBinding;
import com.app.giftxchange.model.Listing;
import com.app.giftxchange.model.Payment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class CheckoutBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String ARG_SUBTOTAL = "subtotal";
    private static final String ARG_TOTAL = "total";
    private static final String ARG_CHARGE = "charge";
    private static final String ARG_CURRID = "currID";
    private static final String ARG_OTHRID = "othrID";
    CheckoutviewBinding binding;

    public static CheckoutBottomSheetDialog newInstance(String subtotal, String total, String charge, String currentuserID, String otheruserID) {
        CheckoutBottomSheetDialog fragment = new CheckoutBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_SUBTOTAL, subtotal);
        args.putString(ARG_TOTAL, total);
        args.putString(ARG_CHARGE, charge);
        args.putString(ARG_CURRID, currentuserID);
        args.putString(ARG_OTHRID, otheruserID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = com.app.giftxchange.databinding.CheckoutviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String subtotal = getArguments().getString(ARG_SUBTOTAL);
            String total = getArguments().getString(ARG_TOTAL);
            String charge = getArguments().getString(ARG_CHARGE);

            binding.subtotalTextView.setText(subtotal);
            binding.totalTextView.setText(total);
            binding.chargeTextView.setText(charge);

        }

        binding.paymentButton.setOnClickListener(v -> {
            initiatePayment(getArguments().getString(ARG_CURRID), getArguments().getString(ARG_OTHRID);)
            ;
        });
    }

    private void initiatePayment(String currentID, String otherID) {
        Payment newItem = new Payment("", binding.cardNumber.getText().toString(), binding.cardExpirydate.getText().toString(), binding.cardcvv.getText().toString(), binding.nameCard.getText().toString(), currentID, otherID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.c_giftcardlisting))
                .add(newItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Item added successfully
                        String generatedDocumentID = documentReference.getId();
                        newItem.setPaymentID(generatedDocumentID);
                        updateFirestoreDocument(db, generatedDocumentID, newItem);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error
                        setToast(getContext(), "Failed to add item");
                    }
                });
    }

    private void updateFirestoreDocument(FirebaseFirestore db, String generatedDocumentID, Payment newItem) {
        newItem.setPaymentID(generatedDocumentID);

        db.collection(getString(R.string.c_giftcardlisting))
                .document(generatedDocumentID)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Document updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), "Failed to update document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
