package com.example.localreads.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class GoogleBook {
  String title;
  ArrayList<String> genres = new ArrayList<>();
  String imageLink;
  ArrayList<String> authors = new ArrayList<>();
  String description;
  String bookId;
  Boolean embeddable;
  int averageRating;

  public GoogleBook() {}

  public GoogleBook(JSONObject jsonObject) {
    try {
      title = jsonObject.getJSONObject("volumeInfo").getString("title");
    } catch (JSONException e) {
      title = "No Title Given";
    }
    try {
      imageLink =
          jsonObject.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail");
    } catch (JSONException e) {
      imageLink =
          "http://books.google.com/books/content?id=6P_jN6zUuMcC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api";
    }
    imageLink = imageLink.substring(0, 4) + "s" + imageLink.substring(4);
    try {
      description = jsonObject.getJSONObject("volumeInfo").getString("description");
    } catch (JSONException e) {
      description = "No description given";
    }
    try {
      bookId = jsonObject.getString("id");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    try {
      embeddable = jsonObject.getJSONObject("accessInfo").getBoolean("embeddable");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    try {
      averageRating = jsonObject.getJSONObject("volumeInfo").getInt("averageRating");
    } catch (JSONException jsonException) {
      averageRating = 0;
    }
    populateLists(jsonObject);
  }

  public static ArrayList<GoogleBook> fromJsonArray(JSONArray bookJsonArray) throws JSONException {
    ArrayList<GoogleBook> books = new ArrayList<>();
    for (int i = 0; i < bookJsonArray.length(); i++) {
      books.add(new GoogleBook(bookJsonArray.getJSONObject(i)));
    }
    return books;
  }

  private void populateLists(JSONObject jsonObject) {
    JSONArray authorsList;
    try {
      authorsList = jsonObject.getJSONObject("volumeInfo").getJSONArray("authors");
      for (int i = 0; i < authorsList.length(); i++) {
        try {
          authors.add(authorsList.getString(i));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } catch (JSONException e) {
      authors.add("No authors given");
    }

    JSONArray genresList;
    try {
      genresList = jsonObject.getJSONObject("volumeInfo").getJSONArray("categories");
      for (int j = 0; j < genresList.length(); j++) {
        try {
          genres.add(genresList.getString(j));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } catch (JSONException e) {
      genres.add("No genres given");
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

  public String getBookId() {
    return bookId;
  }

  public Boolean getEmbeddable() {
    return embeddable;
  }

  public int getAverageRating() {
    return averageRating;
  }
}
