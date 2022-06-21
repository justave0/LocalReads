package com.example.localreads.SignOn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localreads.MainActivity;
import com.example.localreads.R;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SignOnReaderActivity extends AppCompatActivity {

    String TAG = "SignOnReaderActivity";
    String APP_TAG = "LocalReads";
    TextView tvReaderFavoriteGenres;
    List<String> favoriteGenres = new ArrayList<>();
    CheckBox cbRAction;
    CheckBox cbRClassics;
    CheckBox cbRComics;
    CheckBox cbRDetective;
    CheckBox cbRFantasy;
    CheckBox cbRHistoricalFiction;
    CheckBox cbRHorror;
    CheckBox cbRFiction;
    CheckBox cbRRomance;
    CheckBox cbRSuspense;
    CheckBox cbRSciFi;
    CheckBox cbRShortStories;
    CheckBox cbRWomen;
    CheckBox cbRBiographies;
    CheckBox cbRCookbooks;
    CheckBox cbREssays;
    CheckBox cbRHistory;
    CheckBox cbRMemoir;
    CheckBox cbRPoetry;
    CheckBox cbRSelfHelp;
    CheckBox cbRTrueCrime;
    Button btReaderSignOn;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_on_reader);

        tvReaderFavoriteGenres = findViewById(R.id.tvReaderFavoriteGenres);
        cbRAction = findViewById(R.id.cbRAction);
        cbRBiographies = findViewById(R.id.cbRBiographies);
        cbRClassics = findViewById(R.id.cbRClassics);
        cbRComics = findViewById(R.id.cbRComics);
        cbRCookbooks = findViewById(R.id.cbRCookbooks);
        cbRDetective = findViewById(R.id.cbRDetective);
        cbREssays = findViewById(R.id.cbREssays);
        cbRFantasy = findViewById(R.id.cbRFantasy);
        cbRFiction = findViewById(R.id.cbRFiction);
        cbRHistoricalFiction = findViewById(R.id.cbRHistoricalFiction);
        cbRHorror = findViewById(R.id.cbRHorror);
        cbRRomance = findViewById(R.id.cbRRomance);
        cbRSciFi = findViewById(R.id.cbRSciFi);
        cbRShortStories = findViewById(R.id.cbRShortStories);
        cbRWomen = findViewById(R.id.cbRWomen);
        cbRHistory = findViewById(R.id.cbRHistory);
        cbRMemoir = findViewById(R.id.cbRMemoir);
        cbRPoetry = findViewById(R.id.cbRPoetry);
        cbRSelfHelp = findViewById(R.id.cbRSelfHelp);
        cbRTrueCrime = findViewById(R.id.cbRTrueCrime);
        cbRSuspense = findViewById(R.id.cbRSuspense);
        btReaderSignOn = findViewById(R.id.btReaderSignOn);
        // set Favorite Genres of Reader
        btReaderSignOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavoriteGenres();
                createReader();
            }
        });
    }

    private void setFavoriteGenres() {
        //switch doesn't work :(
        if (cbRAction.isChecked()){
            favoriteGenres.add("Action and Adventure");
        }
        if (cbRBiographies.isChecked()){
            favoriteGenres.add("Biographies");
        }
        if (cbRClassics.isChecked()){
            favoriteGenres.add("Classics");
        }
        if (cbRComics.isChecked()){
            favoriteGenres.add("Comics");
        }
        if (cbRCookbooks.isChecked()){
            favoriteGenres.add("Cookbooks");
        }
        if (cbRDetective.isChecked()){
            favoriteGenres.add("Detective");
        }
        if (cbREssays.isChecked()){
            favoriteGenres.add("Essays");
        }
        if (cbRFantasy.isChecked()){
            favoriteGenres.add("Fantasy");
        }
        if (cbRFiction.isChecked()){
            favoriteGenres.add("Fiction");
        }
        if (cbRHistoricalFiction.isChecked()){
            favoriteGenres.add("Historical Fiction");
        }
        if (cbRHorror.isChecked()){
            favoriteGenres.add("Horror");
        }
        if (cbRRomance.isChecked()){
            favoriteGenres.add("Romance");
        }
        if (cbRSciFi.isChecked()){
            favoriteGenres.add("SciFi");
        }
        if (cbRShortStories.isChecked()){
            favoriteGenres.add("Short Stories");
        }
        if (cbRWomen.isChecked()){
            favoriteGenres.add("Women");
        }
        if (cbRHistory.isChecked()){
            favoriteGenres.add("History");
        }
        if (cbRMemoir.isChecked()){
            favoriteGenres.add("Memoir");
        }
        if (cbRPoetry.isChecked()){
            favoriteGenres.add("Poetry");
        }
        if (cbRSelfHelp.isChecked()){
            favoriteGenres.add("Self Help");
        }
        if (cbRTrueCrime.isChecked()){
            favoriteGenres.add("True Crime");
        }
        if (cbRSuspense.isChecked()){
            favoriteGenres.add("Suspense");
        }



    }

    public void createReader(){
        ParseObject entity = new ParseObject("Reader");
        entity.put("username", ParseUser.getCurrentUser().getUsername());
        //Will fix location later
        entity.put("location", new ParseGeoPoint(40.0, -30.0));
        entity.put("favoriteGenres", favoriteGenres);
        entity.put("user", ParseUser.getCurrentUser());

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(e -> {
            if (e==null){
                Intent intent = new Intent(SignOnReaderActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                //Something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



}