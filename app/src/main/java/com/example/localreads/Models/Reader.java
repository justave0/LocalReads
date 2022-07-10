package com.example.localreads.Models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

//@Parcel
@ParseClassName("Reader")
public class Reader extends ParseObject {
    public static final String KEY_FAVORITE_GENRES = "favoriteGenres";
    public static final String KEY_USER = "user";
    public static final String KEY_READ_BOOK = "readBook";

    public Reader(){}

    public void setFavoriteGenres (List<String> favoriteGenres){
        put(KEY_FAVORITE_GENRES, favoriteGenres);
    }
    public List<String> getFavoriteGenres (){
        return getList(KEY_FAVORITE_GENRES);
    }

    public void setUser(ParseUser user){put(KEY_USER, user);}
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setReadBooks (ParseRelation<Book> readBooks){
        put(KEY_READ_BOOK, readBooks);
    }
    public ParseRelation<Book> getReadBooks(){
        return getRelation(KEY_READ_BOOK);
    }
}
