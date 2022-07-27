package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.MainActivity;
import com.example.localreads.Models.Book;
import com.example.localreads.MoreBooksAdapter;
import com.example.localreads.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.android.material.transition.MaterialSharedAxis;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ReaderProfileFragment extends Fragment {
  private static final int WRAP_CONTENT = -2;
  private ParseUser mReader;
  MoreBooksAdapter adapter;
  FragmentActivity listener;
  Context context;
  RecyclerView rvProfileReaderBooks;
  AppBarLayout ablMain;
  ImageView ivProfileReaderPFP;
  TextView tvProfileReaderReads;
  TextView tvProfileReaderLocation;
  TextView tvProfileReaderName;
  Button btReaderUserSettings;
  String TAG = "DetailAuthorFragment";
  private final ArrayList<Book> mMoreBooks = new ArrayList<>();
  Button btChatWithAuthor;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof Activity) {
      this.listener = (FragmentActivity) context;
      this.context = context;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReader = ParseUser.getCurrentUser();
    adapter = new MoreBooksAdapter(getActivity());
    ablMain = getActivity().findViewById(R.id.ablMain);
    setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_detail_author, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Activity activity = getActivity();
    tvProfileReaderName = activity.findViewById(R.id.tvProfileReaderName);
    tvProfileReaderReads = activity.findViewById(R.id.tvProfileReaderReads);
    tvProfileReaderLocation = activity.findViewById(R.id.tvProfileReaderLocation);
    ivProfileReaderPFP = activity.findViewById(R.id.ivProfileReaderPFP);
    btReaderUserSettings = activity.findViewById(R.id.btReaderUserSettings);
    btReaderUserSettings.setVisibility(View.VISIBLE);
    btChatWithAuthor = activity.findViewById(R.id.btChatWithAuthor);
    btChatWithAuthor.setVisibility(View.GONE);
    rvProfileReaderBooks = activity.findViewById(R.id.rvProfileReaderBooks);
    populateData();
  }

  private void populateData() {
    MainActivity activity = (MainActivity) getActivity();
    ParseGeoPoint latlng = mReader.getParseGeoPoint("location");
    tvProfileReaderLocation.setText(
        activity.getReverseGeocode(new LatLng(latlng.getLatitude(), latlng.getLongitude())));
    tvProfileReaderName.setText(mReader.getUsername());
    Glide.with(context)
        .load(mReader.getParseFile("profilePic").getUrl())
        .circleCrop()
        .into(ivProfileReaderPFP);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
    rvProfileReaderBooks.setLayoutManager(gridLayoutManager);
    rvProfileReaderBooks.setAdapter(adapter);
    rvProfileReaderBooks.setOnFlingListener(null);
    queryMoreBooks();
  }

  private void queryMoreBooks() {
    ParseQuery<Book> queryBook = ParseQuery.getQuery(Book.class);
    queryBook.whereEqualTo("readBy", ParseUser.getCurrentUser());
    queryBook.include(Book.KEY_USER);
    queryBook.findInBackground(
        (objects, e) -> {
          if (e == null) {
            MainActivity activity = (MainActivity) getActivity();
            if (getActivity() != null) {
              activity.spinner.setVisibility(View.GONE);
            }
            tvProfileReaderReads.setText(objects.size() + " Books Read");
            mMoreBooks.addAll(objects);
            adapter.updateAdapter(mMoreBooks);

          } else {
            Log.e(TAG, e.toString());
          }
        });
  }

  @Override
  public void onDetach() {
    super.onDetach();
    this.listener = null;
    ablMain.setVisibility(View.VISIBLE);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (adapter != null) {
      adapter.clear();
      mMoreBooks.clear();
    }
    if (ablMain != null) {
      CoordinatorLayout.LayoutParams params =
          (CoordinatorLayout.LayoutParams) ablMain.getLayoutParams();
      params.height = 0;
      ablMain.setLayoutParams(params);
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (ablMain != null) {
      CoordinatorLayout.LayoutParams params =
          (CoordinatorLayout.LayoutParams) ablMain.getLayoutParams();
      params.height = WRAP_CONTENT;
      ablMain.setLayoutParams(params);
    }
  }
}
