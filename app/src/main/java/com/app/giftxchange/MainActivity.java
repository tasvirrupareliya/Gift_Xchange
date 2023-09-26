package com.app.giftxchange;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.app.giftxchange.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }


        SharedPreferences sharedPreference = getSharedPreferences(String.valueOf(R.string.share_key), MODE_PRIVATE);
        String share_username = sharedPreference.getString(String.valueOf(R.string.share_username), null);
        String share_email = sharedPreference.getString(String.valueOf(R.string.share_email), null);
        String share_password = sharedPreference.getString(String.valueOf(R.string.share_password), null);

        if (share_email != null && share_password != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }

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

    private void createAccount(String name, String email, String password) {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        try {
            if (name.equals("")) {
                Toast.makeText(MainActivity.this, R.string.please_enter_your_name, Toast.LENGTH_LONG).show();
            } else if (email.equals("")) {
                Toast.makeText(MainActivity.this, R.string.please_enter_email_address, Toast.LENGTH_LONG).show();
            } else if (password.equals("")) {
                Toast.makeText(MainActivity.this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
            } else {
                progressDialog.setMessage("Please Wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.share_key), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString(getString(R.string.share_username), binding.fitRegister.rName.getText().toString().trim());
                                    editor.putString(getString(R.string.share_email), binding.fitRegister.rEmail.getText().toString().trim());
                                    editor.putString(getString(R.string.share_password), binding.fitRegister.rPassword.getText().toString().trim());
                                    editor.apply();

                                    binding.fitRegister.viewRegister.setVisibility(View.GONE);
                                    binding.fitLogin.viewLogin.setVisibility(View.VISIBLE);

                                    binding.fitRegister.rName.setText("");
                                    binding.fitRegister.rEmail.setText("");
                                    binding.fitRegister.rPassword.setText("");

                                    Toast.makeText(MainActivity.this, "You Registered", Toast.LENGTH_SHORT).show();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Please Enter Valid Email Address.", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
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
            Toast.makeText(MainActivity.this, R.string.please_enter_email_address, Toast.LENGTH_LONG).show();
        } else if (password.equals("")) {
            Toast.makeText(MainActivity.this, R.string.please_enter_password, Toast.LENGTH_LONG).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);

                                Toast.makeText(MainActivity.this, "You Successfully Sign In", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Please Enter Correct Information", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() {
    }

    private void updateUI(FirebaseUser user) {

    }

    private boolean isValidEmail(String email) {
        // Define a regular expression pattern for a strong password
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

}