package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.localreads.DetailedMessageActivity;
import com.example.localreads.MainActivity;
import com.example.localreads.Models.Author;
import com.example.localreads.Models.Book;
import com.example.localreads.Models.MessageGroup;
import com.example.localreads.MoreBooksAdapter;
import com.example.localreads.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.transition.MaterialFadeThrough;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class DetailAuthorFragment extends Fragment {
  private static final int WRAP_CONTENT = -2;
  private Author mAuthor;
  MoreBooksAdapter adapter;
  FragmentActivity listener;
  Context context;
  RecyclerView rvDetailAuthorBooks;
  AppBarLayout ablMain;
  ImageView ivDetailAuthorPFP;
  TextView tvDetailAuthorReads;
  TextView tvDetailAuthorBio;
  TextView tvDetailAuthorName;
  Button btUserSettings;
  Button btChatWithAuthor;
  Button btAuthorAddBook;
  String TAG = "DetailAuthorFragment";
  private final ArrayList<Book> mMoreBooks = new ArrayList<>();
  boolean fromChat;

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
    mAuthor = Parcels.unwrap(getArguments().getParcelable("Author"));
    adapter = new MoreBooksAdapter(getActivity());
    ablMain = getActivity().findViewById(R.id.ablMain);
    setExitTransition(new MaterialFadeThrough());
    setReenterTransition(new MaterialFadeThrough());
    setEnterTransition(new MaterialFadeThrough());
    setExitTransition(new MaterialFadeThrough());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_detail_author, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Activity activity = getActivity();

    tvDetailAuthorName = activity.findViewById(R.id.tvProfileReaderName);
    tvDetailAuthorReads = activity.findViewById(R.id.tvProfileReaderReads);
    tvDetailAuthorBio = activity.findViewById(R.id.tvProfileReaderLocation);
    ivDetailAuthorPFP = activity.findViewById(R.id.ivProfileReaderPFP);
    btAuthorAddBook = activity.findViewById(R.id.btAuthorAddBook);
    btUserSettings = activity.findViewById(R.id.btReaderUserSettings);
    rvDetailAuthorBooks = activity.findViewById(R.id.rvProfileReaderBooks);
    btChatWithAuthor = activity.findViewById(R.id.btChatWithAuthor);
    populateData();

    btChatWithAuthor.setOnClickListener(
        v -> {
          Intent intent = new Intent(context, DetailedMessageActivity.class);
          ArrayList<ParseUser> users = new ArrayList<>();
          users.add(ParseUser.getCurrentUser());
          users.add(mAuthor.getUser());
          intent.putExtra("users", Parcels.wrap(users));

          ParseQuery<MessageGroup> messageQuery = generateMessageQuery(users);
          messageQuery.include(MessageGroup.KEY_MESSAGES);
          messageQuery.include("messages.sender");
          messageQuery.addDescendingOrder("messages.createdAt");
          try {
            MessageGroup mg = (MessageGroup) messageQuery.getFirst();
            intent.putExtra("messageGroup", Parcels.wrap(mg));
          } catch (ParseException e) {
            e.printStackTrace();
          }

          context.startActivity(intent);
          fromChat = true;
        });
  }

  private ParseQuery<MessageGroup> generateMessageQuery(ArrayList<ParseUser> users) {
    ArrayList<String> userIds = new ArrayList<>();
    for (int i = 0; i < users.size(); i++) {
      userIds.add(users.get(i).getObjectId());
    }
    ParseQuery<ParseUser> userQuery = new ParseQuery<>("_User");
    userQuery.whereContainedIn("objectId", userIds);
    ParseQuery<MessageGroup> messageGroupParseQuery = new ParseQuery<>(MessageGroup.class);
    messageGroupParseQuery.whereEqualTo("users", users.get(0));

    for (int i = 0; i < users.size(); i++) {
      messageGroupParseQuery =
          new ParseQuery<>(MessageGroup.class)
              .whereMatchesKeyInQuery("objectId", "objectId", messageGroupParseQuery)
              .whereEqualTo("users", users.get(i));
    }

    ParseQuery<MessageGroup> temp = new ParseQuery<>(MessageGroup.class);
    temp.whereMatchesKeyInQuery("users", "objectId", userQuery);
    temp.whereEqualTo(MessageGroup.KEY_COUNTER, users.size());
    return messageGroupParseQuery;
  }

  private void populateData() {
    tvDetailAuthorBio.setText(mAuthor.getBio());
    tvDetailAuthorReads.setText(mAuthor.getReads() + " Reads");
    tvDetailAuthorName.setText(mAuthor.getUser().getUsername());
    Glide.with(context)
        .load(mAuthor.getUser().getParseFile("profilePic").getUrl())
        .circleCrop()
        .into(ivDetailAuthorPFP);

    if (mAuthor.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
      btChatWithAuthor.setVisibility(View.GONE);
      btUserSettings.setVisibility(View.VISIBLE);
      btAuthorAddBook.setVisibility(View.VISIBLE);
      btAuthorAddBook.setOnClickListener(
          v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.createAction();
          });
    }

    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
    rvDetailAuthorBooks.setLayoutManager(gridLayoutManager);
    rvDetailAuthorBooks.setAdapter(adapter);
    rvDetailAuthorBooks.setOnFlingListener(null);
    queryMoreBooks();
  }

  private void queryMoreBooks() {
    if (mAuthor.getBooks() != null) {
      mAuthor.getBooks().size();
      ParseQuery<Book> queryBook = ParseQuery.getQuery("Book");
      queryBook.whereContainedIn("objectId", mAuthor.getBooks());
      queryBook.include(Book.KEY_USER);
      queryBook.include(Book.KEY_READ_BY);
      queryBook.findInBackground(
          (objects, e) -> {
            if (e == null) {
              mMoreBooks.addAll(objects);
              adapter.updateAdapter(mMoreBooks);
            } else {
              Log.e(TAG, e.toString());
            }
          });
    }
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
    if (adapter != null && !fromChat) {
      adapter.clear();
      mMoreBooks.clear();
      queryMoreBooks();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
