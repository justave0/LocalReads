package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    TextView tvSignUp;
    EditText etSignUpUsername;
    EditText etSignUpPassword;
    EditText etSignUpEmail;
    Button btSignUp;
    RadioGroup rgToggle;
    String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tvSignUp = findViewById(R.id.tvSignUp);
        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        btSignUp = findViewById(R.id.btSignUp);
        rgToggle = findViewById(R.id.rgToggle);

        ParseUser user = new ParseUser();

        btSignUp.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String userTag = null;
                // Set core properties
                user.setUsername(etSignUpUsername.getText().toString());
                user.setPassword(etSignUpPassword.getText().toString());
                user.setEmail(etSignUpEmail.getText().toString());
                //Check RadioGroup
                switch(rgToggle.getCheckedRadioButtonId()) {
                    case R.id.rbReader:
                        userTag = "reader";
                        break;
                    case R.id.rbAuthor:
                        userTag = "author";
                        break;
                }
                // Set custom properties
                user.put("tag", userTag);
                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        });

    }
}