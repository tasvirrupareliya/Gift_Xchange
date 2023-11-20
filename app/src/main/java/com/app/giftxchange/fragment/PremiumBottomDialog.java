package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.saveSharedData;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.giftxchange.R;
import com.app.giftxchange.activity.GiftcardView;
import com.app.giftxchange.databinding.PremiumpaymentViewBinding;
import com.app.giftxchange.model.Premium;
import com.app.giftxchange.utils.Utils;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PremiumBottomDialog extends BottomSheetDialogFragment {
    private static final String ARG_CURRID = "currID";
    private static final String ARG_PREMIUMID = "premiumID";
    PremiumpaymentViewBinding binding;

    public static PremiumBottomDialog newInstance(String currentuserID) {
        PremiumBottomDialog fragment = new PremiumBottomDialog();
        Bundle args = new Bundle();
        args.putString(ARG_CURRID, currentuserID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PremiumpaymentViewBinding.inflate(inflater, container, false);
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

        binding.paymentbtn.setOnClickListener(v -> {
            initiatePayment(getArguments().getString(ARG_CURRID));
        });
    }

    private void initiatePayment(String currentID) {

        Utils.showProgressDialog(getActivity(), getString(R.string.please_wait));

        if (binding.cardForm.isValid()) {
            new Handler().postDelayed(() -> {

                // String cardexpirtdate = binding.cardForm.getExpirationMonth() + "/" + binding.cardForm.getExpirationYear();

                Premium newItem = new Premium("", currentID);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(getString(R.string.c_premium))
                        .add(newItem)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String generatedDocumentID = documentReference.getId();
                                newItem.setUserID(generatedDocumentID);
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
            }, 3000);
        } else {
            Utils.hideProgressDialog(getActivity());
            binding.cardForm.validate();
        }
    }

    private void updateFirestoreDocument(FirebaseFirestore db, String generatedDocumentID, Premium newItem) {
        Utils.showProgressDialog(getActivity(), getString(R.string.please_wait));

        newItem.setUserID(generatedDocumentID);

        db.collection(getString(R.string.c_premium))
                .document(generatedDocumentID)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Premium Purchase successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismiss();
                        Toast.makeText(getContext(), "Failed to update document", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.hideProgressDialog(getActivity());
                            binding.paymentView.setVisibility(View.GONE);
                            binding.lottieContainer.setVisibility(View.VISIBLE);
                            binding.fullScreenLottieAnimation.setAnimation(R.raw.success);
                            binding.fullScreenLottieAnimation.playAnimation();
                            binding.fullScreenLottieAnimation.addAnimatorListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                                    dismiss();
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
