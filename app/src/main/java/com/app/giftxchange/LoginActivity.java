package com.app.giftxchange;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.app.giftxchange.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivityLoginBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    DocumentReference ref;
    FirebaseFirestore firebaseFirestore;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

      /*  if (currentUser != null) {
            reload_nextActivity();
        }
*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.fitLogin.btnGooglesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        binding.fitLogin.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        binding.fitRegister.registerAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fitRegister.viewRegister.setVisibility(View.GONE);
                binding.fitLogin.viewLogin.setVisibility(View.VISIBLE);
            }
        });

        binding.fitLogin.loginAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fitRegister.viewRegister.setVisibility(View.VISIBLE);
                binding.fitLogin.viewLogin.setVisibility(View.GONE);
            }
        });

        binding.fitRegister.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(binding.fitRegister.rName.getText().toString(), binding.fitRegister.rEmail.getText().toString(), binding.fitRegister.rPassword.getText().toString());
            }
        });

        binding.fitLogin.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(binding.fitLogin.lEmail.getText().toString(), binding.fitLogin.lPassword.getText().toString());
            }
        });
    }

    private void resetPassword() {
        String email = binding.fitLogin.lEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = FirebaseFirestore.getInstance().collection("RegisterUser")
                .whereEqualTo("email", email);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // Email exists in Firestore, proceed with sending the reset email
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(this, resetTask -> {
                                if (resetTask.isSuccessful()) {
                                    Toast.makeText(this, "Password reset email sent. Please check your email.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Email does not exist in Firestore
                    Toast.makeText(this, "User with this email does not exist.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the Firestore query failure
                Toast.makeText(this, "Error checking email existence. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent. Please check your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send password reset email. Please check your email address.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void createAccount(String name, String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        try {
            if (name.equals("")) {
                Toast.makeText(LoginActivity.this, R.string.please_enter_your_name, Toast.LENGTH_LONG).show();
            } else if (email.equals("")) {
                Toast.makeText(LoginActivity.this, R.string.please_enter_email_address, Toast.LENGTH_LONG).show();
            } else if (password.equals("")) {
                Toast.makeText(LoginActivity.this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(LoginActivity.this, R.string.please_enter_valid_email, Toast.LENGTH_LONG).show();
            } else {
                progressDialog.setMessage("Please Wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        /*if (!user.isEmailVerified()) {
                                            sendEmailVerification();
                                        }*/

                                        String uid = user.getUid();

                                        ref = firebaseFirestore.collection("RegisterUser").document(uid);
                                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(LoginActivity.this, "Username already exists. Please use another username.", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Map<String, Object> reg_entry = new HashMap<String, Object>();
                                                        reg_entry.put("Name", name);
                                                        reg_entry.put("Email", email);

                                                        ref.set(reg_entry, SetOptions.merge())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(LoginActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(LoginActivity.this, "Failed to create user document", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(LoginActivity.this, "Error checking username existence", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Email is already exist Use other email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void signIn(String email, String password) {
        if (email.equals("")) {
            Toast.makeText(LoginActivity.this, R.string.please_enter_email_address, Toast.LENGTH_LONG).show();
        } else if (password.equals("")) {
            Toast.makeText(LoginActivity.this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
        } else if (!isValidEmail(email)) {
            Toast.makeText(LoginActivity.this, R.string.please_enter_valid_email, Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();

                                    DocumentReference userDocRef = firebaseFirestore.collection("RegisterUser").document(uid);

                                    userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    reload_nextActivity();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "User is not exist!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Error retrieving user document", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                Toast.makeText(LoginActivity.this, "You Successfully Sign In", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "User not exist! Please Enter Correct Information", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void reload_nextActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(String email) {
        // Define a regular expression pattern for a strong password
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Email verification sent. Please check your email.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send email verification.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("111", e.toString());
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {

                                String uid = user.getUid();
                                String name = user.getDisplayName();
                                String email = user.getEmail();

                                // Create a user document in Firestore
                                Map<String, Object> userEntry = new HashMap<>();
                                userEntry.put("Name", name);
                                userEntry.put("Email", email);

                                FirebaseFirestore.getInstance()
                                        .collection("RegisterUser")
                                        .document(uid)
                                        .set(userEntry)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                reload_nextActivity();
                                                Toast.makeText(LoginActivity.this, "User data stored in Firestore", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle Firestore document creation failure
                                                Toast.makeText(LoginActivity.this, "Failed to create user document in Firestore", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Firebase Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}