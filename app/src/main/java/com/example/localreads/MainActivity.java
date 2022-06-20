package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    public String userTag;
    private final String TAG = "MainActivity";
    Menu topMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserTag();
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
                    if (userTag.equals("reader")){
                        getMenuInflater().inflate(R.menu.reader_top_menu, topMenu);
                    }
                    else{
                        getMenuInflater().inflate(R.menu.author_top_menu, topMenu);
                    }
                } else {
                    Log.e(TAG, "Parse Error: " + e.toString());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the reader menu first. This will get changed in get getUserTag() accordingly
        topMenu = menu;
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }

    public void onChatAction(MenuItem mi){
    }

    public void onCreateAction(MenuItem mi){

    }

}