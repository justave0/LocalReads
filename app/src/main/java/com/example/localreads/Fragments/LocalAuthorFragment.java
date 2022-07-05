package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localreads.AuthorAdapter;
import com.example.localreads.MainActivity;
import com.example.localreads.Models.Author;
import com.example.localreads.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.ChipGroup;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LocalAuthorFragment extends Fragment {
    AuthorAdapter adapter;
    FragmentActivity listener;
    Context context;
    public Double searchRadius;
    AppBarLayout ablTopMenu;
    String TAG = "Local Author Fragment";
    TextView tvTitleText;
    ChipGroup cgTopMenu;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Author> authors = new ArrayList<Author>();
        adapter = new AuthorAdapter(getActivity(), authors);
        ablTopMenu = getActivity().findViewById(R.id.ablMain);
        ablTopMenu.setExpanded(true);
        cgTopMenu = getActivity().findViewById(R.id.cgTopMenu);
        cgTopMenu.setVisibility(View.GONE);
        tvTitleText = getActivity().findViewById(R.id.tvTitleText);
        tvTitleText.setText("Showing Popular Authors Near: " + ((MainActivity) getActivity()).address);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_author_feed, parent, false);
        return inflater.inflate(R.layout.fragment_author_feed, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchRadius = Double.valueOf(30);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvAuthorFeed);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv.setAdapter(adapter);
        rv.setLayoutManager(linearLayoutManager);
        queryAuthors();
    }

    public void queryAuthors() {
        ArrayList<Author> authors = new ArrayList<>();
        ParseQuery<Author> authorQuery = ParseQuery.getQuery(Author.class);
        authorQuery.whereWithinMiles("inputLocation", ParseUser.getCurrentUser().getParseGeoPoint("location"), searchRadius);
        authorQuery.setLimit(20);
        authorQuery.include(Author.KEY_USER);
        authorQuery.orderByDescending(Author.KEY_READS);
        authorQuery.findInBackground(new FindCallback<Author>() {
            @Override
            public void done(List<Author> objects, ParseException e) {
                if (e == null) {
                    adapter.clear();
                    authors.addAll(objects);
                    adapter.updateAdapter(authors);
                    Log.i(TAG, "debug");
                } else {
                    Log.e("author query failed - ", "Error: " + e.getMessage());
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

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
