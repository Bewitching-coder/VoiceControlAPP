// WebViewActivity.java
package com.example.app.openweb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "extra_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView mWebView = findViewById(R.id.webview);
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null) {
            mWebView.loadUrl(url);
        }
    }
    public static void openInWebView(Activity context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, url);
        context.startActivity(intent);
    }

    //方法2
    public static void openInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    //调用方法
    // 用WebView打开一个网页
    //openInWebView(MainActivity.this, "https://www.example.com");
    //
    //在手机浏览器中打开一个网页
    //openInBrowser(MainActivity.this, "https://www.example.com");
}
