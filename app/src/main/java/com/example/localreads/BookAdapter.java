package com.example.localreads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.AutoTransition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
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

import org.parceler.Parcels;

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
        TextView tvBookGenre;
        Button btBookSeeMore;
        LinearLayout hiddenViewLayout;
        CardView cvBook;
        TextView tvBookLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
            tvBookGenre = itemView.findViewById(R.id.tvBookGenre);
            btBookDropdown = itemView.findViewById(R.id.btBookDropdown);
            btBookSeeMore = itemView.findViewById(R.id.btBookSeeMore);
            hiddenViewLayout = itemView.findViewById(R.id.hiddenViewLayout);
            cvBook = itemView.findViewById(R.id.cvBook);
            tvBookLocation = itemView.findViewById(R.id.tvBookLocation);
        }

        public void bind(Book book) {
            tvBookTitle.setText(book.getName());
            tvBookAuthor.setText(book.getUser().getUsername());
            tvBookDescription.setText(book.getDescription());
            tvBookLocation.setText("Located in: "+ book.getLocationString());
            tvBookGenre.setText(book.getGenres().toString());
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
            btBookSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Activity for adapter
                    AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
                    //Fragment Transaction (idk)
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    //Create a new fragment
                    DetailBookFragment detailFragment = new DetailBookFragment();
                    //Create a new bundle
                    Bundle args = new Bundle();
                    //Put arguments in the bundle
                    args.putParcelable("book", Parcels.wrap(book));
                    //send bundle to the fragment
                    detailFragment.setArguments(args);
                    //replace the fragment
                    ft.replace(R.id.flTemp, detailFragment);
                    //add to backstack
                    ft.addToBackStack(LocalFeedFragment.class.getSimpleName());
                    //Commit!
                    ft.commit();
                }
            });
        }
    }

    public void updateAdapter(ArrayList<Book> mDataList) {
        this.mBooks = mDataList;
        notifyDataSetChanged();

    }
}
