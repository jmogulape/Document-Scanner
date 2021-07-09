package com.todobom.queenscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebViewForDownload extends AppCompatActivity {
    WebView web;
    //    ProgressBar progressBar;
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private ProgressDialog mProgress1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_for_download);

        web = findViewById(R.id.webview);
        web.setWebViewClient(new Browser_Home());
        web.setWebChromeClient(new ChromeClient3());
        WebSettings webSettings = web.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);

        String subName = getIntent(). getStringExtra("url");

        internetAvailable(subName);

    }
    public void internetAvailable(String url){
        if(isNetworkConnected()) {

            loadWebsite(""+url);
        }
        else {

            mProgress1.setMessage("Sorry! Turn on your internet... ");
            mProgress1.show();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    /* Create an Intent that will start the Menu-Activity. */
//                    Intent intent = new Intent(getApplicationContext(),Help.class);
//                    startActivity(intent);
//                    Dashboard.this.finish();

                }
            }, SPLASH_DISPLAY_LENGTH);

        }
    }

    public void loadWebsite(String url){
        web.loadUrl(""+url);
    }

    private class Browser_Home extends WebViewClient {
        Browser_Home() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }
    }

    public class ChromeClient3 extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient3() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;

            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }
        public void onHideCustomView() {

            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}