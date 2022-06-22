package com.example.localreads.SignOn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


import com.example.localreads.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SignOnAuthorActivity1 extends AppCompatActivity {

    TextView tvAuthorFavoriteGenres;
    List<String> favoriteGenres = new ArrayList<>();
    CheckBox cbAAction;
    CheckBox cbAClassics;
    CheckBox cbAComics;
    CheckBox cbADetective;
    CheckBox cbAFantasy;
    CheckBox cbAHistoricalFiction;
    CheckBox cbAHorror;
    CheckBox cbAFiction;
    CheckBox cbARomance;
    CheckBox cbASuspense;
    CheckBox cbASciFi;
    CheckBox cbAShortStories;
    CheckBox cbAWomen;
    CheckBox cbABiographies;
    CheckBox cbACookbooks;
    CheckBox cbAEssays;
    CheckBox cbAHistory;
    CheckBox cbAMemoir;
    CheckBox cbAPoetry;
    CheckBox cbASelfHelp;
    CheckBox cbATrueCrime;
    FloatingActionButton fabGoNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_on_author1);
        tvAuthorFavoriteGenres = findViewById(R.id.tvAuthorFavoriteGenres);
        cbAAction = findViewById(R.id.cbAAction);
        cbABiographies = findViewById(R.id.cbABiographies);
        cbAClassics = findViewById(R.id.cbAClassics);
        cbAComics = findViewById(R.id.cbAComics);
        cbACookbooks = findViewById(R.id.cbACookbooks);
        cbADetective = findViewById(R.id.cbADetective);
        cbAEssays = findViewById(R.id.cbAEssays);
        cbAFantasy = findViewById(R.id.cbAFantasy);
        cbAFiction = findViewById(R.id.cbAFiction);
        cbAHistoricalFiction = findViewById(R.id.cbAHistoricalFiction);
        cbAHorror = findViewById(R.id.cbAHorror);
        cbARomance = findViewById(R.id.cbARomance);
        cbASciFi = findViewById(R.id.cbASciFi);
        cbAShortStories = findViewById(R.id.cbAShortStories);
        cbAWomen = findViewById(R.id.cbAWomen);
        cbAHistory = findViewById(R.id.cbAHistory);
        cbAMemoir = findViewById(R.id.cbAMemoir);
        cbAPoetry = findViewById(R.id.cbAPoetry);
        cbASelfHelp = findViewById(R.id.cbASelfHelp);
        cbATrueCrime = findViewById(R.id.cbATrueCrime);
        cbASuspense = findViewById(R.id.cbASuspense);
        fabGoNext = findViewById(R.id.fabGoNext);
        fabGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavoriteGenres();
                goNext();
            }
        });
    }
    private void setFavoriteGenres() {
        //switch doesn't work :(
        if (cbAAction.isChecked()){
            favoriteGenres.add("Action and Adventure");
        }
        if (cbABiographies.isChecked()){
            favoriteGenres.add("Biographies");
        }
        if (cbAClassics.isChecked()){
            favoriteGenres.add("Classics");
        }
        if (cbAComics.isChecked()){
            favoriteGenres.add("Comics");
        }
        if (cbACookbooks.isChecked()){
            favoriteGenres.add("Cookbooks");
        }
        if (cbADetective.isChecked()){
            favoriteGenres.add("Detective");
        }
        if (cbAEssays.isChecked()){
            favoriteGenres.add("Essays");
        }
        if (cbAFantasy.isChecked()){
            favoriteGenres.add("Fantasy");
        }
        if (cbAFiction.isChecked()){
            favoriteGenres.add("Fiction");
        }
        if (cbAHistoricalFiction.isChecked()){
            favoriteGenres.add("Historical Fiction");
        }
        if (cbAHorror.isChecked()){
            favoriteGenres.add("Horror");
        }
        if (cbARomance.isChecked()){
            favoriteGenres.add("Romance");
        }
        if (cbASciFi.isChecked()){
            favoriteGenres.add("SciFi");
        }
        if (cbAShortStories.isChecked()){
            favoriteGenres.add("Short Stories");
        }
        if (cbAWomen.isChecked()){
            favoriteGenres.add("Women");
        }
        if (cbAHistory.isChecked()){
            favoriteGenres.add("History");
        }
        if (cbAMemoir.isChecked()){
            favoriteGenres.add("Memoir");
        }
        if (cbAPoetry.isChecked()){
            favoriteGenres.add("Poetry");
        }
        if (cbASelfHelp.isChecked()){
            favoriteGenres.add("Self Help");
        }
        if (cbATrueCrime.isChecked()){
            favoriteGenres.add("True Crime");
        }
        if (cbASuspense.isChecked()){
            favoriteGenres.add("Suspense");
        }



    }

    private void goNext(){
        Intent intent = new Intent(SignOnAuthorActivity1.this, SignOnAuthorActivity2.class);
        intent.putExtra("favoriteGenres", Parcels.wrap(favoriteGenres));
        startActivity(intent);

    }
}