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

import com.example.localreads.AuthorAdapter;
import com.example.localreads.MainActivity;
import com.example.localreads.Models.Author;
import com.example.localreads.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

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
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    this.context = context;
    if (context instanceof Activity) {
      this.listener = (FragmentActivity) context;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ArrayList<Author> authors = new ArrayList<>();
    adapter = new AuthorAdapter(getActivity(), authors);
    ablTopMenu = getActivity().findViewById(R.id.ablMain);
    ablTopMenu.setExpanded(true);
    cgTopMenu = getActivity().findViewById(R.id.cgTopMenu);
    cgTopMenu.setVisibility(View.GONE);
    tvTitleText = getActivity().findViewById(R.id.tvTitleText);
    tvTitleText.setText("Showing Popular Authors Near: " + ((MainActivity) getActivity()).address);

    setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    // return inflater.inflate(R.layout.fragment_author_feed, parent, false);
    return inflater.inflate(R.layout.fragment_author_feed, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    searchRadius = 30.0;
    RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvAuthorFeed);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    rv.setAdapter(adapter);
    rv.setLayoutManager(linearLayoutManager);
    queryAuthors();
  }

  public void queryAuthors() {
    ArrayList<Author> authors = new ArrayList<>();
    ParseQuery<Author> authorQuery = ParseQuery.getQuery(Author.class);
    authorQuery.whereWithinMiles(
        "inputLocation", ParseUser.getCurrentUser().getParseGeoPoint("location"), searchRadius);
    authorQuery.setLimit(20);
    authorQuery.include(Author.KEY_USER);
    authorQuery.orderByDescending(Author.KEY_READS);
    authorQuery.findInBackground(
        (objects, e) -> {
          if (e == null) {
            MainActivity activity = (MainActivity) getActivity();
            if (getActivity() != null) {
              activity.spinner.setVisibility(View.GONE);
            }
            adapter.clear();
            authors.addAll(objects);
            adapter.updateAdapter(authors);
            Log.i(TAG, "debug");
          } else {
            Log.e("author query failed - ", "Error: " + e.getMessage());
          }
        });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    this.listener = null;
  }
}
