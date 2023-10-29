package com.app.giftxchange.activity;

import static com.app.giftxchange.utils.Utils.getSharedData;
import static com.app.giftxchange.utils.Utils.hideProgressDialog;
import static com.app.giftxchange.utils.Utils.saveSharedData;
import static com.app.giftxchange.utils.Utils.setToast;
import static com.app.giftxchange.utils.Utils.showProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.app.giftxchange.R;
import com.app.giftxchange.databinding.ActivityLoginBinding;
import com.app.giftxchange.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivityLoginBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    DocumentReference ref;
    FirebaseFirestore firebaseFirestore;
    private static final int RC_SIGN_IN = 9001;
    User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userid = getSharedData(this, getString(R.string.key_userid), null);
        String useremail = getSharedData(this, getString(R.string.key_email), null);


        if (useremail != null && userid != null) {
            reload_nextActivity();
        }

       /* if (currentUser != null) {
            reload_nextActivity();
            saveSharedData(LoginActivity.this, getString(R.string.key_userid), currentUser.getUid());
            saveSharedData(LoginActivity.this, getString(R.string.key_email), currentUser. ());
        }*/

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
            setToast(this, getString(R.string.please_enter_email_address));
            return;
        }

        Query query = FirebaseFirestore.getInstance().collection(getString(R.string.c_registeruser))
                .whereEqualTo("Email", email);

        query.get().addOnCompleteListener(task -> {
            showProgressDialog(this, getString(R.string.please_wait));
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(this, resetTask -> {
                                if (resetTask.isSuccessful()) {
                                    hideProgressDialog(this);
                                    setToast(this, getString(R.string.password_reset_email_sent_please_check_your_email));
                                } else {
                                    hideProgressDialog(this);
                                    setToast(this, getString(R.string.failed_to_send_password_reset_email_please_check_your_email_address));
                                }
                            });
                } else {
                    hideProgressDialog(this);
                    setToast(this, getString(R.string.user_with_this_email_does_not_exist));
                }
            } else {
                hideProgressDialog(this);
                setToast(this, getString(R.string.error_checking_email_existence_please_try_again_later));
            }
        });
    }


    private void createAccount(String name, String email, String password) {
        try {
            if (name.equals("")) {
                setToast(this, getString(R.string.please_enter_your_name));
            } else if (email.equals("")) {
                setToast(this, getString(R.string.please_enter_email_address));
            } else if (password.equals("")) {
                setToast(this, getString(R.string.please_enter_password));
            } else if (!isValidEmail(email)) {
                setToast(this, getString(R.string.please_enter_valid_email));
            } else {
                showProgressDialog(this, getString(R.string.please_wait));

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        String uid = user.getUid();

                                        newUser.setUserID(uid);
                                        newUser.setUserEmail(email);
                                        newUser.setUserName(name);
                                        newUser.setUserPassword(password);
                                        newUser.setUserAge("");

                                        ref = firebaseFirestore.collection(getString(R.string.c_registeruser)).document(uid);
                                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        hideProgressDialog(LoginActivity.this);
                                                        setToast(LoginActivity.this, getString(R.string.username_already_exists_please_use_another_username));
                                                    } else {
                                                        ref.set(newUser, SetOptions.merge())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                        binding.fitRegister.viewRegister.setVisibility(View.GONE);
                                                                        binding.fitLogin.viewLogin.setVisibility(View.VISIBLE);

                                                                        hideProgressDialog(LoginActivity.this);
                                                                        setToast(LoginActivity.this, getString(R.string.user_registered_successfully));

                                                                        binding.fitRegister.rEmail.setText("");
                                                                        binding.fitRegister.rPassword.setText("");
                                                                        binding.fitRegister.rName.setText("");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        hideProgressDialog(LoginActivity.this);
                                                                        setToast(LoginActivity.this, getString(R.string.failed_to_create_user_document_in_firestore));
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    hideProgressDialog(LoginActivity.this);
                                                    setToast(LoginActivity.this, getString(R.string.error_checking_username_existence));
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressDialog(LoginActivity.this);
                                setToast(LoginActivity.this, getString(R.string.email_is_already_exist_use_other_email));
                            }
                        });
            }
        } catch (Exception e) {
            hideProgressDialog(LoginActivity.this);
            setToast(LoginActivity.this, e.toString());
        }
    }


    private void signIn(String email, String password) {
        if (email.equals("")) {
            setToast(this, getString(R.string.please_enter_email_address));
        } else if (password.equals("")) {
            setToast(this, getString(R.string.please_enter_password));
        } else if (!isValidEmail(email)) {
            setToast(this, getString(R.string.please_enter_valid_email));
        } else {
            showProgressDialog(this, getString(R.string.please_wait));

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                hideProgressDialog(LoginActivity.this);

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();

                                    saveSharedData(LoginActivity.this, getString(R.string.key_userid), uid);

                                    DocumentReference userDocRef = firebaseFirestore.collection(getString(R.string.c_registeruser)).document(uid);
                                    userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    reload_nextActivity();
                                                } else {
                                                    setToast(LoginActivity.this, getString(R.string.user_is_not_exist));
                                                }
                                            } else {
                                                setToast(LoginActivity.this, getString(R.string.error_retrieving_user_document));
                                            }
                                        }
                                    });
                                }
                                setToast(LoginActivity.this, getString(R.string.you_successfully_sign_in));
                            } else {
                                hideProgressDialog(LoginActivity.this);
                                setToast(LoginActivity.this, getString(R.string.user_not_exist_please_enter_correct_information));
                            }
                        }
                    });
        }
    }

    private void reload_nextActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                            setToast(LoginActivity.this, getString(R.string.email_verification_sent_please_check_your_email));
                        } else {
                            setToast(LoginActivity.this, getString(R.string.failed_to_send_email_verification));
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
                setToast(LoginActivity.this, e.toString());
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
                                saveSharedData(LoginActivity.this, getString(R.string.key_userid), uid);

                                newUser.setUserID(uid);
                                newUser.setUserEmail(user.getEmail());
                                newUser.setUserName(user.getDisplayName());
                                newUser.setUserPassword("");
                                newUser.setUserAge("");

                                FirebaseFirestore.getInstance()
                                        .collection(getString(R.string.c_registeruser))
                                        .document(uid)
                                        .set(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                reload_nextActivity();
                                                setToast(LoginActivity.this, getString(R.string.user_data_stored_in_firestore));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                setToast(LoginActivity.this, getString(R.string.failed_to_create_user_document_in_firestore));
                                            }
                                        });
                            }
                        } else {
                            setToast(LoginActivity.this, getString(R.string.firebase_authentication_failed));
                        }
                    }
                });
    }
}