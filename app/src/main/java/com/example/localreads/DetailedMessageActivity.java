package com.example.localreads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.localreads.Models.Message;
import com.example.localreads.Models.MessageGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DetailedMessageActivity extends AppCompatActivity {

  public static final String TAG = "DetailedMessageActivity";
  private MessageGroup mMessageGroup;
  RecyclerView rvDetailedMessageFeed;
  private ArrayList<ParseUser> mUsers;
  private ArrayList<Message> mMessages;
  public DetailMessageAdapter messageAdapter;
  TextInputLayout tlSendMessage;
  TextInputEditText etSendMessage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detailed_message);

    mMessages = new ArrayList<>();
    mMessageGroup = Parcels.unwrap(getIntent().getParcelableExtra("messageGroup"));
    mUsers = Parcels.unwrap(getIntent().getParcelableExtra("users"));

    tlSendMessage = findViewById(R.id.tlSendMessage);
    etSendMessage = findViewById(R.id.etSendMessage);

    messageAdapter =
        new DetailMessageAdapter(this, mMessages, ParseUser.getCurrentUser().getObjectId());
    rvDetailedMessageFeed = findViewById(R.id.rvDetailedMessageFeed);
    LinearLayoutManager linearLayoutManager =
        new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
    rvDetailedMessageFeed.setLayoutManager(linearLayoutManager);
    rvDetailedMessageFeed.setAdapter(messageAdapter);

    loadMessages();

    tlSendMessage.setEndIconOnClickListener(v -> sendMessage());
  }

  private void sendMessage() {
    if (etSendMessage.getText().toString().length() <= 0) {
      Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
      return;
    }
    String text = etSendMessage.getText().toString();
    Message message = new Message();
    message.setText(text);
    message.setSender(ParseUser.getCurrentUser());
    if (mMessageGroup == null) {
      createNewMessageGroup(text, message);
    } else {
      updateMessageGroup(text, message);
    }
    message.saveInBackground(
        e -> {
          if (e == null) {
            updateSentMessage(message);
          } else {
            Toast.makeText(
                    DetailedMessageActivity.this, "Error Sending Message", Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  private void updateMessageGroup(String text, Message message) {
    ArrayList<Message> messages = (ArrayList<Message>) mMessageGroup.getMessages();
    messages.add(message);
    mMessageGroup.setMessages(messages);
    mMessageGroup.setTimeStamp(Calendar.getInstance().getTime());
    mMessageGroup.setRecentText(text);
    mMessageGroup.saveInBackground();
  }

  private void createNewMessageGroup(String text, Message message) {
    mMessageGroup = new MessageGroup();
    ArrayList<Message> messages = new ArrayList<>();
    messages.add(message);
    mMessageGroup.setMessages(messages);
    mMessageGroup.setTimeStamp(Calendar.getInstance().getTime());
    mMessageGroup.setCounter(mUsers.size());
    ParseRelation relation = mMessageGroup.getRelation(MessageGroup.KEY_USERS);
    for (int i = 0; i < mUsers.size(); i++) {
      relation.add(mUsers.get(i));
    }
    mMessageGroup.setRecentText(text);
    mMessageGroup.saveInBackground();
  }

  private void updateSentMessage(Message message) {
    etSendMessage.setText("");
    mMessages.add(0, message);
    messageAdapter.notifyDataSetChanged();
  }

  private void loadMessages() {
    if (mMessageGroup != null && mMessageGroup.getMessages() != null) {
      mMessages.addAll(mMessageGroup.getMessages());
      Collections.reverse(mMessages);
      messageAdapter.notifyDataSetChanged();
    }
  }
}
