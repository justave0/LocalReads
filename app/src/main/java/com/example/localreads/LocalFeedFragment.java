package com.example.localreads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localreads.Models.Author;
import com.example.localreads.Models.Book;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LocalFeedFragment extends Fragment {
    BookAdapter adapter;
    FragmentActivity listener;
    public ArrayList<Book> books = new ArrayList<>();
    Context context;
    public Double searchRadius;
    public List<String> selectedGenres;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
        this.context = context;
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Book> books = new ArrayList<Book>();
        adapter = new BookAdapter(books, getActivity());
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRadius = Double.valueOf(30);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvBookFeed);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv.setAdapter(adapter);
        rv.setLayoutManager(linearLayoutManager);

        queryAuthors();
    }

    public void queryAuthors() {
        ArrayList<String> authorID = new ArrayList<>();
        ParseQuery<Author> authorQuery = ParseQuery.getQuery(Author.class);
        authorQuery.whereWithinMiles("inputLocation", ParseUser.getCurrentUser().getParseGeoPoint("location"), searchRadius);
        authorQuery.setLimit(20);
        authorQuery.findInBackground(new FindCallback<Author>() {
            @Override
            public void done(List<Author> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        authorID.add(objects.get(i).getUser().getObjectId());

                    }
                    queryBooks(authorID);
                } else {
                    Log.e("author query failed - ", "Error: " + e.getMessage());
                }
            }
        });
    }


        public void queryBooks(ArrayList <String> authorID){
        ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
        query.include(Book.KEY_USER);
        query.addDescendingOrder("createdAt");
        query.whereContainedIn("user", authorID);
        if (selectedGenres != null){
            query.whereContainedIn("genres", selectedGenres);
        }
        query.setLimit(20);
        // Specify the object id
        query.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> objects, com.parse.ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    books.addAll(objects);
                    adapter.updateAdapter(books);
                } else {
                    Log.e("item", "Error: " + e.getMessage());
                }
            }

        });
    }


    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;

    }

    @Override
    public void onResume() {

        super.onResume();
        if(adapter != null) {
            adapter.clear();
        }
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public double toKilometers(Double miles){
        return miles * 1.609;
    }
}
