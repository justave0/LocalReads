package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.localreads.Models.Author;
import com.example.localreads.MoreBooksAdapter;
import com.example.localreads.R;
import com.example.localreads.StartSnapHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localreads.Models.Book;
import com.google.android.material.transition.MaterialFadeThrough;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DetailBookFragment extends Fragment {
    MoreBooksAdapter adapter;
    Context context;
    FragmentActivity listener;
    private Book mBook;
    TextView tvDetailBookTitle;
    TextView tvDetailBookAuthor;
    TextView tvDetailBookLocation;
    TextView tvDetailBookDescription;
    MaterialButton btReadBook;
    TextView tvDetailReads;
    AppBarLayout ablTopMenu;
    RecyclerView rvDetailMoreBooks;
    private String TAG = "DetailBookFragment";
    Author mAuthor;
    public ArrayList<Book> authorBooks = new ArrayList<>();
    Boolean check;


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
        this.context = context;
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBook = Parcels.unwrap(getArguments().getParcelable("book"));
        ablTopMenu = getActivity().findViewById(R.id.ablMain);
        ablTopMenu.setExpanded(false);
        ArrayList<Book> authorBooks = new ArrayList<Book>();
        adapter = new MoreBooksAdapter(authorBooks, getActivity());
        setExitTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_book, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();
        tvDetailBookAuthor = activity.findViewById(R.id.tvDetailBookAuthor);
        tvDetailBookTitle = activity.findViewById(R.id.tvDetailBookTitle);
        tvDetailBookDescription = activity.findViewById(R.id.tvDetailBookDescription);
        tvDetailBookLocation = activity.findViewById(R.id.tvDetailBookLocation);
        btReadBook = activity.findViewById(R.id.btReadBook);
        tvDetailReads = activity.findViewById(R.id.tvDetailReads);
        authorBooks.clear();
        rvDetailMoreBooks = activity.findViewById(R.id.rvDetailMoreBooks);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        rvDetailMoreBooks.setLayoutManager(linearLayoutManager);
        rvDetailMoreBooks.setAdapter(adapter);
        rvDetailMoreBooks.setOnFlingListener(null);
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(rvDetailMoreBooks);
        new StartSnapHelper().attachToRecyclerView(rvDetailMoreBooks);

        tvDetailBookAuthor.setText(mBook.getUser().getUsername());
        tvDetailBookLocation.setText(mBook.getLocationString());
        tvDetailBookTitle.setText(mBook.getName());
        tvDetailBookDescription.setText(mBook.getDescription());
        tvDetailReads.setText(String.valueOf(mBook.getReads()));
        checkReadPost();
        queryMoreBooks();


        btReadBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check){
                    //remove like
                    removeRead();
                    btReadBook.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark_outline));
                    check = false;
                }
                else{
                    //add like
                    addRead();
                    btReadBook.setIcon(AppCompatResources.getDrawable(getContext(),R.drawable.ic_bookmark));
                    check = true;
                }
                tvDetailReads.setText(String.valueOf(mBook.getReads()));
            }
        });


    }

    private void queryMoreBooks() {
        ParseQuery<Author> query = ParseQuery.getQuery("Author");
        query.include(Author.KEY_USER);
        query.whereEqualTo("books", mBook.getObjectId());

        query.findInBackground(new FindCallback<Author>() {
            @Override
            public void done(List<Author> objects, ParseException e) {
                if (e == null){
                    mAuthor = objects.get(0);
                    List<String> authorBooksID = objects.get(0).getBooks();
                    ParseQuery<Book> queryBook = ParseQuery.getQuery("Book");
                    queryBook.whereContainedIn("objectId", authorBooksID);
                    queryBook.include(Book.KEY_USER);
                    queryBook.findInBackground(new FindCallback<Book>() {
                        @Override
                        public void done(List<Book> objects, ParseException e) {
                            if(e == null){
                                authorBooks.addAll(objects);
                                authorBooks.removeIf(n -> (n.getObjectId().equals(mBook.getObjectId())));
                                adapter.updateAdapter(authorBooks);
                                Log.i(TAG, authorBooks.toString());
                            }
                            else{
                                Log.e(TAG, e.toString());
                            }
                        }
                    });
                }
                else{
                    Log.e(TAG, e.toString());
                }
            }
        });

    }

    private void addRead() {
        //UPDATE like counter
        mBook.addRead();
        mBook.getRelation("readBy").add(ParseUser.getCurrentUser());
        mBook.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Log.i(TAG, "read updated");
                }else{
                    //Something went wrong
                    Log.e(TAG, "Error: "+ e.toString());
                }
            }
        });
        mAuthor.addRead();
        mAuthor.saveInBackground(e -> {
            if (e==null){
                Log.i(TAG, "read updated");
            }else{
                //Something went wrong
                Log.e(TAG, "Error: "+ e.toString());
            }
        });
    }

    private void removeRead() {
        mBook.removeRead();
        mBook.getRelation("readBy").remove(ParseUser.getCurrentUser());
        mBook.saveInBackground(e -> {
            if (e==null){
                Log.i(TAG, "read updated");
            }else{
                //Something went wrong
                Log.e(TAG, "Error: "+ e.toString());
            }
        });
        mAuthor.removeRead();
        mAuthor.saveInBackground(e -> {
            if (e==null){
                Log.i(TAG, "read updated");
            }else{
                //Something went wrong
                Log.e(TAG, "Error: "+ e.toString());
            }
        });
    }

    private void checkReadPost() {
        ParseQuery newQuery = mBook.getRelation("readBy").getQuery();


        newQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List <ParseUser> users, ParseException e) {
                ParseUser user = users.stream().filter(temp -> (temp.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())))
                        .findAny().orElse(null);
                if (e == null) {
                    //Object was successfully retrieved
                    if (user != null) {
                        btReadBook.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark));
                        check = true;
                    }
                    else{
                        btReadBook.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark_outline));
                        check = false;
                    }
                } else {
                    // something went wrong
                }
            }
        });

//        newQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//
//            }
//        });
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.listener = null;
    }
// This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.

}
