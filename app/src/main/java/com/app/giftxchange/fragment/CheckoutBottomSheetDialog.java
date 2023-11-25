package com.app.giftxchange.fragment;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.saveSharedData;
import static com.app.giftxchange.utils.Utils.setToast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.giftxchange.R;
import com.app.giftxchange.activity.EditProfileView;
import com.app.giftxchange.activity.GiftcardView;
import com.app.giftxchange.activity.PremiumView;
import com.app.giftxchange.databinding.CheckoutviewBinding;
import com.app.giftxchange.model.Payment;
import com.app.giftxchange.utils.Utils;
import com.braintreepayments.cardform.view.CardForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckoutBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String ARG_SUBTOTAL = "subtotal";
    private static final String ARG_CURRID = "currID";
    private static final String ARG_OTHRID = "othrID";
    private static final String ARG_ListID = "ListID";
    CheckoutviewBinding binding;
    private static final int REQUEST_CODE_LOCATION = 1;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    String CustomerID, Ephemeral_keys, ClientServerId;

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

        PaymentConfiguration.init(getContext(), getString(R.string.public_key));
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            PaymentResults(paymentSheetResult);
        });

        fetchpremiumCheck();

        if (getArguments() != null) {
            binding.subtotalTextView.setText(getArguments().getString(ARG_SUBTOTAL));
            binding.chargeTextView.setText("$2.00");
        }
        binding.paymentButton.setOnClickListener(v -> {
            String totalprice = binding.totalTextView.getText().toString();
            totalprice = totalprice.replace("$", "");
            try {
                double price = Double.parseDouble(totalprice);
                int tp = (int) price * 100;

                StartPayment(String.valueOf(tp));
            } catch (Exception e) {
                setToast(getContext(), e.getMessage().toString());
            }
            //initiatePayment(getArguments().getString(ARG_CURRID), getArguments().getString(ARG_OTHRID));
        });
    }

    private void fetchpremiumCheck() {

        String currentID = getSharedData(getContext(), getString(R.string.key_userid), null);

        Utils.showProgressDialog(getActivity(), getString(R.string.please_wait));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.c_premium))
                .whereEqualTo("userID", currentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideProgressDialog(getActivity());

                        if (!queryDocumentSnapshots.isEmpty()) {
                            // User with current userID found in Premium collection
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String premiumID = document.getId();
                                try {
                                    String purchaseDateStr = document.getString("purchaseDate");

                                    if (isOneMonthPassed(purchaseDateStr)) {
                                        // One month has passed, update UI accordingly
                                        String subtotal = getArguments().getString(ARG_SUBTOTAL);
                                        subtotal = subtotal.replace("$", "");
                                        double subtotalValue = Double.parseDouble(subtotal);
                                        double totalValue = (subtotalValue + 2);

                                        binding.totalTextView.setText(String.format("$" + totalValue));
                                        binding.paymentButton.setText(String.format("Payment($%.2f)", totalValue));
                                        binding.paymentButton.setTextSize(15);
                                    } else {
                                        binding.premiumlabel.setVisibility(View.VISIBLE);
                                        binding.premiumTextView.setVisibility(View.VISIBLE);

                                        String subtotal = getArguments().getString(ARG_SUBTOTAL);
                                        subtotal = subtotal.replace("$", "");
                                        double subtotalValue = Double.parseDouble(subtotal);
                                        // Apply 25% discount if premium is visible
                                        double discount = (25 * subtotalValue) / 100;
                                        // subtotalValue -= discount;
                                        binding.premiumTextView.setText(String.format("-$" + discount));

                                        double totalValue = (subtotalValue + 2) - discount;

                                        binding.totalTextView.setText(String.format("$" + totalValue));
                                        binding.paymentButton.setText(String.format("Payment($%.2f)", totalValue));
                                        binding.paymentButton.setTextSize(15);
                                    }
                                } catch (NumberFormatException e) {
                                    binding.totalTextView.setText("Invalid Subtotal");
                                }

                                Log.e("PremiumCheck", "PremiumID: " + premiumID);
                            }

                        } else {
                            String subtotal = getArguments().getString(ARG_SUBTOTAL);
                            subtotal = subtotal.replace("$", "");
                            double subtotalValue = Double.parseDouble(subtotal);
                            double totalValue = (subtotalValue + 2);

                            binding.totalTextView.setText(String.format("$" + totalValue));
                            binding.paymentButton.setText(String.format("Payment($%.2f)", totalValue));
                            binding.paymentButton.setTextSize(15);
                            // User with current userID not found in Premium collection
                            Log.e("PremiumCheck", "User is not a premium user");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        hideProgressDialog(getActivity());
                        Log.e("PremiumCheck", "Error: " + e.getMessage());
                    }
                });
    }

    private void initiatePayment(String currentID, String otherID) {
        Utils.showProgressDialog(getActivity(), getString(R.string.please_wait));

        Payment newItem = new Payment("", currentID, otherID);

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
                        Utils.hideProgressDialog(getActivity());
                        //setToast(getContext(), "Failed to payment");
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
                        Utils.hideProgressDialog(getActivity());
                        Toast.makeText(getContext(), "Failed to update document", Toast.LENGTH_SHORT).show();
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
                                            hideProgressDialog(getActivity());
                                            // Handle the failure
                                            setToast(getActivity(), "Failed to update ListStatus: " + e.getMessage());
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dismiss();
                                                Utils.hideProgressDialog(getActivity());
                                                saveSharedData(getContext(), "listID", getArguments().getString(ARG_ListID));
                                                startActivity(new Intent(getContext(), GiftcardView.class));
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

    private void PaymentResults(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            initiatePayment(getArguments().getString(ARG_CURRID), getArguments().getString(ARG_OTHRID));
            setToast(getContext(), "Payments was Successfully");
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            setToast(getContext(), "Payments was Failed");
        }

        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            setToast(getContext(), "Payments was Canceled");
        }
    }

    private void StartPayment(String totalprice) {
        createCustomer(totalprice);
    }

    private void createCustomer(String totalprice) {
        Utils.showProgressDialog(getActivity(), getString(R.string.please_wait));
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    CustomerID = jsonObject.getString("id");
                    getEphemeralKey(CustomerID, totalprice);

                } catch (JSONException e) {
                    Log.e("error", e.toString());
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Utils.hideProgressDialog(getActivity());
                    Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                    Log.e("Volley Error", "Response Data: " + new String(error.networkResponse.data));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + getString(R.string.private_key));
                return header;
            }
        };
        queue.add(stringRequest);
    }

    private void getEphemeralKey(String customerID, String totalprice) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Ephemeral_keys = jsonObject.getString("id");
                    getPaymentSecret(customerID, Ephemeral_keys, totalprice);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Utils.hideProgressDialog(getActivity());
                    Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                    Log.e("Volley Error", "Response Data: " + new String(error.networkResponse.data));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + getString(R.string.private_key));
                header.put("Stripe-Version", "2023-10-16");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void getPaymentSecret(String customerID, String ephemeral_keys, String totalprice) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ClientServerId = jsonObject.getString("client_secret");

                    MakePayments();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Utils.hideProgressDialog(getActivity());
                Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + getString(R.string.private_key));
                header.put("Stripe-Version", "2023-10-16");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", totalprice);
                params.put("currency", "cad");
                // Check the Stripe API documentation for the correct parameters
                // params.put("some_other_parameter", "value");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void MakePayments() {
        Utils.hideProgressDialog(getActivity());
        paymentSheet.presentWithPaymentIntent(ClientServerId, new PaymentSheet.Configuration("GiftXchange Company", new PaymentSheet.CustomerConfiguration(CustomerID, Ephemeral_keys)));
    }

    private boolean isOneMonthPassed(String purchaseDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date purchaseDate = sdf.parse(purchaseDateStr);

            Calendar currentCalendar = Calendar.getInstance();
            Calendar purchaseCalendar = Calendar.getInstance();
            purchaseCalendar.setTime(purchaseDate);
            // Add one month to the purchase date
            purchaseCalendar.add(Calendar.MONTH, 1);

            // Compare with the current date
            return currentCalendar.after(purchaseCalendar);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
