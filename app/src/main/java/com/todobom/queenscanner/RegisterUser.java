package com.todobom.queenscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.gpaddy.hungdh.camscanner.MainActivity;

public class RegisterUser extends AppCompatActivity {

    EditText email, password, confirmPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);


        mProgress = new ProgressDialog(this);

        email = findViewById(R.id.emailaddress);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        mAuth = FirebaseAuth.getInstance();


    }

    public void alreadyRegistered(View view) {
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

    public void register(View view) {
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String confirmPasswordString = confirmPassword.getText().toString().trim();


        if (!passwordString.equals(""+confirmPasswordString)){
            Toast.makeText(RegisterUser.this, "Password didn't match", Toast.LENGTH_SHORT).show();
        }else {
            if ( passwordString.isEmpty() && passwordString.isEmpty() && emailString.isEmpty()) {
                Toast.makeText(this, "Please fill all field fist", Toast.LENGTH_SHORT).show();
            } else if (passwordString.length() >= 7){

                // All set

                mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(RegisterUser.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterUser.this, "This user already registered \n"+task.getException(), Toast.LENGTH_LONG)
                                            .show();
                                } else {
                                    startPosting();
                                }
                            }
                        });
            }
            else {
                Toast.makeText(RegisterUser.this, "Password should greater than 7", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void startPosting(){
        mProgress.setMessage("creating account please wait.. ");
        mProgress.show();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        mProgress.dismiss();

    }
}