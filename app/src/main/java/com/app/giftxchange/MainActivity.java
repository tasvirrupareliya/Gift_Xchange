package com.app.giftxchange;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

       /* FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }*/

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

        String register_email = binding.fitRegister.rEmail.getText().toString();
        String register_name = binding.fitRegister.rName.getText().toString();
        String register_password = binding.fitRegister.rPassword.getText().toString();
        String login_email = binding.fitLogin.lEmail.getText().toString();
        String login_password = binding.fitLogin.lPassword.getText().toString();

        binding.fitRegister.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount("registeremail@gmail.com", "Gcshu@4543");

                /*if (register_name.isEmpty() && register_password.isEmpty() && register_email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Something is missing", Toast.LENGTH_SHORT).show();
                } else {
                    createAccount("registeremail@gmail.com", "Gcshu@4543");
                }*/
            }
        });

        binding.fitLogin.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_email.equals("")) {
                    Toast.makeText(MainActivity.this, "Enter Email Address", Toast.LENGTH_LONG).show();
                } else if (login_password.equals("")) {
                    Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                } else {
                    signIn("registeremail@gmail.com", "Gcshu@4543");
                }
            }
        });
    }

    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("111", "ds");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("111", "error");
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("111", e.toString());
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
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