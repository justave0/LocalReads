package com.example.localreads.SignOn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.localreads.MainActivity;
import com.example.localreads.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SavePasswordRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.identity.SignInPassword;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Headers;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";
    EditText etLoginUsername;
    EditText etLoginPassword;
    Button btLogin;
    Button btLoginCreateAccount;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 0;
    private boolean showOneTapUI = true;
    private BeginSignInRequest signUpRequest;
    private static final int REQUEST_CODE_GIS_SAVE_PASSWORD = 2; /* unique request id */
    private AsyncHttpClient clienthttp;
    private HashMap<String, String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Login persistence
        if (ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else{
            callOneTap();
        }

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btLogin = findViewById(R.id.btLogin);
        btLoginCreateAccount = findViewById(R.id.btLoginCreateAccount);



        btLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(etLoginUsername.getText().toString(), etLoginPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, com.parse.ParseException e) {
                                if (user != null) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.e(TAG, e.toString());
                                    Toast.makeText(LoginActivity.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        btLoginCreateAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void callOneTap() {
        oneTapClient = Identity.getSignInClient(this);

        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        signUpUser();
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
    }

    private void signUpUser() {
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         * Signs in the user using Google SignIn
         * Implement Later
         *
         */
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idTokenString = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();
                    //token verifier


//                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
//                            // Specify the CLIENT_ID of the app that accesses the backend:
//                            .setAudience(Arrays.asList((getString(R.string.android_client_id))))
//                            .setIssuer("https://accounts.google.com")
//                            // Or, if multiple clients access the backend:
//                            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                            .build();
//                    GoogleIdToken idToken = verifier.verify(idTokenString);
                    if (idTokenString !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Log.d(TAG, "Got ID token.");
                        verifyIdToken(idTokenString);
                    } else if (password != null) {
                        // Got a saved username and password. Use them to authenticate
                        // with your backend.
                        Log.d(TAG, "Got password.");
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d(TAG, "One-tap dialog was closed.");
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d(TAG, "One-tap encountered a network error.");
                            // Try again or just ignore.
                            break;
                        default:
                            Log.d(TAG, "Couldn't get credential from result."
                                    + e.getLocalizedMessage());
                            break;
                    }
                }
                break;
            case REQUEST_CODE_GIS_SAVE_PASSWORD:
                if (resultCode == Activity.RESULT_OK) {
                    /* password was saved */
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    /* password saving was cancelled */
                }
        }
    }

    private void verifyIdToken(String idTokenString) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idTokenString;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    Date currentTime = Calendar.getInstance().getTime();
                    Boolean iss = jsonObject.getString("iss").equals("https://accounts.google.com");
                    long time = currentTime.getTime() / 1000;
                    long expTime = Long.valueOf(jsonObject.getString("exp"));
                    Boolean exp = Long.valueOf(jsonObject.getString("exp")) > time;
                    if(jsonObject.getString("iss").equals("https://accounts.google.com") && exp){
                        authenticateBackend(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, String.valueOf(statusCode) + response);
            }
        });

    }




    private void authenticateBackend(JSONObject jsonObject) throws JSONException, ParseException {
        HashMap<String, String> jsonData = new HashMap<String, String>();

        // Print user identifier
        jsonData.put("userId",jsonObject.getString("sub"));
        //System.out.println("User ID: " + userId);

        // Get profile information from payload
        jsonData.put("email",jsonObject.getString("email"));
        jsonData.put("emailVerified", jsonObject.getString("email_verified"));
        jsonData.put("name", jsonObject.getString("name"));
        jsonData.put("picture", jsonObject.getString("picture"));
        jsonData.put("locale", jsonObject.getString("locale"));
        jsonData.put("family_name",jsonObject.getString("family_name"));
        jsonData.put("given_name", jsonObject.getString("given_name"));

        ParseQuery userQuery = new ParseQuery("_User");
        userQuery.whereEqualTo("email", jsonData.get("email"));
        if (userQuery.count() == 0) {
            userData = jsonData;
            showGoogleSignUpDialog(jsonData);
        }
        else{

        }
    }

    private void showGoogleSignUpDialog(HashMap<String, String> jsonData) {
        FragmentManager fm = getSupportFragmentManager();
        GoogleSignUpDialogFragment googleSignUpDialogFragment = GoogleSignUpDialogFragment.newInstance(jsonData);
        googleSignUpDialogFragment.show(fm, GoogleSignUpDialogFragment.class.getSimpleName());
    }

    public void signUpUserIntent(String username, String tag) {
        ParseUser user = new ParseUser();
        user.setUsername(username);

        //Random Password Generator
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        user.setPassword(generatedString);
        user.setEmail(userData.get("email"));
        user.put("location", new ParseGeoPoint(0,0));

        if (tag.equals("Reader")) {
            user.put("tag", "reader");
        }
        else {
            user.put("tag", "author");
        }
        // Set custom properties
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Intent intent;
                    if (tag.equals("Reader")){
                        intent = new Intent(LoginActivity.this, SignOnReaderActivity.class);
                    }
                    else{
                        intent = new Intent(LoginActivity.this, SignOnAuthorActivity1.class);
                    }
                    startActivity(intent);
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

//    private void savePassword(String userId, String password) {
//        SignInPassword signInPassword = new SignInPassword(userId, password);
//        SavePasswordRequest savePasswordRequest =
//                SavePasswordRequest.builder().setSignInPassword(signInPassword).build();
//        Identity.getCredentialSavingClient(this)
//                .savePassword(savePasswordRequest)
//                .addOnSuccessListener(
//                        result -> {
//                            try {
//                                startIntentSenderForResult(
//                                        result.getPendingIntent().getIntentSender(),
//                                        REQUEST_CODE_GIS_SAVE_PASSWORD,
//                                        /* fillInIntent= */ null,
//                                        /* flagsMask= */ 0,
//                                        /* flagsValue= */ 0,
//                                        /* extraFlags= */ 0,
//                                        /* options= */ null);
//                            } catch (IntentSender.SendIntentException e) {
//                                e.printStackTrace();
//                            }
//                        });
//    }
}