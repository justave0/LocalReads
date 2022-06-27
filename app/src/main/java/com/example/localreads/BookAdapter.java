package com.example.localreads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import androidx.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Models.Book;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<Book> mBooks;
    private final Context context;

    public BookAdapter(List<Book> books, Context context) {
        mBooks = books;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View bookView = inflater.inflate(R.layout.item_book, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(bookView);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        MaterialButton btBookDropdown;
        TextView tvBookDescription;
        Button btBookSeeMore;
        LinearLayout hiddenViewLayout;
        CardView cvBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
            btBookDropdown = itemView.findViewById(R.id.btBookDropdown);
            btBookSeeMore = itemView.findViewById(R.id.btBookSeeMore);
            hiddenViewLayout = itemView.findViewById(R.id.hiddenViewLayout);
            cvBook = itemView.findViewById(R.id.cvBook);
        }

        public void bind(Book book) {
            tvBookTitle.setText(book.getName());
            tvBookAuthor.setText(book.getUser().getUsername());
            tvBookDescription.setText(book.getDescription());
            Glide.with(context).load(book.getImage().getUrl()).into(ivBookImage);
            btBookDropdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If the CardView is already expanded, set its visibility
                    //  to gone and change the expand less icon to expand more.
                    if (hiddenViewLayout.getVisibility() == View.VISIBLE) {
                        MaterialSharedAxis sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, true);
                        sharedAxis.excludeTarget(R.id.cvBookImage, true);
                        sharedAxis.excludeTarget(R.id.clBookName, true);
                        // The transition of the hiddenView is carried out
                        //  by the TransitionManager class.
                        // Here we use an object of the AutoTransition
                        // Class to create a default transition.
                        TransitionManager.beginDelayedTransition(cvBook, sharedAxis);
                        hiddenViewLayout.setVisibility(View.GONE);
                        btBookDropdown.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_chevron_up));
                    }

                    // If the CardView is not expanded, set its visibility
                    // to visible and change the expand more icon to expand less.
                    else {
                        MaterialSharedAxis sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, false);
                        sharedAxis.excludeTarget(R.id.cvBookImage, true);
                        sharedAxis.excludeTarget(R.id.clBookName, true);
                        TransitionManager.beginDelayedTransition(cvBook, sharedAxis);
                        hiddenViewLayout.setVisibility(View.VISIBLE);
                        btBookDropdown.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_chevron_down));
                    }
                }
            });
        }
    }

    public void updateAdapter(ArrayList<Book> mDataList) {
        this.mBooks = mDataList;
        notifyDataSetChanged();

    }
}
