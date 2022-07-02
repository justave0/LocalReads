package com.example.localreads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Models.Author;
import com.example.localreads.Models.Book;

import java.util.ArrayList;
import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {

    private List<Author> mAuthors;
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

        // Inflate the custom layout
        View bookView = inflater.inflate(R.layout.item_author, parent, false);

        // Return a new holder instance
        AuthorAdapter.ViewHolder viewHolder = new AuthorAdapter.ViewHolder(bookView);
        return viewHolder;
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
        //mBooks.clear();
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

        public ViewHolder(View authorView) {
            super(authorView);
            ivAuthorPFP = authorView.findViewById(R.id.ivAuthorPFP);
            tvAuthorBio = authorView.findViewById(R.id.tvAuthorBio);
            tvAuthorUsername = authorView.findViewById(R.id.tvAuthorUsername);
            tvAuthorReads = authorView.findViewById(R.id.tvAuthorReads);
        }

        public void bind(Author author) {
            tvAuthorReads.setText(author.getReads() + " Total Reads");
            tvAuthorBio.setText(author.getBio());
            tvAuthorUsername.setText(author.getUser().getUsername());
            Glide.with(context).load(author.getUser().getParseFile("profilePic").getUrl()).circleCrop().into(ivAuthorPFP);
        }
    }
}
