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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.localreads.GoogleBookReaderActivity;
import com.example.localreads.Models.GoogleBook;
import com.example.localreads.R;

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

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBook = Parcels.unwrap(getArguments().getParcelable("googleBook"));
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_book_detail, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
        Glide.with(view).load(mBook.getImageLink()).error(R.drawable.green_check).into(ivGoogleBookDetailImage);

        btGoogleBookDetailReadBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBookReader();
            }
        });
    }

    private void goToBookReader() {
        Intent intent = new Intent(getContext(), GoogleBookReaderActivity.class);
        intent.putExtra("bookId", mBook.getBookId());
        getActivity().startActivity(intent);
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }


}
