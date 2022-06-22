package com.example.localreads;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    public String userTag;
    public String readerId;
    public String authorId;
    private final String TAG = "MainActivity";
    Menu topMenu;
    private final static String KEY_LOCATION = "location";
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getUserTag();
        // Check is current location is null
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        MainActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
    }

    // Helper function to get if the current user is a reader or author
    private void getUserTag() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject user, com.parse.ParseException e) {
                if (e == null) {
                    userTag = user.getString("tag");
                    if (userTag.equals("reader")) {
                        getMenuInflater().inflate(R.menu.reader_top_menu, topMenu);
                        getReader();
                    } else {
                        getMenuInflater().inflate(R.menu.author_top_menu, topMenu);
                        getAuthor();
                    }
                } else {
                    Log.e(TAG, "Parse Error: " + e.toString());
                }
            }
        });

    }
    private void getReader(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Reader");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null){
                    readerId = object.getObjectId();
                    Log.i(TAG, readerId);
                    saveLocation();
                }
                else{
                    Log.i(TAG, e.toString());
                }

            }
        });
    }

    private void getAuthor(){
        // copy getReader()
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the reader menu first. This will get changed in get getUserTag() accordingly
        topMenu = menu;
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }

    // Goes to chat activity
    public void onChatAction(MenuItem mi) {
    }

    // Goes to create activity
    public void onCreateAction(MenuItem mi) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateLocation(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });

    }

    public void updateLocation(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        mCurrentLocation = location;
        saveLocation();
        // Display the current location
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void saveLocation() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        // Retrieve the object by id
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), (object, e) -> {
            if (e == null) {
                //Object was successfully retrieved
                // Update the fields we want to
                object.put("location", new ParseGeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                //All other fields will remain the same
                object.saveInBackground();
                getReverseGeocode();
            } else {
                // something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getReverseGeocode() {
        String reverseGeocode = getString(R.string.geocode_url) + "latlng=" + mCurrentLocation.getLatitude()
                +","+ mCurrentLocation.getLongitude() +"&result_type=locality&key=" + getString(R.string.google_maps_api_key);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(reverseGeocode, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG,  "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONObject results = (JSONObject) jsonObject.getJSONArray("results").get(0);
                    address = results.getString("formatted_address");
                    Log.i(TAG, address);

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

}