package com.example.localreads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.localreads.Models.Book;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class DetailBookFragment extends Fragment {
    FragmentActivity listener;
    private Book mBook;
    TextView tvDetailBookTitle;
    TextView tvDetailBookAuthor;
    TextView tvDetailBookLocation;
    TextView tvDetailBookDescription;
    MaterialButton btReadBook;
    TextView tvDetailReads;
    AppBarLayout ablTopMenu;
    private String TAG = "DetailBookFragment";


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
        mBook = Parcels.unwrap(getArguments().getParcelable("book"));
        ablTopMenu = getActivity().findViewById(R.id.ablTopMenu);
        ablTopMenu.setExpanded(false);
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

        tvDetailBookAuthor.setText(mBook.getUser().getUsername());
        tvDetailBookLocation.setText(mBook.getLocationString());
        tvDetailBookTitle.setText(mBook.getName());
        tvDetailBookDescription.setText(mBook.getDescription());
        tvDetailReads.setText(String.valueOf(mBook.getReads()));
        checkReadPost();


        btReadBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReadPost()){
                    //remove like
                    removeRead();
                    btReadBook.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark_outline));
                }
                else{
                    //add like
                    addRead();
                    btReadBook.setIcon(AppCompatResources.getDrawable(getContext(),R.drawable.ic_bookmark));
                }
                tvDetailReads.setText(String.valueOf(mBook.getReads()));
            }
        });
    }

    private void addRead() {
        //UPDATE like counter
        mBook.addRead();
        mBook.saveInBackground(e -> {
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
        mBook.saveInBackground(e -> {
            if (e==null){
                Log.i(TAG, "read updated");
            }else{
                //Something went wrong
                Log.e(TAG, "Error: "+ e.toString());
            }
        });
    }

    private boolean checkReadPost() {

        if (mBook.getReadBy().contains(ParseUser.getCurrentUser().getObjectId())){
            btReadBook.setIcon(AppCompatResources.getDrawable(getContext(),R.drawable.ic_bookmark));
            return true;
        }
        else{
            btReadBook.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_bookmark_outline));
            return false;
        }

    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.

}
