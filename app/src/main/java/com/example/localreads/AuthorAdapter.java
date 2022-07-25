package com.example.localreads;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Fragments.DetailAuthorFragment;
import com.example.localreads.Fragments.LocalAuthorFragment;
import com.example.localreads.Models.Author;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {

  private final List<Author> mAuthors;
  private final Context context;

  public AuthorAdapter(Context context, List<Author> authors) {
    mAuthors = (authors);
    this.context = context;
  }

  @NonNull
  @Override
  public AuthorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View bookView = inflater.inflate(R.layout.item_author, parent, false);
    return new AuthorAdapter.ViewHolder(bookView);
  }

  @Override
  public void onBindViewHolder(@NonNull AuthorAdapter.ViewHolder holder, int position) {
    Author author = mAuthors.get(position);
    holder.bind(author);
  }

  public void clear() {
    mAuthors.clear();
    notifyDataSetChanged();
  }

  public void updateAdapter(ArrayList<Author> authors) {
    mAuthors.addAll(authors);
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return mAuthors.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    ImageView ivAuthorPFP;
    TextView tvAuthorUsername;
    TextView tvAuthorBio;
    TextView tvAuthorReads;
    ConstraintLayout clAuthorItem;

    public ViewHolder(View authorView) {
      super(authorView);
      ivAuthorPFP = authorView.findViewById(R.id.ivAuthorPFP);
      tvAuthorBio = authorView.findViewById(R.id.tvAuthorBio);
      tvAuthorUsername = authorView.findViewById(R.id.tvAuthorUsername);
      tvAuthorReads = authorView.findViewById(R.id.tvAuthorReads);
      clAuthorItem = authorView.findViewById(R.id.clAuthorItem);
    }

    public void bind(Author author) {
      tvAuthorReads.setText(author.getReads() + " Total Reads");
      tvAuthorBio.setText(author.getBio());
      tvAuthorUsername.setText(author.getUser().getUsername());
      Glide.with(context)
          .load(author.getUser().getParseFile("profilePic").getUrl())
          .circleCrop()
          .into(ivAuthorPFP);
      clAuthorItem.setOnClickListener(
          v -> {
            AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            DetailAuthorFragment detailFragment = new DetailAuthorFragment();
            Bundle args = new Bundle();
            args.putParcelable("Author", Parcels.wrap(author));
            detailFragment.setArguments(args);
            ft.replace(R.id.flTemp, detailFragment, DetailAuthorFragment.class.getSimpleName());
            ft.addToBackStack(LocalAuthorFragment.class.getSimpleName());
            ft.commit();
          });
    }
  }
}
