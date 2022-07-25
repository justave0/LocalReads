package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.localreads.Models.MessageGroup;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;


public class MessageFeedActivity extends AppCompatActivity {

  public static String[] authorList = new String[] {};
  AutoCompleteTextView autotextView;
  TextView tvMessageUsername;
  CondensedMessageAdapter messageAdapter;
  ArrayList<MessageGroup> messages;
  String TAG = "MessageFeedActivity";
  RecyclerView rvCondensedMessageFeed;
  public ArrayAdapter<String> authorAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message_feed);

    // autocomplete search users
    authorAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, authorList);
    autotextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteUserSearch);
    autotextView.setThreshold(3);
    autotextView.setAdapter(authorAdapter);

    tvMessageUsername = findViewById(R.id.tvMessageUsername);
    tvMessageUsername.setText(ParseUser.getCurrentUser().getUsername());

    messages = new ArrayList<>();
    messageAdapter = new CondensedMessageAdapter(messages, this);
    LinearLayoutManager linearLayoutManager =
        new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

    rvCondensedMessageFeed = findViewById(R.id.rvCondensedMessageFeed);
    rvCondensedMessageFeed.setLayoutManager(linearLayoutManager);
    rvCondensedMessageFeed.setAdapter(messageAdapter);

    queryMessages();
  }

  private void queryMessages() {
    ParseQuery messageQuery = new ParseQuery(MessageGroup.class);
    messageQuery.setLimit(100);
    messageQuery.whereEqualTo(MessageGroup.KEY_USERS, ParseUser.getCurrentUser());
    messageQuery.include(MessageGroup.KEY_MESSAGES);
    messageQuery.include("messages.sender");
    messageQuery.addDescendingOrder("createdAt");
    messageQuery.findInBackground(
            (FindCallback<MessageGroup>) (objects, e) -> {
              messages.addAll(objects);
              messageAdapter.notifyDataSetChanged();
            });

    autotextView.setOnClickListener(
            v -> {});
  }
}
