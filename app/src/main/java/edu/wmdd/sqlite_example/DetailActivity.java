package edu.wmdd.sqlite_example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        String url = intent.getStringExtra("link");
        webView.loadUrl(url);
    }
}
