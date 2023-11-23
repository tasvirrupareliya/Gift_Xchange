package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.setToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityPremiumViewBinding;
import com.app.giftxchange.model.Premium;
import com.app.giftxchange.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class PremiumView extends AppCompatActivity {

    ActivityPremiumViewBinding binding;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    String CustomerID, Ephemeral_keys, ClientServerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PaymentConfiguration.init(PremiumView.this, getString(R.string.public_key));
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            PaymentResults(paymentSheetResult);
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String currentID = getSharedData(PremiumView.this, getString(R.string.key_userid), null);

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

                                String purchaseDateStr = document.getString("purchaseDate");

                                if (isOneMonthPassed(purchaseDateStr)) {
                                    // One month has passed, update UI accordingly
                                    binding.purchased.setVisibility(View.GONE);
                                    binding.buyNow.setVisibility(View.VISIBLE);
                                }

                                Log.e("PremiumCheck", "PremiumID: " + premiumID);
                            }

                        } else {
                            binding.purchased.setVisibility(View.GONE);
                            binding.buyNow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    StartPayment("2000");
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

    private void StartPayment(String totalprice) {
        createCustomer(totalprice);
    }

    private void createCustomer(String totalprice) {
        Utils.showProgressDialog(PremiumView.this, getString(R.string.please_wait));
        RequestQueue queue = Volley.newRequestQueue(PremiumView.this);
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
                    Utils.hideProgressDialog(PremiumView.this);
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
        RequestQueue queue = Volley.newRequestQueue(PremiumView.this);
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
                    Utils.hideProgressDialog(PremiumView.this);
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
        RequestQueue queue = Volley.newRequestQueue(PremiumView.this);
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
                Utils.hideProgressDialog(PremiumView.this);
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

    private void PaymentResults(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            String userid = getSharedData(this, getString(R.string.key_userid), null);
            initiatePayment(userid);
            setToast(PremiumView.this, "Payments was Successfully");
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            setToast(PremiumView.this, "Payments was Failed");
        }

        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            setToast(PremiumView.this, "Payments was Canceled");
        }
    }

    private void MakePayments() {
        Utils.hideProgressDialog(PremiumView.this);
        paymentSheet.presentWithPaymentIntent(ClientServerId, new PaymentSheet.Configuration("GiftXchange Company", new PaymentSheet.CustomerConfiguration(CustomerID, Ephemeral_keys)));
    }

    private void initiatePayment(String currentID) {

        Utils.showProgressDialog(PremiumView.this, getString(R.string.please_wait));

        Premium newItem = new Premium(generateUniquePremiumID(), currentID, getCurrentDate());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.c_premium))
                .document(currentID)
                .set(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        setToast(PremiumView.this, "Premium Purchase Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //setToast(getContext(), "Failed to payment");
                        Utils.hideProgressDialog(PremiumView.this);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.hideProgressDialog(PremiumView.this);
                        }
                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = Calendar.getInstance().getTime();
        return sdf.format(currentDate);
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

    private String generateUniquePremiumID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date currentDate = Calendar.getInstance().getTime();
        return sdf.format(currentDate);
    }
}