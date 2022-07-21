package com.example.localreads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.localreads.Fragments.DetailBookFragment;
import com.example.localreads.Fragments.GoogleBookDetailFragment;
import com.example.localreads.Models.Book;
import com.example.localreads.Models.GoogleBook;

import org.parceler.Parcels;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksAdapter extends RecyclerView.Adapter<GoogleBooksAdapter.ViewHolder> {

    private List<GoogleBook> mBooks = new ArrayList<>();
    private final Context context;
    private DetailBookFragment fragment_detail_book;

    public GoogleBooksAdapter(List<GoogleBook> books, Context context) {
        // mBooks.addAll(books);
        this.context = context;
        AppCompatActivity ap = (AppCompatActivity) context;
        fragment_detail_book = (DetailBookFragment) ap.getSupportFragmentManager().findFragmentByTag(DetailBookFragment.class.getSimpleName());
    }

    @NonNull
    @Override
    public GoogleBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookView = inflater.inflate(R.layout.item_condensed_book, parent, false);

        // Return a new holder instance
        GoogleBooksAdapter.ViewHolder viewHolder = new GoogleBooksAdapter.ViewHolder(bookView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GoogleBook book = mBooks.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }


    public void updateAdapter(ArrayList<GoogleBook> googleBooks) {
        //mBooks.clear();
        mBooks.addAll(googleBooks);
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

        public void bind(GoogleBook book) {
            tvCondensedBookGenres.setText(book.getGenres().toString());
            tvCondensedBookTitle.setText(book.getTitle());
            Glide.with(context).load(book.getImageLink())
                    .error(R.drawable.green_check)
                    .into(ivCondensedBookImage);

            clDetailBookItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (book.getEmbeddable()) {
                        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
//                        Intent intent = new Intent(context, GoogleBookReaderActivity.class);
//                        intent.putExtra("bookId", book.getBookId());
//                        context.startActivity(intent);

                        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        //Create a new fragment
                        GoogleBookDetailFragment fragment_google_book_detail = new GoogleBookDetailFragment();
                        //Create a new bundle
                        Bundle args = new Bundle();
                        //Put arguments in the bundle
                        args.putParcelable("googleBook", Parcels.wrap(book));
                        //send bundle to the fragment
                        fragment_google_book_detail.setArguments(args);
//                        ft.setReorderingAllowed(true);
                        //replace the fragment
                        ft.replace(R.id.flTemp, fragment_google_book_detail, DetailBookFragment.class.getSimpleName());
                        //add to backstack
                        ft.addToBackStack(DetailBookFragment.class.getSimpleName());
                        //Commit!
                        ft.commit();
                    }
                    else {
                        Toast.makeText(context, "Book cannot be accessed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
    public void clear() {
        mBooks.clear();
        notifyDataSetChanged();
    }
}
