package com.example.localreads.SignOn;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localreads.R;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.List;

public class SignOnReaderActivity extends AppCompatActivity {

    String TAG = "SignOnReaderActivity";
    TextView tvReaderFavoriteGenres;
    List<String> favoriteGenres;
    CheckBox cbRAction;
    CheckBox cbRClassics;
    CheckBox cbRComics;
    CheckBox cbRDetective;
    CheckBox cbRFantasy;
    CheckBox cbRHistoricalFiction;
    CheckBox cbRHorror;
    CheckBox cbRFiction;
    CheckBox cbRRomance;
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
        //switch doesnt work :(
        if (cbRAction.isSelected()){
            favoriteGenres.add("Action and Adventure");
        }
        if (cbRBiographies.isSelected()){
            favoriteGenres.add("Biographies");
        }
        if (cbRClassics.isSelected()){
            favoriteGenres.add("Classics");
        }
        if (cbRComics.isSelected()){
            favoriteGenres.add("Comics");
        }
        if (cbRCookbooks.isSelected()){
            favoriteGenres.add("Cookbooks");
        }
        if (cbRDetective.isSelected()){
            favoriteGenres.add("Detective");
        }
        if (cbREssays.isSelected()){
            favoriteGenres.add("Essays");
        }
        if (cbRFantasy.isSelected()){
            favoriteGenres.add("Fantasy");
        }


    }

    public void createReader(){
        ParseObject entity = new ParseObject("Reader");

        entity.put("username", "A string");
        Bitmap icon = BitmapFactory.decodeResource(SignOnReaderActivity.this.getResources(),
                R.drawable.instagram_user_outline_24);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bitmapBytes = stream.toByteArray();

        ParseFile image = new ParseFile("profilePic", bitmapBytes);
        try {
            image.save();
        } catch (com.parse.ParseException e) {
            Log.e(TAG, e.toString());
        }
        entity.put("profilePic", image);
        //Will fix location later
        entity.put("location", new ParseGeoPoint(40.0, -30.0));
        entity.put("favoriteGenres", favoriteGenres);
        entity.put("user", ParseUser.getCurrentUser());

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(e -> {
            if (e==null){
                //Save was done
            }else{
                //Something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}