package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.localreads.Models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MessageFeedActivity extends AppCompatActivity {

    public static String [] userList = new String[]{};
    AutoCompleteTextView autotextView;
    ArrayAdapter<String> arrayAdapter;
    TextView tvMessageUsername;
    CondensedMessageAdapter messageAdapter;
    ArrayList<Message> messages;
    String TAG = "MessageFeedActivity";
    HashSet<ArrayList<String>> userIds = new HashSet<>();
    RecyclerView rvCondensedMessageFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_feed);

        // autocomplete search users
//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, userList);
        autotextView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteUserSearch);
        tvMessageUsername = findViewById(R.id.tvMessageUsername);
        tvMessageUsername.setText(ParseUser.getCurrentUser().getUsername());


        messages=  new ArrayList<>();
        messageAdapter = new CondensedMessageAdapter(messages, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);

        rvCondensedMessageFeed = findViewById(R.id.rvCondensedMessageFeed);
        rvCondensedMessageFeed.setLayoutManager(linearLayoutManager);
        rvCondensedMessageFeed.setAdapter(messageAdapter);


        queryMessages();
    }

    private void queryMessages() {
        ParseQuery messageQuery = new ParseQuery(Message.class);
        messageQuery.setLimit(100);
        messageQuery.whereEqualTo(Message.KEY_USERS, ParseUser.getCurrentUser());
        messageQuery.include(Message.KEY_USERS);
        messageQuery.addDescendingOrder("createdAt");

        messageQuery.findInBackground(new FindCallback<Message>(){
            @Override
            public void done(List<Message> objects, ParseException e) {

//                messages.addAll(objects);
                for(int i = 0; i < objects.size(); i++){
                    ArrayList<String> help = doSomething(objects.get(i));
                    if (userIds.add(help)){
                        messages.add(objects.get(i));
                    }
//                    userIds.add(help);
                }
                Log.i(TAG, messages.toString());
                //ArrayList<ParseRelation> distinctUsers = (ArrayList<ParseRelation>) messageUsers.stream().distinct().collect(Collectors.toList());
                messageAdapter.notifyDataSetChanged();
            }
        });

        autotextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private ArrayList<String> doSomething(Message message) {
        ArrayList<String> tempIds = new ArrayList<>();
        ParseRelation relation = message.getRelation(Message.KEY_USERS);
        ParseQuery pq = relation.getQuery();
        pq.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null){

                    for (int i = 0; i < objects.size(); i++){
                        tempIds.add(objects.get(i).getObjectId());
                    }
                    Log.i(TAG, tempIds.toString());
                }
                else{
                    e.printStackTrace();
                }
            }
        });
        return tempIds;
    }
}