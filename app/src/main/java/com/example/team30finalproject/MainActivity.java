package com.example.team30finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText  editTextEmail, editTextPassword;
    private Button signIn;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.redirect_to_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editTextPassword.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Mobile no. can not be empty");
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

                mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("email", editTextEmail.getText().toString().trim());
                            intent.putExtra("name", "Tejas Wate");
                            intent.putExtra("mobile", "3239496863");
                            startActivity(intent);

//                            DatabaseReference users = mDatabase.child("users");
//
//                            users.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                    for (DataSnapshot user: snapshot.getChildren()){
//
//                                        String userE = user.child("email").getValue(String.class);
//                                        String userM = user.getKey();
//
//                                        if(userE.equals(email)){
//                                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            intent.putExtra("email", editTextEmail.getText().toString().trim());
//                                            intent.putExtra("name", snapshot.child("name").getValue(String.class));
//                                            intent.putExtra("mobile", userM);
//                                            startActivity(intent);
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}