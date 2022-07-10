package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;

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


//        ParseCloud.callFunctionInBackground("getDistinctUsers", new HashMap<>(), new FunctionCallback<List<Message>>() {
//            @Override
//            public void done(List<Message> objects, ParseException e) {
//                Log.i("heelo", objects.toString());
//            }
//        });
    }

    private ArrayList<String> doSomething(Message message) {
        ParseRelation relation = message.getRelation(Message.KEY_USERS);
        //relation.add(ParseUser.getCurrentUser());
        ParseQuery pq = relation.getQuery();
        try {
            ArrayList<ParseUser> users = (ArrayList<ParseUser>) pq.find();
            ArrayList<String> tempIds = new ArrayList<>();
            for (int i = 0; i < users.size(); i++){
                tempIds.add(users.get(i).getObjectId());
            }
            Log.i(TAG, tempIds.toString());
            return tempIds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}