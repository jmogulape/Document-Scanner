package com.todobom.queenscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gpaddy.hungdh.camscanner.MainActivity;

public class Login extends AppCompatActivity {

    EditText email,password;

    private RadioButton radiouserType;
    private RadioGroup radioGroup;
    private Button submitButtonn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private String emailText, passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailTex);
        password = findViewById(R.id.passwordText);
        submitButtonn = findViewById(R.id.submitButton);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(Login.this, "user sign in", Toast.LENGTH_LONG)
                    .show();
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }else {
            Toast.makeText(Login.this, "not signed in", Toast.LENGTH_LONG).show();
        }


        submitButtonn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){

                emailText = email.getText().toString().trim();
                passwordTxt = password.getText().toString();

                mAuth.signInWithEmailAndPassword(""+emailText, ""+passwordTxt)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "signed in", Toast.LENGTH_LONG)
                                            .show();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Failed sign in", Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
            }

        });
    }

    public void registerFirst(View view) {
        startActivity(new Intent(getApplicationContext(),RegisterUser.class));
    }

    public void onGuest(View view) {
        startActivity(new Intent(Login.this, MainActivity.class));
    }
}