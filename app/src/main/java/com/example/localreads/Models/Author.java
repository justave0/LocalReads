package com.example.localreads.Models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

//@Parcel
@ParseClassName("Author")

public class Author extends ParseObject {

    public static final String KEY_BOOKS = "books";
    public static final String KEY_BIO = "bio";
    public static final String KEY_LINK = "link";
    public static final String KEY_READS = "reads";
    public static final String KEY_INPUT_LOCATION = "inputLocation";
    public static final String KEY_USER = "user";
    public static final String KEY_FAVORITE_GENRES = "favoriteGenres";

    public Author () {}

    public void setBooks (List<String> books){
        put(KEY_BOOKS, books);
    }
    public void addBooks (Book book){
        List<String> userBooks = getBooks();
        if (userBooks != null){
            userBooks.add(book.getObjectId());
        }
    }
    public List<String> getBooks (){
        return getList(KEY_BOOKS);
    }

    public void setBio (String bio){
        put(KEY_BIO, bio);
    }
    public String getBio (){
        return getString(KEY_BIO);
    }


    public void setLink (String link){
        put(KEY_LINK, link);
    }
    public String getLink (){
        return getString(KEY_LINK);
    }

    public void setReads (int reads){
        put(KEY_READS, reads);
    }
    public int getReads (){
        return getInt(KEY_READS);
    }

    public void setInputLocation (ParseGeoPoint inputLocation){
        put(KEY_INPUT_LOCATION, inputLocation);
    }
    public ParseGeoPoint getInputLocation (){
        return getParseGeoPoint(KEY_INPUT_LOCATION);
    }

    public void setUser(ParseUser user){put(KEY_USER, user);}

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }


    public void setFavoriteGenres (List<String> favoriteGenres){
        put(KEY_FAVORITE_GENRES, favoriteGenres);
    }
    public List<String> getFavoriteGenres (){
        return getList(KEY_FAVORITE_GENRES);
    }


}
