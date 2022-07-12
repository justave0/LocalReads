package com.example.localreads.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

//@Parcel
@ParseClassName("MessageGroup")
public class MessageGroup extends ParseObject {

    public static final String KEY_RECENT_TEXT = "recentText";
    public static final String KEY_USERS = "users";
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_TIME_STAMP = "timeStamp";

    public MessageGroup(){}

    public void setRecentText(String text){
        put(KEY_RECENT_TEXT, text);
    }
    public String getRecentText(){
        return getString(KEY_RECENT_TEXT);
    }

    public void setUsers(ParseRelation relation){
        put(KEY_USERS, relation);
    }
    public ParseRelation getUsers(){
        return getRelation(KEY_USERS);
    }

    public void setMessages(List<Message> messages){
        put(KEY_MESSAGES, messages);
    }
    public List<Message> getMessages(){
        return getList(KEY_MESSAGES);
    }

    public void setTimeStamp(Date date){
        put(KEY_TIME_STAMP, date);
    }
    public Date getTimeStamp(){
        return getDate(KEY_TIME_STAMP);
    }
}
