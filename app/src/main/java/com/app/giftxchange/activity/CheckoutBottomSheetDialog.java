package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.setToast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.CheckoutviewBinding;
import com.app.giftxchange.model.Payment;
import com.app.giftxchange.utils.ExpiryDateInputFilter;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckoutBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String ARG_SUBTOTAL = "subtotal";
    private static final String ARG_TOTAL = "total";
    private static final String ARG_CHARGE = "charge";
    private static final String ARG_CURRID = "currID";
    private static final String ARG_OTHRID = "othrID";
    CheckoutviewBinding binding;

    public static CheckoutBottomSheetDialog newInstance(String subtotal, String currentuserID, String otheruserID) {
        CheckoutBottomSheetDialog fragment = new CheckoutBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_SUBTOTAL, subtotal);
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

            binding.subtotalTextView.setText("Subtotal: " + getArguments().getString(ARG_SUBTOTAL));
            binding.chargeTextView.setText("Charge: $1.50");
            subtotal = subtotal.replace("$", "");

            try {
                double subtotalValue = Double.parseDouble(subtotal);
                double totalValue = subtotalValue + 1.5;
                binding.totalTextView.setText(String.format("Total: " + "$%.2f", totalValue));
                binding.paymentButton.setText(String.format("Payment($%.2f)", totalValue));
                binding.paymentButton.setTextSize(15);
            } catch (NumberFormatException e) {
                // Handle the exception, e.g., show an error message or log it
                binding.totalTextView.setText("Invalid Subtotal");
            }
        }
        binding.paymentButton.setOnClickListener(v -> {
            initiatePayment(getArguments().getString(ARG_CURRID), getArguments().getString(ARG_OTHRID));
        });
    }

    private void initiatePayment(String currentID, String otherID) {

        Utils.showProgressDialog(getActivity(), getString(R.string.please_wait));

        if (!validateFields()) {
            Utils.hideProgressDialog(getActivity());
            return;
        }

        new Handler().postDelayed(() -> {
            Payment newItem = new Payment("", binding.cardNumber.getText().toString(), binding.cardExpirydate.getText().toString(), binding.cardcvv.getText().toString(), binding.nameCard.getText().toString(), currentID, otherID);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(getString(R.string.c_payment))
                    .add(newItem)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String generatedDocumentID = documentReference.getId();
                            newItem.setPaymentID(generatedDocumentID);
                            updateFirestoreDocument(db, generatedDocumentID, newItem);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle error
                            //setToast(getContext(), "Failed to payment");
                        }
                    })
                    .addOnCompleteListener(task -> {
                        // This block will be called when the Firestore operation is complete
                        if (task.isSuccessful()) {
                            Utils.hideProgressDialog(getActivity());
                            binding.checkOutView.setVisibility(View.GONE);
                            binding.lottieContainer.setVisibility(View.VISIBLE);
                            binding.fullScreenLottieAnimation.setAnimation(R.raw.success);
                            binding.fullScreenLottieAnimation.playAnimation();
                            binding.fullScreenLottieAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    openDialogwithGiftcard();
                                    Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        } else {
                            dismiss();
                            // If failed, you may handle it accordingly
                            Toast.makeText(getContext(), "Failed to complete payment", Toast.LENGTH_SHORT).show();
                        }
                    });
        }, 3700);
    }

    private boolean validateFields() {
        String cardName = binding.nameCard.getText().toString();
        String cardNumber = binding.cardNumber.getText().toString();
        String cardCVV = binding.cardcvv.getText().toString();
        String cardExpiry = binding.cardExpirydate.getText().toString();

        if (TextUtils.isEmpty(cardName)) {
            setToast(getContext(), "Please fill in the Card Name");
            return false;
        } else if (TextUtils.isEmpty(cardCVV)) {
            setToast(getContext(), "Please fill in the Card CVV");
            return false;
        } else if (TextUtils.isEmpty(cardExpiry)) {
            setToast(getContext(), "Please fill in the Card Expiry");
            return false;
        } else if (TextUtils.isEmpty(cardNumber)) {
            setToast(getContext(), "Please fill in the Card Number");
            return false;
        }

        return true;
    }

    private void openDialogwithGiftcard() {

    }

    private void updateFirestoreDocument(FirebaseFirestore db, String generatedDocumentID, Payment newItem) {
        newItem.setPaymentID(generatedDocumentID);

        db.collection(getString(R.string.c_payment))
                .document(generatedDocumentID)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // dismiss();
                        //Toast.makeText(getContext(), "Document updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismiss();
                        Toast.makeText(getContext(), "Failed to update document", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
