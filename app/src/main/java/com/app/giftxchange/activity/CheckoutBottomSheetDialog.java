package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.setToast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.CheckoutviewBinding;
import com.app.giftxchange.model.Payment;
import com.app.giftxchange.utils.ExpiryDateInputFilter;
import com.app.giftxchange.utils.Utils;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CheckoutBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String ARG_SUBTOTAL = "subtotal";
    private static final String ARG_CURRID = "currID";
    private static final String ARG_OTHRID = "othrID";
    private static final String ARG_ListID = "ListID";
    CheckoutviewBinding binding;

    public static CheckoutBottomSheetDialog newInstance(String subtotal, String currentuserID, String otheruserID, String listID) {
        CheckoutBottomSheetDialog fragment = new CheckoutBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_SUBTOTAL, subtotal);
        args.putString(ARG_CURRID, currentuserID);
        args.putString(ARG_OTHRID, otheruserID);
        args.putString(ARG_ListID, listID);
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

        binding.cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .actionLabel("Purchase")
                .setup(getActivity());


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

        if (binding.cardForm.isValid()) {
            new Handler().postDelayed(() -> {

                String cardexpirtdate = binding.cardForm.getExpirationMonth() + "/" + binding.cardForm.getExpirationYear();

                Payment newItem = new Payment("", binding.cardForm.getCardNumber(), cardexpirtdate, binding.cardForm.getCvv(), binding.cardForm.getCardholderName(), currentID, otherID);

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
                                dismiss();
                                //setToast(getContext(), "Failed to payment");
                            }
                        });
            }, 3700);
        } else {
            Utils.hideProgressDialog(getActivity());
            binding.cardForm.validate();
        }
    }

    private void openDialogwithGiftcard() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.c_giftcardlisting))
                .whereEqualTo("listID", getArguments().getString(ARG_ListID))
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


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        setToast(getActivity(), e.getMessage().toString());
                    }
                });
    }

    private void updateListStatus(String listID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.c_giftcardlisting))
                .whereEqualTo("listID", listID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Get the document ID
                            String documentId = document.getId();

                            // Update the 'listStatus' field to "Sold"
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("listStatus", "Sold");

                            db.collection(getString(R.string.c_giftcardlisting))
                                    .document(documentId)
                                    .update(updateData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Utils.hideProgressDialog(getActivity());
                                            // Handle the success, e.g., show a message
                                            Log.d("Firestore", "ListStatus updated to Sold");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Utils.hideProgressDialog(getActivity());
                                            // Handle the failure
                                            setToast(getActivity(), "Failed to update ListStatus: " + e.getMessage());
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
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
                                                        Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                                                        dismiss();
                                                        openDialogwithGiftcard();
                                                    }
                                                });
                                            } else {
                                                dismiss();
                                                // If failed, you may handle it accordingly
                                                Toast.makeText(getContext(), "Failed to complete payment", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Utils.hideProgressDialog(getActivity());
                        setToast(getActivity(), e.getMessage().toString());
                    }
                });
    }


    private void updateFirestoreDocument(FirebaseFirestore db, String generatedDocumentID, Payment newItem) {
        newItem.setPaymentID(generatedDocumentID);

        db.collection(getString(R.string.c_payment))
                .document(generatedDocumentID)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateListStatus(getArguments().getString(ARG_ListID));
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
