package com.example.localreads;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.localreads.CreateBook.CreateActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private final static String KEY_LOCATION = "location";
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String address;
    Menu topMenu;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    RecyclerView rvBooks;
    LocalFeedFragment fragment_local_feed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar temp = findViewById(R.id.topAppBar);
        topMenu = temp.getMenu();
        getUserTag();
        fragment_local_feed = new LocalFeedFragment();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flTemp, fragment_local_feed, LocalFeedFragment.class.getSimpleName());
        ft.commit();

        // Check is current location is null
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        MainActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);

        //Fragment Manager
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                String tag;
                switch (item.getItemId()) {
                    case R.id.action_local_feed:
                        fragment = fragment_local_feed;
                        tag = LocalFeedFragment.class.getSimpleName();
                        break;

                    case R.id.action_local_author:
//                        fragment = feed_fragment;
//                        tag = FeedFragment.class.getSimpleName();
                        break;

                    case R.id.action_profile:
//                        fragment = profile_fragment;
//                        tag = ProfileFragment.class.getSimpleName();
                        break;

                    default:
                        fragment = new Fragment();
                        tag = "rubbish";
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //ft.replace(R.id.flTemp, fragment, tag);
                ft.replace(R.id.flTemp,fragment_local_feed, LocalFeedFragment.class.getSimpleName());
                ft.commit();

                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_local_feed);

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
                        topMenu.findItem(R.id.createBook).setVisible(false);
                        getReader();
                    } else {
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Author");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null){
                    authorId = object.getObjectId();
                    Log.i(TAG, authorId);
                    saveLocation();
                }
                else{
                    Log.i(TAG, e.toString());
                }

            }
        });
    }


    // Goes to chat activity
    public void onChatAction(MenuItem mi) {
    }

    // Goes to create activity
    public void onCreateAction(MenuItem mi) {
        Intent intent = new Intent (MainActivity.this, CreateActivity.class);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 10000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        updateLocation(location);
                    }
                }
            }
        };

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            updateLocation(location);
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
//                        e.printStackTrace();
//                    }
//                });

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
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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