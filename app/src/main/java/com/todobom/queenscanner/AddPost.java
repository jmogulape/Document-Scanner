package com.todobom.queenscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPost extends AppCompatActivity {

    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ProgressDialog mProgress;
    private ImageButton mPostImage;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");


        mPostTitle = findViewById(R.id.postTitle);
        mPostDesc = findViewById(R.id.postDescription);
        mSubmitButton = findViewById(R.id.submitPost);

        mPostImage = findViewById(R.id.imageButton);
        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //posting to our database
                startPosting();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {
        mProgress.setMessage("Adding Restaurant...");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();
        final String downloadUrl;

        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)
                && mImageUri != null){
            // start uploading...
            final StorageReference filepath = mStorageRef.child("MBlog_image")
                    .child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("URL", uri.toString());

                            // This is the complete uri, you can store it to realtime database
                            String downloadUrl = uri.toString();

                            DatabaseReference newPost = mPostDatabase.push();

                            Map<String, String> dataToSave = new HashMap<>();
                            dataToSave.put("title", titleVal);
                            dataToSave.put("desc", descVal);
                            dataToSave.put("image", downloadUrl);
                            dataToSave.put("timestamp", String.valueOf(System.currentTimeMillis()));
                            dataToSave.put("userId",mUser.getUid());

                            newPost.setValue(dataToSave);
                            mProgress.dismiss();
                            //foloww

                            Toast.makeText(AddPost.this, "posted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }


}