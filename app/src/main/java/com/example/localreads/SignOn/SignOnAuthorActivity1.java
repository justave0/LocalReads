package com.example.localreads.SignOn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        cbAAction = findViewById(R.id.cbGAction);
        cbABiographies = findViewById(R.id.cbGBiographies);
        cbAClassics = findViewById(R.id.cbGClassics);
        cbAComics = findViewById(R.id.cbGComics);
        cbACookbooks = findViewById(R.id.cbGCookbooks);
        cbADetective = findViewById(R.id.cbGDetective);
        cbAEssays = findViewById(R.id.cbGEssays);
        cbAFantasy = findViewById(R.id.cbGFantasy);
        cbAFiction = findViewById(R.id.cbGFiction);
        cbAHistoricalFiction = findViewById(R.id.cbGHistoricalFiction);
        cbAHorror = findViewById(R.id.cbGHorror);
        cbARomance = findViewById(R.id.cbGRomance);
        cbASciFi = findViewById(R.id.cbGSciFi);
        cbAShortStories = findViewById(R.id.cbGShortStories);
        cbAWomen = findViewById(R.id.cbGWomen);
        cbAHistory = findViewById(R.id.cbGHistory);
        cbAMemoir = findViewById(R.id.cbGMemoir);
        cbAPoetry = findViewById(R.id.cbGPoetry);
        cbASelfHelp = findViewById(R.id.cbGSelfHelp);
        cbATrueCrime = findViewById(R.id.cbGTrueCrime);
        cbASuspense = findViewById(R.id.cbGSuspense);
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