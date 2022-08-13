package com.example.team30finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText editTextEmail, editTextPassword, editTextMobile, editTextName;
    private Button signUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextMobile = (EditText) findViewById(R.id.mobile);
        editTextName = (EditText) findViewById(R.id.name);
        signUp = (Button) findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.redirect_to_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String mobile = editTextMobile.getText().toString().trim();
                String name = editTextName.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Email can not be empty");
                    editTextEmail.requestFocus();
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Please enter a valid email address");
                    editTextEmail.requestFocus();
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Password can not be empty");
                    editTextPassword.requestFocus();
                }

                if (password.length() < 6) {
                    editTextPassword.setError("Minimum length should be more than 6");
                    editTextPassword.requestFocus();
                }

                if (mobile.isEmpty()) {
                    editTextMobile.setError("Mobile No. can not be empty");
                    editTextMobile.requestFocus();
                }

                if (mobile.length() != 10) {
                    editTextMobile.setError("Mobile No. should be 10-digit");
                    editTextMobile.requestFocus();
                }

                mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG).show();
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("email", email);
                            obj.put("name", name);
                            obj.put("profile_pic", "https://cdn.vectorstock.com/i/thumb-large/99/94/default-avatar-placeholder-profile-icon-male-vector-23889994.webp");
                            mDatabase.child("users").push().child(mobile);
                            mDatabase.child("users").child(mobile).updateChildren(obj);
                            
                            addUserToDB(email);
                            Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "User Already Registered", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

    }

    public void addUserToDB(String email) {
        Account account = new Account(email);

        mDatabase.child("account").child(String.valueOf(email.hashCode())).setValue(account);
    }


}