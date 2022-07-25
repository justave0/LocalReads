package com.example.localreads;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.Fragments.DetailBookFragment;
import com.example.localreads.Fragments.GoogleBookDetailFragment;
import com.example.localreads.Models.GoogleBook;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class GoogleBooksAdapter extends RecyclerView.Adapter<GoogleBooksAdapter.ViewHolder> {

  private final List<GoogleBook> mBooks = new ArrayList<>();
  private final Context context;

  public GoogleBooksAdapter(Context context) {
    this.context = context;
  }

  @NonNull
  @Override
  public GoogleBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);
    View bookView = inflater.inflate(R.layout.item_condensed_book, parent, false);
    return new GoogleBooksAdapter.ViewHolder(bookView);
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
    // mBooks.clear();
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
      Glide.with(context)
          .load(book.getImageLink())
          .error(R.drawable.green_check)
          .into(ivCondensedBookImage);

      clDetailBookItem.setOnClickListener(
          v -> {
            if (book.getEmbeddable()) {
              AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
              FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
              GoogleBookDetailFragment fragment_google_book_detail = new GoogleBookDetailFragment();
              Bundle args = new Bundle();
              args.putParcelable("googleBook", Parcels.wrap(book));
              fragment_google_book_detail.setArguments(args);
              ft.replace(
                  R.id.flTemp,
                  fragment_google_book_detail,
                  DetailBookFragment.class.getSimpleName());
              ft.addToBackStack(DetailBookFragment.class.getSimpleName());
              ft.commit();
            } else {
              Toast.makeText(context, "Book cannot be accessed", Toast.LENGTH_SHORT).show();
            }
          });
    }
  }

  public void clear() {
    mBooks.clear();
    notifyDataSetChanged();
  }
}
