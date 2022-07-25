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
import com.example.localreads.Models.Author;
import com.example.localreads.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;

public class SignOnAuthorActivity2 extends AppCompatActivity {
  String TAG = "SignOnAuthorActivity2";
  EditText etSignOnBio;
  LatLng authorLocation;
  Button btAuthorSignOn;
  ParseGeoPoint inputLocation;

  List<String> favoriteGenres = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_on_author2);
    if (!Places.isInitialized()) {
      Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));
    }
    PlacesClient placesClient = Places.createClient(this);
    favoriteGenres = Parcels.unwrap(getIntent().getParcelableExtra("favoriteGenres"));
    etSignOnBio = findViewById(R.id.etSignOnBio);

    btAuthorSignOn = findViewById(R.id.btAuthorSignOn);

    AutocompleteSupportFragment autocompleteFragment =
        (AutocompleteSupportFragment)
            getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

    autocompleteFragment.setOnPlaceSelectedListener(
        new PlaceSelectionListener() {
          @Override
          public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            getLatLng(place);
          }

          @Override
          public void onError(Status status) {
            // TODO: Handle the error.
            Log.i(TAG, "An error occurred: " + status);
          }
        });

    btAuthorSignOn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            createAuthor();
          }
        });
  }

  public void getLatLng(Place place) {
    String placeHTTPS =
        "https://maps.googleapis.com/maps/api/place/details/json?placeid="
            + place.getId()
            + "&key="
            + getString(R.string.google_maps_api_key);
    AsyncHttpClient client = new AsyncHttpClient();
    client.get(
        placeHTTPS,
        new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.d(TAG, "onSuccess");
            JSONObject jsonObject = json.jsonObject;
            try {
              JSONObject results =
                  (JSONObject)
                      jsonObject
                          .getJSONObject("result")
                          .getJSONObject("geometry")
                          .getJSONObject("location");
              inputLocation = new ParseGeoPoint(results.getDouble("lat"), results.getDouble("lng"));
              Log.i(TAG, inputLocation.toString());
            } catch (JSONException e) {
              Log.e(TAG, "Hit JSON exception", e);
              e.printStackTrace();
            }
          }

          @Override
          public void onFailure(
              int statusCode, Headers headers, String response, Throwable throwable) {}
        });
  }

  // Checking if an author exists
  // Use this code later for converting location to latlng
  //    private void findLocation() {
  //        String locationName = etAuthorSignOnCity.getText().toString() + "+" +
  // etAuthorSignOnState.getText().toString() + "+" + etAuthorSignOnCountry.getText().toString();
  //        locationName = locationName.replace(" ","+");
  //        String geocode = getString(R.string.geocode_url) + "address=" +locationName + "&key=" +
  // getString(R.string.google_maps_api_key);
  //        AsyncHttpClient client = new AsyncHttpClient();
  //        client.get(geocode, new JsonHttpResponseHandler() {
  //            @Override
  //            public void onSuccess(int statusCode, Headers headers, JSON json) {
  //                Log.d(TAG,  "onSuccess");
  //                JSONObject jsonObject = json.jsonObject;
  //                try{
  //                    JSONObject results = (JSONObject) jsonObject.getJSONArray("results").get(0);
  //                    JSONObject location =
  // results.getJSONObject("geometry").getJSONObject("location");
  //                    inputLocation = new ParseGeoPoint( location.getDouble("lat"),
  // location.getDouble("lng"));
  //                    createAuthor();
  //                }
  //                catch (JSONException e){
  //                    Log.e(TAG, "Hit JSON exception", e);
  //                    e.printStackTrace();
  //                }
  //            }
  //
  //            @Override
  //            public void onFailure(int statusCode, Headers headers, String response, Throwable
  // throwable) {
  //            }
  //        });
  //    }

  private void createAuthor() {
    Author author = new Author();
    author.setInputLocation(inputLocation);
    author.setBio(etSignOnBio.getText().toString());
    author.setBooks(new ArrayList<>());
    author.setLink("");
    author.setReads(0);
    author.setUser(ParseUser.getCurrentUser());
    author.setFavoriteGenres(favoriteGenres);

    // Saves the new object.
    // Notice that the SaveCallback is totally optional!
    author.saveInBackground(
        e -> {
          if (e == null) {
            Intent intent = new Intent(SignOnAuthorActivity2.this, MainActivity.class);
            startActivity(intent);
          } else {
            // Something went wrong
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }
}
