package com.todobom.queenscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.gpaddy.hungdh.camscanner.MainActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        final Intent i = new Intent( SplashScreen.this, Login.class);

        Thread thread= new Thread()
        {
            @Override
            public void run() {
                try
                {
                    sleep(2000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    startActivity(i);
                    finish();
                }
            }
        };thread.start();


    }
}