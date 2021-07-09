package com.todobom.queenscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.joshuabutton.queenscanner.PresenterScanner.FOLDER_NAME;

public class UploadPDF extends AppCompatActivity {
    ImageView upload;
    Uri imageuri = null;
    File folder;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_p_d_f);
        upload = findViewById(R.id.uploadpdf);

        // After Clicking on this we will be
        // redirected to choose pdf
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                if (!folder.exists()) {
                    folder.mkdir();
                }

                // We will be redirected to choose pdf
                galleryIntent.setType("QueenScanner/pdf");
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            // this will show message uploading
            dialog.show();

            String mEmail;
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            mEmail = ""+currentUser.getEmail();
            int i = mEmail.indexOf(".");
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime());
            DatabaseReference myRef = database.getReference(""+mEmail.substring(0, i)).child("pdf").child(""+timeStamp);


            // while pdf is uploading
            imageuri = data.getData();

            final String timestamp1 = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = timestamp1;

            Toast.makeText(UploadPDF.this, imageuri.toString(), Toast.LENGTH_SHORT).show();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(messagePushID + "." + "pdf");
            Toast.makeText(UploadPDF.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageuri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // After uploading is done it progress
                        // dialog box will be dismissed
                        dialog.dismiss();
                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        myRef.child(""+timestamp1).setValue(""+myurl);
                        Toast.makeText(UploadPDF.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(UploadPDF.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}