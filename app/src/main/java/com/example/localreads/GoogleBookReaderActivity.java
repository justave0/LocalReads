package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GoogleBookReaderActivity extends AppCompatActivity {

  WebView googleBooksWebView;
  String bookId;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_google_book_reader);
    googleBooksWebView = findViewById(R.id.googleBooksWebView);

    bookId = getIntent().getStringExtra("bookId");

    googleBooksWebView.getSettings().setJavaScriptEnabled(true);

    googleBooksWebView.loadUrl("file:///android_asset/" + "loadGoogleBook.html" + "?" + bookId);

    googleBooksWebView.setWebViewClient(
        new WebViewClient() {
          public void onPageFinished(WebView view, String url) {
            Log.i("Help ", url);
          }
        });
  }
}
