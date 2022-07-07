package com.example.localreads.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localreads.MainActivity;
import com.example.localreads.Models.Book;
import com.example.localreads.MoreBooksAdapter;
import com.example.localreads.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.transition.MaterialFadeThrough;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ReaderProfileFragment extends Fragment {
    private static final int WRAP_CONTENT = -2;
    private ParseUser mReader;
    MoreBooksAdapter adapter;
    FragmentActivity listener;
    Context context;
    RecyclerView rvProfileReaderBooks;
    AppBarLayout ablMain;
    ImageView ivProfileReaderPFP;
    TextView tvProfileReaderReads;
    TextView tvProfileReaderLocation;
    TextView tvProfileReaderName;
    Button btReaderUserSettings;
    String TAG = "DetailAuthorFragment";
    private ArrayList mMoreBooks = new ArrayList();


    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
            this.context = context;
        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Book> mMoreBooks = new ArrayList<Book>();
        mReader = ParseUser.getCurrentUser();
        adapter = new MoreBooksAdapter(mMoreBooks, getActivity());
        ablMain = getActivity().findViewById(R.id.ablMain);
        setExitTransition(new MaterialFadeThrough());
        setReenterTransition(new MaterialFadeThrough());
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());

    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_author, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();

        tvProfileReaderName = activity.findViewById(R.id.tvProfileReaderName);
        tvProfileReaderReads = activity.findViewById(R.id.tvProfileReaderReads);
        tvProfileReaderLocation = activity.findViewById(R.id.tvProfileReaderLocation);
        ivProfileReaderPFP = activity.findViewById(R.id.ivProfileReaderPFP);
        btReaderUserSettings = activity.findViewById(R.id.btReaderUserSettings);
        btReaderUserSettings.setVisibility(View.VISIBLE);
        rvProfileReaderBooks = activity.findViewById(R.id.rvProfileReaderBooks);
        populateData();
    }

    private void populateData() {
        MainActivity activity = (MainActivity) getActivity();
        ParseGeoPoint latlng = mReader.getParseGeoPoint("location");
        tvProfileReaderLocation.setText(activity.getReverseGeocode(new LatLng(latlng.getLatitude(),latlng.getLongitude())));
        tvProfileReaderName.setText(mReader.getUsername());
        Glide.with(context).load(mReader.getParseFile("profilePic").getUrl()).circleCrop().into(ivProfileReaderPFP);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        rvProfileReaderBooks.setLayoutManager(gridLayoutManager);
        rvProfileReaderBooks.setAdapter(adapter);
        rvProfileReaderBooks.setOnFlingListener(null);
        queryMoreBooks();
    }


    private void queryMoreBooks() {
        ParseQuery<Book> queryBook = ParseQuery.getQuery(Book.class);
        queryBook.whereEqualTo("readBy", ParseUser.getCurrentUser());
        queryBook.include(Book.KEY_USER);
        queryBook.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> objects, ParseException e) {
                if (e == null){
                    MainActivity activity = (MainActivity) getActivity();
                    if (getActivity() != null) {
                        activity.spinner.setVisibility(View.GONE);
                    }
                    tvProfileReaderReads.setText(String.valueOf(objects.size()) + " Books Read");
                    mMoreBooks.addAll(objects);
                    adapter.updateAdapter(mMoreBooks);

                }
                else{
                    Log.e(TAG, e.toString());
                }
            }
        });

    }
    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
        ablMain.setVisibility(View.VISIBLE);
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.clear();
            mMoreBooks.clear();
        }
        if(ablMain != null) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ablMain.getLayoutParams();
            params.height = 0;
            ablMain.setLayoutParams(params);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(ablMain != null){
            CoordinatorLayout.LayoutParams params= (CoordinatorLayout.LayoutParams) ablMain.getLayoutParams();
            params.height=WRAP_CONTENT;
            ablMain.setLayoutParams(params);
        }
    }
}
