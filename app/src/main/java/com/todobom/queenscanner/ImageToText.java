package com.todobom.queenscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.util.jar.Pack200.Packer.ERROR;

public class ImageToText extends AppCompatActivity {

    EditText mResultEt;
    ImageView mPreviewIv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];
    Button button;
    Uri image_uri;

    private FirebaseAuth mAuth;
    private EditText pdfNameEt;
    private String pdfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("click + button to insert Image");
        button = findViewById(R.id.createPdf);
        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);
        pdfNameEt = findViewById(R.id.pdfName);
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(ImageToText.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main1, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addImage){
            showImageImportDialog();
        }
//        if(id == R.id.setting){
//
//        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        // item display in dialog

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    // camera option clicked
                    if (!checkCameraPermission()){
                        //camera permission not allwed, request it
                        requesCameraPermission();
                    }else {
                        pickCamera();
                    }
                }
                if (which == 1){
                    // gallery option clicked
                    if (!checkStoragePermission()){
                        //storage permission not allwed, request it
                        requesStoragePermission();
                    }else {
                        pickGallery();
                    }
                }
            }


        });

        dialog.create().show();
    }

    private void pickGallery() {
        //intent to pick image from gallery

        Intent intent = new Intent(Intent .ACTION_PICK);
        // set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void requesStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE);

    }

    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result1;
    }

    private void requesCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // handle permission Result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //got image from gallery now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON) // enable image guideline
                        .start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //got image from camera now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }

        //get cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri(); // get image uri
                mPreviewIv.setImageURI(resultUri);

                // get drawable bitmap for text recognition

                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()){
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    //get text from StringBuilder until there is no text
                    for (int i =0; i<items.size(); i++){
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }

                    // set text to edit text
                    mResultEt.setText(sb.toString());
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                //if there is any error then show it
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveButton() {

        PdfDocument myPdfDocument = new PdfDocument();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

        Paint myPaint = new Paint();
        String myString = mResultEt.getText().toString();
        int x = 10, y=25;
        for (String line:myString.split("\n")){
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y+=myPaint.descent()-myPaint.ascent();
        }


        myPdfDocument.finishPage(myPage);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(Calendar.getInstance().getTime());
        pdfName = pdfNameEt.getText().toString();
        if (pdfName.isEmpty()){
            pdfName = timeStamp;
        }
        String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/myPDFFile"+pdfName+".pdf";
        File myFile = new File(myFilePath);
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
        }
        catch (Exception e){
            e.printStackTrace();
            mResultEt.setText("err");
        }

        myPdfDocument.close();

        ProgressDialog dialog;
        String mEmail;
        String pdfName2 = myFile.getName();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mEmail = ""+currentUser.getEmail();
        int i = mEmail.indexOf(".");
        DatabaseReference myRef = database.getReference(""+mEmail.substring(0, i)).child("pdf").child(""+timeStamp).child(""+pdfName);

        Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_LONG).show();

        // Here we are initialising the progress dialog box
//                        dialog = new ProgressDialog(getApplicationContext());
//                        dialog.setMessage("Uploading");
        // this will show message uploading
//                        dialog.show();

        Uri uri;


            uri = Uri.fromFile(myFile);

            String timestamp1 = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = timestamp1;

            StorageReference filepath = storageReference.child(""+pdfName);
//        Toast.makeText(UploadPDF.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(uri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        Log.d("gg", "onClick:" + task.getException());
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

                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        myRef.setValue(""+myurl);
                        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(getApplicationContext(), "Failed"+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }
}