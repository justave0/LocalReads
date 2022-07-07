package com.example.localreads;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.localreads.CreateBook.CreateActivity;
import com.example.localreads.Fragments.DetailAuthorFragment;
import com.example.localreads.Fragments.DetailBookFragment;
import com.example.localreads.Fragments.LocalAuthorFragment;
import com.example.localreads.Fragments.LocalFeedFragment;
import com.example.localreads.Fragments.ReaderProfileFragment;
import com.example.localreads.Models.Author;
import com.example.localreads.Models.Reader;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    public String userTag;
    public Reader reader;
    Author author;
    private final String TAG = "MainActivity";
    private final static String KEY_LOCATION = "location";
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    public String address;
    Menu topMenu;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    LocalFeedFragment fragment_local_feed;
    DetailBookFragment fragment_detail_book;
    LocalAuthorFragment fragment_local_author;
    DetailAuthorFragment fragment_detail_author;
    ReaderProfileFragment fragment_reader_profile;
    MaterialToolbar tabMain;
    CollapsingToolbarLayout ctlMain;
    TextView tvTitleText;
    Chip genresChip;
    Chip radiusChip;
    List<String> selectedGenres = new ArrayList<>();
    public ProgressBar spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabMain = findViewById(R.id.mtbMain);
        topMenu = tabMain.getMenu();
        ctlMain = findViewById(R.id.ctlMain);
        tvTitleText = findViewById(R.id.tvTitleText);
        genresChip = findViewById(R.id.genresChip);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        getUserTag();
        fragment_local_feed = new LocalFeedFragment();
        fragment_detail_book = new DetailBookFragment();
        fragment_local_author = new LocalAuthorFragment();
        fragment_detail_author = new DetailAuthorFragment();
        fragment_reader_profile = new ReaderProfileFragment();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flTemp, fragment_local_feed, LocalFeedFragment.class.getSimpleName());
        ft.commit();

        genresChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateGenresMenu();
            }
        });

        tabMain.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                optionsItemSelected(item);
                return false;
            }
        });

        // Check is current location is null
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        MainActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
//hello world
        //Fragment Manager
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                String tag = null;
                Bundle args = new Bundle();
                switch (item.getItemId()) {
                    case R.id.action_local_feed:
                        fragment = fragment_local_feed;
                        tag = LocalFeedFragment.class.getSimpleName();
                        break;

                    case R.id.action_local_author:
                        fragment = fragment_local_author;
                        tag = LocalAuthorFragment.class.getSimpleName();
                        break;

                    case R.id.action_profile:
                        if (reader != null){
                            fragment = fragment_reader_profile;
                            tag = ReaderProfileFragment.class.getSimpleName();
                        }
                        else if (author != null){
                            args.putParcelable("Author", Parcels.wrap(author));
                            fragment_detail_author.setArguments(args);
                            fragment = fragment_detail_author;
                            tag = DetailAuthorFragment.class.getSimpleName();
                        }
                        else{
                            String text = "Profile is Loading";
                            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        boolean detailVisible = getVisibleFragment();
                        break;

                    default:
                        fragment = new Fragment();
                        tag = "rubbish";
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flTemp, fragment, tag);
                ft.commit();

                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_local_feed);
    }

    private boolean getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        ArrayList<Fragment> fragments = (ArrayList<Fragment>) fragmentManager.getFragments();
        for (Fragment fragment: fragments){
            if (fragment.getClass().getSimpleName().equals(DetailAuthorFragment.class.getSimpleName())){
                DetailAuthorFragment frag = (DetailAuthorFragment) fragmentManager.findFragmentByTag(DetailAuthorFragment.class.getSimpleName());
                fragmentManager.beginTransaction().remove(frag).commit();
            }
        }
        return false;
    }

    private void inflateGenresMenu() {
        PopupMenu popupMenu = new PopupMenu(this, genresChip);
        popupMenu.getMenuInflater().inflate(R.menu.genres_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            selectedGenres.clear();
            switch (item.getItemId()){
                case R.id.action:
                    selectedGenres.add(getString(R.string.action_and_adventure));
                    break;
                case R.id.classics:
                    selectedGenres.add(getString(R.string.classics));
                    break;
                case R.id.comics:
                    selectedGenres.add(getString(R.string.comic_books_and_graphic_novels));
                    break;
                case R.id.detective:
                    selectedGenres.add(getString(R.string.detective_and_mystery));
                    break;
                case R.id.fantasy:
                    selectedGenres.add(getString(R.string.fantasy));
                    break;
                case R.id.historicalFiction:
                    selectedGenres.add(getString(R.string.historical_fiction));
                    break;
                case R.id.horror:
                    selectedGenres.add(getString(R.string.horror));
                    break;
                case R.id.fiction:
                    selectedGenres.add(getString(R.string.literary_fiction));
                    break;
                case R.id.romance:
                    selectedGenres.add(getString(R.string.romance));
                    break;
                case R.id.sciFi:
                    selectedGenres.add(getString(R.string.science_fiction));
                    break;
                case R.id.shortStories:
                    selectedGenres.add(getString(R.string.short_stories));
                    break;
                case R.id.suspense:
                    selectedGenres.add(getString(R.string.suspense_and_thrillers));
                    break;
                case R.id.women:
                    selectedGenres.add(getString(R.string.women_s_fiction));
                    break;
                case R.id.biographies:
                    selectedGenres.add(getString(R.string.biographies_and_autobiographies));
                    break;
                case R.id.cookbooks:
                    selectedGenres.add(getString(R.string.cookbooks));
                    break;
                case R.id.essays:
                    selectedGenres.add(getString(R.string.essays));
                    break;
                case R.id.history:
                    selectedGenres.add(getString(R.string.history));
                    break;
                case R.id.memoir:
                    selectedGenres.add(getString(R.string.memoir));
                    break;
                case R.id.poetry:
                    selectedGenres.add(getString(R.string.poetry));
                    break;
                case R.id.selfHelp:
                    selectedGenres.add(getString(R.string.self_help));
                    break;
                case R.id.trueCrime:
                    selectedGenres.add(getString(R.string.true_crime));
                    break;
                default:
                    selectedGenres.add("");
            }
            fragment_local_feed.selectedGenres = selectedGenres;
            fragment_local_feed.books.clear();
            fragment_local_feed.queryAuthors();
            genresChip.setText(selectedGenres.get(0));

            return true;

        });
        popupMenu.show();
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
        ParseQuery<Reader> query = ParseQuery.getQuery("Reader");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getFirstInBackground(new GetCallback<Reader>() {
            @Override
            public void done(Reader object, ParseException e) {
                if (e == null){
                    reader = object;
                    Log.i(TAG, reader.getObjectId());
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
        ParseQuery<Author> query = ParseQuery.getQuery("Author");
        query.include("user");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // The query will search for a ParseObject, given its objectId.
        // When the query finishes running, it will invoke the GetCallback
        // with either the object, or the exception thrown
        query.getFirstInBackground(new GetCallback<Author>() {
            @Override
            public void done(Author object, ParseException e) {
                if (e == null){
                    author = object;

                    Log.i(TAG, author.getObjectId());
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
    public void createAction() {
        if (address != null && author != null) {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            intent.putExtra("address", address);
            intent.putExtra("authorID", author.getObjectId());
            startActivity(intent);
        }
    }


    public boolean optionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat:
                return true;
                default:
                return super.onOptionsItemSelected(item);
        }
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
                        saveLocation();
                    }
                }
            }
        };

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    public void updateLocation(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        mCurrentLocation = location;
        //saveLocation();
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
                if(mCurrentLocation != null) {
                    object.put(KEY_LOCATION, new ParseGeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                }
                //All other fields will remain the same
                try {
                    object.save();
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                if (mCurrentLocation != null) {
                    getReverseGeocode(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                }
            } else {
                // something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    public String getReverseGeocode(LatLng latlng) {
        String reverseGeocode = getString(R.string.geocode_url) + "latlng=" + latlng.latitude
                +","+ latlng.longitude +"&result_type=locality&key=" + getString(R.string.google_maps_api_key);
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
                    tvTitleText.setText("Showing New Releases Near: " + address);
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
        return address;
    }

}