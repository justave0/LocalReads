package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.localreads.GoogleBookReaderActivity;
import com.example.localreads.Models.GoogleBook;
import com.example.localreads.R;
import com.google.android.material.transition.MaterialFadeThrough;

import org.parceler.Parcels;

public class GoogleBookDetailFragment extends Fragment {
  FragmentActivity listener;
  private GoogleBook mBook;
  TextView tvGoogleBookDetailTitle;
  TextView tvGoogleBookDetailAuthor;
  TextView tvGoogleBookDetailDescription;
  Button btGoogleBookDetailReadBook;
  RatingBar googleBookDetailRating;
  ImageView ivGoogleBookDetailImage;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof Activity) {
      this.listener = (FragmentActivity) context;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBook = Parcels.unwrap(getArguments().getParcelable("googleBook"));

    setExitTransition(new MaterialFadeThrough());
    setReenterTransition(new MaterialFadeThrough());
    setEnterTransition(new MaterialFadeThrough());
    setReturnTransition(new MaterialFadeThrough());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_google_book_detail, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    tvGoogleBookDetailAuthor = getActivity().findViewById(R.id.tvGoogleBookDetailAuthor);
    tvGoogleBookDetailDescription = getActivity().findViewById(R.id.tvGoogleBookDetailDescription);
    tvGoogleBookDetailTitle = getActivity().findViewById(R.id.tvGoogleBookDetailTitle);
    btGoogleBookDetailReadBook = getActivity().findViewById(R.id.btGoogleBookDetailReadBook);
    googleBookDetailRating = getActivity().findViewById(R.id.googleBookDetailRating);
    ivGoogleBookDetailImage = getActivity().findViewById(R.id.ivGoogleBookDetailImage);

    tvGoogleBookDetailTitle.setText(mBook.getTitle());
    tvGoogleBookDetailAuthor.setText(mBook.getAuthors().toString());
    tvGoogleBookDetailDescription.setText(mBook.getDescription());
    googleBookDetailRating.setRating(mBook.getAverageRating());
    Glide.with(view)
        .load(mBook.getImageLink())
        .error(R.drawable.green_check)
        .into(ivGoogleBookDetailImage);

    btGoogleBookDetailReadBook.setOnClickListener(v -> goToBookReader());
  }

  private void goToBookReader() {
    Intent intent = new Intent(getContext(), GoogleBookReaderActivity.class);
    intent.putExtra("bookId", mBook.getBookId());
    getActivity().startActivity(intent);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    this.listener = null;
  }
}
