package com.example.localreads.Models;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class GoogleBook {
    String title;
    ArrayList<String> genres = new ArrayList<>();
    String imageLink;
    ArrayList<String> authors = new ArrayList<>();
    String description;

    public GoogleBook(){}

    public GoogleBook(JSONObject jsonObject)throws JSONException {
        title = jsonObject.getJSONObject("volumeInfo").getString("title");
        imageLink = jsonObject.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");
        imageLink = imageLink.substring(0,4) + "s" + imageLink.substring(4);
        description = jsonObject.getJSONObject("volumeInfo").getString("description");
        populateLists(jsonObject);
    }

    public static ArrayList<GoogleBook> fromJsonArray(JSONArray bookJsonArray) throws JSONException {
        ArrayList<GoogleBook> books = new ArrayList<>();
        for(int i = 0; i < bookJsonArray.length(); i++){
            books.add(new GoogleBook(bookJsonArray.getJSONObject(i)));
        }
        return books;
    }

    private void populateLists(JSONObject jsonObject) throws JSONException {
        JSONArray authorsList = jsonObject.getJSONObject("volumeInfo").getJSONArray("authors");
        for (int i = 0; i < authorsList.length(); i++){
            authors.add(authorsList.getString(i));
        }
        JSONArray genresList = jsonObject.getJSONObject("volumeInfo").getJSONArray("categories");
        for (int j = 0; j < genresList.length(); j++){
            genres.add(genresList.getString(j));
        }
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getImageLink() {
        return imageLink;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }
}
