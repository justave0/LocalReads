package com.example.localreads.SignOn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.localreads.R;

import java.util.HashMap;

import io.opencensus.tags.Tag;

public class GoogleSignUpActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_up);

        HashMap<String, String> jsonData = (HashMap<String, String>) getIntent().getSerializableExtra("jsonData");
        Log.i(TAG, jsonData.toString());
    }
}