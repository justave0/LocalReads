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
import com.example.localreads.Fragments.DetailBookFragment;
import com.example.localreads.Models.Book;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MoreBooksAdapter extends RecyclerView.Adapter<MoreBooksAdapter.ViewHolder> {

    private List<Book> mBooks = new ArrayList<>();
    private final Context context;
    private DetailBookFragment fragment_detail_book;

    public MoreBooksAdapter(List<Book> books, Context context) {
        // mBooks.addAll(books);
        this.context = context;
        AppCompatActivity ap = (AppCompatActivity) context;
        fragment_detail_book = (DetailBookFragment) ap.getSupportFragmentManager().findFragmentByTag(DetailBookFragment.class.getSimpleName());
    }

    @NonNull
    @Override
    public MoreBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookView = inflater.inflate(R.layout.item_condensed_book, parent, false);

        // Return a new holder instance
        MoreBooksAdapter.ViewHolder viewHolder = new MoreBooksAdapter.ViewHolder(bookView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Book book = mBooks.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }


    public void updateAdapter(ArrayList<Book> authorBooks) {
        //mBooks.clear();
        mBooks.addAll(authorBooks);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCondensedBookImage;
        TextView tvCondensedBookTitle;
        TextView tvCondensedBookGenres;
        ConstraintLayout clDetailBookItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCondensedBookImage = itemView.findViewById(R.id.ivCondensedBookImage);
            tvCondensedBookGenres = itemView.findViewById(R.id.tvCondensedBookGenres);
            tvCondensedBookTitle = itemView.findViewById(R.id.tvCondensedBookTitle);
            clDetailBookItem = itemView.findViewById(R.id.clDetailBookItem);
        }

        public void bind(Book book) {
            tvCondensedBookTitle.setText(book.getName());
            tvCondensedBookGenres.setText(book.getGenres().toString());
            Glide.with(context).load(book.getImage().getUrl()).into(ivCondensedBookImage);
            DetailBookFragment detailFragment = new DetailBookFragment();
            clDetailBookItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
                    if (fragment_detail_book != null) {

                        FragmentTransaction fts = activity.getSupportFragmentManager().beginTransaction();
                        fts.detach(fragment_detail_book);
                        fts.commit();
                    }
                    //Activity for adapter

                    //Fragment Transaction (idk)
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    //Create a new fragment

                    //Create a new bundle
                    Bundle args = new Bundle();
                    //Put arguments in the bundle
                    args.putParcelable("book", Parcels.wrap(book));
                    //send bundle to the fragment
                    detailFragment.setArguments(args);
                    ft.setReorderingAllowed(true);
                    //replace the fragment
                    ft.replace(R.id.flTemp, detailFragment, DetailBookFragment.class.getSimpleName());
                    //ft.replace(R.id.flTemp, detailFragment, LocalFeedFragment.class.getSimpleName());
                    //add to backstack
                    ft.addToBackStack(DetailBookFragment.class.getSimpleName());
                    //Commit!
                    ft.commit();
                }
            });
        }
    }
}
