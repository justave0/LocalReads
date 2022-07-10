package com.example.localreads.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

//@Parcel
@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String KEY_TEXT = "Text";
    public static final String KEY_USERS = "users";

    public Message (){}

    public void setText(String text){
        put(KEY_TEXT, text);
    }
    public String getText(){
        return getString(KEY_TEXT);
    }

    public boolean userEquals(Message message) {
        if (message.getRelation(KEY_USERS).getQuery().equals(this.getRelation(KEY_USERS).getQuery())){
            return true;
        }
        else{
            return false;
        }
    }
}
