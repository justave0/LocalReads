package com.example.localreads.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// @Parcel
@ParseClassName("Message")
public class Message extends ParseObject {

  public static final String KEY_TEXT = "Text";
  public static final String KEY_SENDER = "sender";

  public Message() {}

  public void setText(String text) {
    put(KEY_TEXT, text);
  }

  public String getText() {
    return getString(KEY_TEXT);
  }

  public void setSender(ParseUser user) {
    put(KEY_SENDER, user);
  }

  public ParseUser getSender() {
    return getParseUser(KEY_SENDER);
  }
}
