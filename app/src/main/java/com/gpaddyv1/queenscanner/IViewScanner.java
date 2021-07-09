package com.joshuabutton.queenscanner;

import android.graphics.Bitmap;

public interface IViewScanner {

    void onResult(Bitmap bitmap);

    void chooseImage();

    void editImage(String path);
}
