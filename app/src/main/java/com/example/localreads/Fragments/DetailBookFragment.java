package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.localreads.GoogleBooksAdapter;
import com.example.localreads.Models.Author;
import com.example.localreads.Models.GoogleBook;
import com.example.localreads.MoreBooksAdapter;
import com.example.localreads.R;
import com.example.localreads.StartSnapHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.localreads.Models.Book;
import com.google.android.material.transition.MaterialFadeThrough;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class DetailBookFragment extends Fragment {
  MoreBooksAdapter moreBooksAdapter;
  GoogleBooksAdapter googleBooksAdapter;
  Context context;
  FragmentActivity listener;
  private Book mBook;
  TextView tvDetailBookTitle;
  TextView tvDetailBookAuthor;
  TextView tvDetailBookLocation;
  TextView tvDetailBookDescription;
  MaterialButton btReadBook;
  TextView tvDetailReads;
  AppBarLayout ablTopMenu;
  RecyclerView rvDetailMoreBooks;
  private final String TAG = "DetailBookFragment";
  Author mAuthor;
  public ArrayList<Book> authorBooks = new ArrayList<>();
  public ArrayList<GoogleBook> googleBooks = new ArrayList<>();
  Boolean check;
  RecyclerView rvDetailGoogleBooks;
  CollapsingToolbarLayout ctlMain;
  BottomNavigationView bottom_navigation;
  FrameLayout flTemp;

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
    mBook = Parcels.unwrap(getArguments().getParcelable("book"));
    ablTopMenu = getActivity().findViewById(R.id.ablMain);
    ablTopMenu.setEnabled(false);
    ctlMain = getActivity().findViewById(R.id.ctlMain);
    ctlMain.setVisibility(View.GONE);

    bottom_navigation = getActivity().findViewById(R.id.bottom_navigation);
    bottom_navigation.setVisibility(View.INVISIBLE);

    flTemp = getActivity().findViewById(R.id.flTemp);
    CoordinatorLayout.LayoutParams params =
        (CoordinatorLayout.LayoutParams) flTemp.getLayoutParams();
    params.setBehavior(null);

    moreBooksAdapter = new MoreBooksAdapter(getActivity());
    googleBooksAdapter = new GoogleBooksAdapter(getActivity());
    setExitTransition(new MaterialFadeThrough());
    setReenterTransition(new MaterialFadeThrough());
    setEnterTransition(new MaterialFadeThrough());
    setReturnTransition(new MaterialFadeThrough());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_detail_book, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Activity activity = getActivity();
    tvDetailBookAuthor = activity.findViewById(R.id.tvDetailBookAuthor);
    tvDetailBookTitle = activity.findViewById(R.id.tvDetailBookTitle);
    tvDetailBookDescription = activity.findViewById(R.id.tvDetailBookDescription);
    tvDetailBookLocation = activity.findViewById(R.id.tvDetailBookLocation);
    btReadBook = activity.findViewById(R.id.btReadBook);
    tvDetailReads = activity.findViewById(R.id.tvDetailReads);
    authorBooks.clear();

    LinearLayoutManager horizontalLinearLayoutManager1 =
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
    rvDetailMoreBooks = activity.findViewById(R.id.rvDetailMoreBooks);
    rvDetailMoreBooks.setLayoutManager(horizontalLinearLayoutManager1);
    rvDetailMoreBooks.setAdapter(moreBooksAdapter);
    rvDetailMoreBooks.setOnFlingListener(null);
    new StartSnapHelper().attachToRecyclerView(rvDetailMoreBooks);

    LinearLayoutManager horizontalLinearLayoutManager2 =
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
    rvDetailGoogleBooks = activity.findViewById(R.id.rvDetailGoogleBooks);
    rvDetailGoogleBooks.setLayoutManager(horizontalLinearLayoutManager2);
    rvDetailGoogleBooks.setAdapter(googleBooksAdapter);
    rvDetailGoogleBooks.setOnFlingListener(null);
    new StartSnapHelper().attachToRecyclerView(rvDetailGoogleBooks);

    tvDetailBookAuthor.setText(mBook.getUser().getUsername());
    tvDetailBookLocation.setText(mBook.getLocationString());
    tvDetailBookTitle.setText(mBook.getName());
    tvDetailBookDescription.setText(mBook.getDescription());
    tvDetailReads.setText(String.valueOf(mBook.getReads()));

    checkReadPost();
    queryMoreBooks();
    btReadBook.setOnClickListener(
        v -> {
          // remove like
          if (check) {
            removeRead();
            AnimatedVectorDrawableCompat unreadPostAnimation =
                AnimatedVectorDrawableCompat.create(context, R.drawable.unread_post_animation);
            btReadBook.setIcon(unreadPostAnimation);
            unreadPostAnimation.start();
            check = false;
          }
          // add like
          else {
            addRead();
            AnimatedVectorDrawableCompat addReadPostAnimation =
                AnimatedVectorDrawableCompat.create(context, R.drawable.read_post_animation);
            btReadBook.setIcon(addReadPostAnimation);
            addReadPostAnimation.start();
            check = true;
          }
          tvDetailReads.setText(String.valueOf(mBook.getReads()));
        });
  }

  private void queryGoogleBooks() {
    AsyncHttpClient client = new AsyncHttpClient();
    // create string for book genres
    StringBuilder genres = new StringBuilder("+subject:");
    for (int i = 0; i < mBook.getGenres().size(); i++) {
      genres.append(mBook.getGenres().get(i)).append("|");
    }
    genres = new StringBuilder(genres.substring(0, genres.length() - 1));
    genres = new StringBuilder(genres.toString().replaceAll("\\s+", "+"));
    // book name
    String title = mBook.getName();

    String bookQueryUrl =
        "https://www.googleapis.com/books/v1/volumes?q="
            + title
            + genres
            + "&key="
            + getString(R.string.google_maps_api_key);
    client.get(
        bookQueryUrl,
        new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, String.valueOf(statusCode));
            JSONObject jsonObject = json.jsonObject;
            try {
              if (jsonObject.getInt("totalItems") == 0) {
                widenGoogleBooksQuery();
              } else {
                populateGoogleBooksRecycler(jsonObject);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }

          @Override
          public void onFailure(
              int statusCode, Headers headers, String response, Throwable throwable) {
            Log.e(TAG, String.valueOf(statusCode));
          }
        });
  }

  // removes the title in the recommended google book search
  private void widenGoogleBooksQuery() {
    AsyncHttpClient client = new AsyncHttpClient();
    // create string for book genres
    StringBuilder genres = new StringBuilder("+subject:");
    for (int i = 0; i < mBook.getGenres().size(); i++) {
      genres.append(mBook.getGenres().get(i)).append("|");
    }
    genres = new StringBuilder(genres.substring(0, genres.length() - 1));
    genres = new StringBuilder(genres.toString().replaceAll("\\s+", "+"));
    // book name
    String bookQueryUrl =
        "https://www.googleapis.com/books/v1/volumes?q="
            + genres
            + "&key="
            + getString(R.string.google_maps_api_key);
    client.get(
        bookQueryUrl,
        new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, String.valueOf(statusCode));
            JSONObject jsonObject = json.jsonObject;
            try {
              if (jsonObject.getInt("totalItems") == 0) {
                Log.i(TAG, "ran out of google books");
              } else {
                populateGoogleBooksRecycler(jsonObject);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }

          @Override
          public void onFailure(
              int statusCode, Headers headers, String response, Throwable throwable) {
            Log.e(TAG, String.valueOf(statusCode));
          }
        });
  }

  private void populateGoogleBooksRecycler(JSONObject jsonObject) throws JSONException {
    // make new books from json
    JSONArray items = jsonObject.getJSONArray("items");
    googleBooks.addAll(GoogleBook.fromJsonArray(items));
    googleBooksAdapter.updateAdapter(googleBooks);
  }

  private void queryMoreBooks() {
    ParseQuery<Author> query = ParseQuery.getQuery("Author");
    query.include(Author.KEY_USER);
    query.whereEqualTo("books", mBook.getObjectId());
    query.findInBackground(
        (objects, e) -> {
          if (e == null) {
            mAuthor = objects.get(0);
            List<String> authorBooksID = objects.get(0).getBooks();
            ParseQuery<Book> queryBook = ParseQuery.getQuery("Book");
            queryBook.whereContainedIn("objectId", authorBooksID);
            queryBook.include(Book.KEY_USER);
            queryBook.findInBackground(
                (objects1, e1) -> {
                  if (e1 == null) {
                    authorBooks.addAll(objects1);
                    authorBooks.removeIf(n -> (n.getObjectId().equals(mBook.getObjectId())));
                    moreBooksAdapter.updateAdapter(authorBooks);
                    Log.i(TAG, authorBooks.toString());
                  } else {
                    Log.e(TAG, e1.toString());
                  }
                });
          } else {
            Log.e(TAG, e.toString());
          }
        });
  }

  private void addRead() {
    // UPDATE like counter
    mBook.addRead();
    mBook.getRelation("readBy").add(ParseUser.getCurrentUser());
    mBook.saveInBackground(
        e -> {
          if (e == null) {
            Log.i(TAG, "read updated");
          } else {
            Log.e(TAG, "Error: " + e);
          }
        });
    mAuthor.addRead();
    mAuthor.saveInBackground(
        e -> {
          if (e == null) {
            Log.i(TAG, "read updated");
          } else {
            Log.e(TAG, "Error: " + e);
          }
        });
  }

  private void removeRead() {
    mBook.removeRead();
    mBook.getRelation("readBy").remove(ParseUser.getCurrentUser());
    mBook.saveInBackground(
        e -> {
          if (e == null) {
            Log.i(TAG, "read updated");
          } else {
            Log.e(TAG, "Error: " + e);
          }
        });
    mAuthor.removeRead();
    mAuthor.saveInBackground(
        e -> {
          if (e == null) {
            Log.i(TAG, "read updated");
          } else {
            Log.e(TAG, "Error: " + e);
          }
        });
  }

  private void checkReadPost() {
    ParseQuery newQuery = mBook.getRelation("readBy").getQuery();
    newQuery.findInBackground(
        (FindCallback<ParseUser>)
            (users, e) -> {
              ParseUser user =
                  users.stream()
                      .filter(
                          temp ->
                              (temp.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())))
                      .findAny()
                      .orElse(null);
              if (e == null) {
                if (user != null) {
                  btReadBook.setIcon(
                      AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark));
                  check = true;
                } else {
                  btReadBook.setIcon(
                      AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark_outline));
                  check = false;
                }
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
    ctlMain.setVisibility(View.GONE);
    bottom_navigation.setVisibility(View.INVISIBLE);
    CoordinatorLayout.LayoutParams params =
        (CoordinatorLayout.LayoutParams) flTemp.getLayoutParams();
    params.setBehavior(null);
    if (googleBooksAdapter != null) {
      googleBooksAdapter.clear();
      googleBooks.clear();
      queryGoogleBooks();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    this.listener = null;
    bottom_navigation.setVisibility(View.VISIBLE);
    ctlMain.setVisibility(View.VISIBLE);
    AppBarLayout.ScrollingViewBehavior viewBehavior = new AppBarLayout.ScrollingViewBehavior();
    CoordinatorLayout.LayoutParams params =
        (CoordinatorLayout.LayoutParams) flTemp.getLayoutParams();
    params.setBehavior(viewBehavior);
  }
}
