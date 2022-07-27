package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localreads.BookAdapter;
import com.example.localreads.MainActivity;
import com.example.localreads.Models.Author;
import com.example.localreads.Models.Book;
import com.example.localreads.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.transition.MaterialFadeThrough;
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
  TextView tvTitleText;
  ChipGroup cgTopMenu;
  CollapsingToolbarLayout ctlMain;
  BottomNavigationView bottom_navigation;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof Activity) {
      this.listener = (FragmentActivity) context;
    }
    this.context = context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ArrayList<Book> books = new ArrayList<Book>();
    adapter = new BookAdapter(books, getActivity());
    ctlMain = getActivity().findViewById(R.id.ctlMain);
    bottom_navigation = getActivity().findViewById(R.id.bottom_navigation);

    setExitTransition(new MaterialFadeThrough());
    setReenterTransition(new MaterialFadeThrough());
    setEnterTransition(new MaterialFadeThrough());
    setReturnTransition(new MaterialFadeThrough());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_book, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    searchRadius = 30.0;
    RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvBookFeed);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    rv.setAdapter(adapter);
    rv.setLayoutManager(linearLayoutManager);
    tvTitleText = getActivity().findViewById(R.id.tvTitleText);
    tvTitleText.setText("Showing New Releases Near: " + ((MainActivity) getActivity()).address);
    cgTopMenu = getActivity().findViewById(R.id.cgTopMenu);
    cgTopMenu.setVisibility(View.VISIBLE);
  }

  public void queryAuthors() {
    ArrayList<String> authorID = new ArrayList<>();
    ParseQuery<Author> authorQuery = ParseQuery.getQuery(Author.class);
    authorQuery.whereWithinMiles(
        "inputLocation", ParseUser.getCurrentUser().getParseGeoPoint("location"), searchRadius);
    authorQuery.setLimit(20);
    authorQuery.findInBackground(
        (objects, e) -> {
          if (e == null) {
            for (int i = 0; i < objects.size(); i++) {
              authorID.add(objects.get(i).getUser().getObjectId());
            }
            queryBooks(authorID);
          } else {
            Log.e("author query failed - ", "Error: " + e.getMessage());
          }
        });
  }

  public void queryBooks(ArrayList<String> authorID) {
    ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
    query.include(Book.KEY_USER);
    query.include(Book.KEY_READ_BY);
    query.addDescendingOrder("createdAt");
    query.whereContainedIn("user", authorID);
    if (selectedGenres != null) {
      query.whereContainedIn("genres", selectedGenres);
    }
    query.setLimit(20);
    // Specify the object id
    query.findInBackground(
        (objects, e) -> {
          if (e == null) {
            MainActivity activity = (MainActivity) getActivity();
            if (getActivity() != null) {
              activity.spinner.setVisibility(View.GONE);
            }
            // Access the array of results here
            adapter.clear();
            books.addAll(objects);
            adapter.updateAdapter(books);
          } else {
            Log.e("item", "Error: " + e.getMessage());
          }
        });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    this.listener = null;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (adapter != null) {
      adapter.clear();
      books.clear();
      queryAuthors();
    }
    ctlMain.setVisibility(View.VISIBLE);
    ctlMain.setVisibility(View.VISIBLE);
  }
}
