package com.example.localreads.SignOn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.ParcelableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.localreads.MainActivity;
import com.example.localreads.R;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SignOnAuthorActivity2 extends AppCompatActivity {
    String TAG = "SignOnAuthorActivity2";
    EditText etSignOnBio;
    EditText etAuthorSignOnCity;
    EditText etAuthorSignOnState;
    EditText etAuthorSignOnCountry;
    Button btAuthorSignOn;
    ParseGeoPoint inputLocation;

    List<String> favoriteGenres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_on_author2);
        favoriteGenres = Parcels.unwrap(getIntent().getParcelableExtra("favoriteGenres"));
        etSignOnBio = findViewById(R.id.etSignOnBio);
        etAuthorSignOnCity = findViewById(R.id.etAuthorSignOnCity);
        etAuthorSignOnState = findViewById(R.id.etAuthorSignOnState);
        etAuthorSignOnCountry = findViewById(R.id.etAuthorSignOnCountry);
        btAuthorSignOn = findViewById(R.id.btAuthorSignOn);

        btAuthorSignOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation();
            }
        });

    }
    // Checking if an author exists
    // Use this code later for converting location to latlng
    private void findLocation() {
        String locationName = etAuthorSignOnCity.getText().toString() + "+" + etAuthorSignOnState.getText().toString() + "+" + etAuthorSignOnCountry.getText().toString();
        locationName = locationName.replace(" ","+");
        String geocode = getString(R.string.geocode_url) + "address=" +locationName + "&key=" + getString(R.string.google_maps_api_key);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(geocode, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG,  "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONObject results = (JSONObject) jsonObject.getJSONArray("results").get(0);
                    JSONObject location = results.getJSONObject("geometry").getJSONObject("location");
                    inputLocation = new ParseGeoPoint( location.getDouble("lat"), location.getDouble("lng"));
                    createAuthor();
                }
                catch (JSONException e){
                    Log.e(TAG, "Hit JSON exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }

    private void createAuthor() {
        ParseObject entity = new ParseObject("Author");
        entity.put("username", ParseUser.getCurrentUser().getUsername());
        //Will fix location later
        entity.put("inputLocation", inputLocation);
        entity.put("favoriteGenres", favoriteGenres);
        entity.put("user", ParseUser.getCurrentUser());
        entity.put("reads", 0);

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(e -> {
            if (e==null){
                Intent intent = new Intent(SignOnAuthorActivity2.this, MainActivity.class);
                startActivity(intent);
            }else{
                //Something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}