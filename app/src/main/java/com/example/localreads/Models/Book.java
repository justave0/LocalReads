package com.example.localreads.Models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

//@Parcel
@ParseClassName("Book")
public class Book extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LINK = "link";
    public static final String KEY_READS = "reads";
    public static final String KEY_NAME = "name";
    public static final String KEY_USER = "user";
    public static final String KEY_GENRES = "genres";
    public static final String KEY_IMAGE = "image";

    public Book (){}

    public void setDescription(String description){put(KEY_DESCRIPTION, description);}
    public String getDescription(){return getString(KEY_DESCRIPTION);}

    public void setLink(String link){put(KEY_LINK, link);}
    public String getLink(){return getString(KEY_LINK);}

    public void setReads(int reads){put(KEY_READS, reads);}
    public int getReads(){return getInt(KEY_READS);}

    public void setName(String name){put(KEY_NAME, name);}
    public String getName(){return getString(KEY_NAME);}

    public void setUser(ParseUser user){put(KEY_USER, user);}
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setGenres(List<String> genres){put(KEY_GENRES, genres);}
    public List<String> getGenres(){
        return getList(KEY_GENRES);
    }

    public void setImage(ParseFile image){put(KEY_IMAGE, image);}
    public ParseFile getImage(){return getParseFile(KEY_IMAGE);}


}
