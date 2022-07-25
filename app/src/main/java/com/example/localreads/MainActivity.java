package com.example.localreads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
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
  private static final String KEY_LOCATION = "location";
  Location mCurrentLocation;
  public String address;
  Menu topMenu;
  LocalFeedFragment fragment_local_feed;
  DetailBookFragment fragment_detail_book;
  LocalAuthorFragment fragment_local_author;
  DetailAuthorFragment fragment_detail_author;
  ReaderProfileFragment fragment_reader_profile;
  MaterialToolbar tabMain;
  CollapsingToolbarLayout ctlMain;
  TextView tvTitleText;
  Chip genresChip;
  List<String> selectedGenres = new ArrayList<>();
  public ProgressBar spinner;
  public static HashMap<String, String> googleUserData = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tabMain = findViewById(R.id.mtbMain);
    topMenu = tabMain.getMenu();
    ctlMain = findViewById(R.id.ctlMain);
    tvTitleText = findViewById(R.id.tvTitleText);
    genresChip = findViewById(R.id.genresChip);
    spinner = (ProgressBar) findViewById(R.id.progressBar1);

    googleUserData = (HashMap<String, String>) getIntent().getSerializableExtra("googleUserData");

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

    genresChip.setOnClickListener(v -> inflateGenresMenu());

    tabMain.setOnMenuItemClickListener(
        item -> {
          optionsItemSelected(item);
          return false;
        });

    // Check is current location is null
    if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
      // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
      // is not null.
      mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
    }
    MainActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
    // Fragment Manager
    bottomNavigationView.setOnItemSelectedListener(
        item -> {
          Fragment fragment;
          String tag;
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
              if (reader != null) {
                fragment = fragment_reader_profile;
                tag = ReaderProfileFragment.class.getSimpleName();
              } else if (author != null) {
                args.putParcelable("Author", Parcels.wrap(author));
                fragment_detail_author.setArguments(args);
                fragment = fragment_detail_author;
                tag = DetailAuthorFragment.class.getSimpleName();
              } else {
                String text = "Profile is Loading";
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                return false;
              }
              break;

            default:
              fragment = new Fragment();
              tag = "rubbish";
          }

          FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
          ft1.replace(R.id.flTemp, fragment, tag);
          ft1.commit();

          return true;
        });
    bottomNavigationView.setSelectedItemId(R.id.action_local_feed);
  }

  private void inflateGenresMenu() {
    PopupMenu popupMenu = new PopupMenu(this, genresChip);
    popupMenu.getMenuInflater().inflate(R.menu.genres_popup, popupMenu.getMenu());
    popupMenu.setOnMenuItemClickListener(
        item -> {
          selectedGenres.clear();
          switch (item.getItemId()) {
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
    ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
    userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
    userQuery.getFirstInBackground(
        (user, e) -> {
          if (e == null) {
            userTag = user.getString("tag");
            if (userTag != null && userTag.equals("reader")) {
              getReader();
            } else {
              getAuthor();
            }
          } else {
            Log.e(TAG, "Parse Error: " + e);
          }
        });
  }

  private void getReader() {
    ParseQuery<Reader> readerQuery = ParseQuery.getQuery("Reader");
    readerQuery.whereEqualTo("user", ParseUser.getCurrentUser());
    readerQuery.getFirstInBackground(
        (object, e) -> {
          if (e == null) {
            reader = object;
            Log.i(TAG, reader.getObjectId());
            saveLocation();
          } else {
            Log.i(TAG, e.toString());
          }
        });
  }

  private void getAuthor() {
    ParseQuery<Author> authorQuery = ParseQuery.getQuery("Author");
    authorQuery.include("user");
    authorQuery.whereEqualTo("user", ParseUser.getCurrentUser());
    authorQuery.getFirstInBackground(
        (object, e) -> {
          if (e == null) {
            author = object;
            Log.i(TAG, author.getObjectId());
            saveLocation();
          } else {
            Log.i(TAG, e.toString());
          }
        });
  }

  // Goes to chat activity
  public void onChatAction(MenuItem mi) {
    Intent intent = new Intent(MainActivity.this, MessageFeedActivity.class);
    startActivity(intent);
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

  public void optionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.chat) {
      return;
    }
    super.onOptionsItemSelected(item);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }

  @SuppressLint("MissingPermission")
  @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION})
  void getMyLocation() {
    LocationRequest locationRequest = LocationRequest.create();
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval(20 * 10000);
    LocationCallback locationCallback =
        new LocationCallback() {
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

    FusedLocationProviderClient locationClient =
        LocationServices.getFusedLocationProviderClient(this);
    locationClient.requestLocationUpdates(
        locationRequest, locationCallback, Looper.getMainLooper());
  }

  public void updateLocation(Location location) {
    // GPS may be turned off
    if (location == null) {
      return;
    }
    mCurrentLocation = location;
  }

  private void saveLocation() {
    ParseUser currUser = ParseUser.getCurrentUser();
    if (currUser != null && mCurrentLocation != null) {
      currUser.put(
          KEY_LOCATION,
          new ParseGeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
      currUser.saveInBackground(
          e -> {
            if (e == null) {
              getReverseGeocode(
                  new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
            } else {
              // Something went wrong while saving
              Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });
    }
  }

  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
    super.onSaveInstanceState(savedInstanceState);
  }

  public String getReverseGeocode(LatLng latlng) {
    String reverseGeocode =
        getString(R.string.geocode_url)
            + "latlng="
            + latlng.latitude
            + ","
            + latlng.longitude
            + "&result_type=locality&key="
            + getString(R.string.google_maps_api_key);
    AsyncHttpClient client = new AsyncHttpClient();
    client.get(
        reverseGeocode,
        new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.d(TAG, "onSuccess");
            JSONObject jsonObject = json.jsonObject;
            try {
              JSONObject results = (JSONObject) jsonObject.getJSONArray("results").get(0);
              address = results.getString("formatted_address");
              Log.i(TAG, address);
              tvTitleText.setText("Showing New Releases Near: " + address);
            } catch (JSONException e) {
              Log.e(TAG, "Hit JSON exception", e);
              e.printStackTrace();
            }
          }

          @Override
          public void onFailure(
              int statusCode, Headers headers, String response, Throwable throwable) {}
        });
    return address;
  }
}
