package com.example.localreads.CreateBook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.localreads.R;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.ArrayList;
import java.util.List;

public class CreateFragment1 extends Fragment {
  FragmentActivity listener;
  List<String> favoriteGenres = new ArrayList<>();
  CheckBox cbCAction;
  CheckBox cbCClassics;
  CheckBox cbCComics;
  CheckBox cbCDetective;
  CheckBox cbCFantasy;
  CheckBox cbCHistoricalFiction;
  CheckBox cbCHorror;
  CheckBox cbCFiction;
  CheckBox cbCRomance;
  CheckBox cbCSuspense;
  CheckBox cbCSciFi;
  CheckBox cbCShortStories;
  CheckBox cbCWomen;
  CheckBox cbCBiographies;
  CheckBox cbCCookbooks;
  CheckBox cbCEssays;
  CheckBox cbCHistory;
  CheckBox cbCMemoir;
  CheckBox cbCPoetry;
  CheckBox cbCSelfHelp;
  CheckBox cbCTrueCrime;
  EditText etCreateBookTitle;
  EditText etCreateBookDescription;

  // This event fires 1st, before creation of fragment or any views
  // The onAttach method is called when the Fragment instance is associated with an Activity.
  // This does not mean the Activity is fully initialized.
  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof Activity) {
      this.listener = (FragmentActivity) context;
    }
  }

  // This event fires 2nd, before views are created for the fragment
  // The onCreate method is called when the Fragment instance is being created, or re-created.
  // Use onCreate for any standard setup that does not require the activity to be fully created
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
  }

  // The onCreateView method is called when Fragment should create its View object hierarchy,
  // either dynamically or via XML layout inflation.
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_create_1, parent, false);
  }

  // This event is triggered soon after onCreateView().
  // onViewCreated() is only called if the view returned from onCreateView() is non-null.
  // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    assignCheckboxes(view);
    etCreateBookTitle = view.findViewById(R.id.etCreateBookTitle);
    etCreateBookDescription = view.findViewById(R.id.etCreateBookDescription);
  }

  private void assignCheckboxes(View view) {
    cbCAction = view.findViewById(R.id.cbCAction);
    cbCBiographies = view.findViewById(R.id.cbCBiographies);
    cbCClassics = view.findViewById(R.id.cbCClassics);
    cbCComics = view.findViewById(R.id.cbCComics);
    cbCCookbooks = view.findViewById(R.id.cbCCookbooks);
    cbCDetective = view.findViewById(R.id.cbCDetective);
    cbCEssays = view.findViewById(R.id.cbCEssays);
    cbCFantasy = view.findViewById(R.id.cbCFantasy);
    cbCFiction = view.findViewById(R.id.cbCFiction);
    cbCHistoricalFiction = view.findViewById(R.id.cbCHistoricalFiction);
    cbCHorror = view.findViewById(R.id.cbCHorror);
    cbCRomance = view.findViewById(R.id.cbCRomance);
    cbCSciFi = view.findViewById(R.id.cbCSciFi);
    cbCShortStories = view.findViewById(R.id.cbCShortStories);
    cbCWomen = view.findViewById(R.id.cbCWomen);
    cbCHistory = view.findViewById(R.id.cbCHistory);
    cbCMemoir = view.findViewById(R.id.cbCMemoir);
    cbCPoetry = view.findViewById(R.id.cbCPoetry);
    cbCSelfHelp = view.findViewById(R.id.cbCSelfHelp);
    cbCTrueCrime = view.findViewById(R.id.cbCTrueCrime);
    cbCSuspense = view.findViewById(R.id.cbCSuspense);
  }

  // This method is called when the fragment is no longer connected to the Activity
  // Any references saved in onAttach should be nulled out here to prevent memory leaks.
  @Override
  public void onDetach() {
    super.onDetach();
    this.listener = null;
    favoriteGenres.clear();
  }

  public void setGenres() {
    if (cbCAction.isChecked()) {
      favoriteGenres.add(getString(R.string.action_and_adventure));
    }
    if (cbCBiographies.isChecked()) {
      favoriteGenres.add(getString(R.string.biographies_and_autobiographies));
    }
    if (cbCClassics.isChecked()) {
      favoriteGenres.add(getString(R.string.classics));
    }
    if (cbCComics.isChecked()) {
      favoriteGenres.add(getString(R.string.comic_books_and_graphic_novels));
    }
    if (cbCCookbooks.isChecked()) {
      favoriteGenres.add(getString(R.string.cookbooks));
    }
    if (cbCDetective.isChecked()) {
      favoriteGenres.add(getString(R.string.detective_and_mystery));
    }
    if (cbCEssays.isChecked()) {
      favoriteGenres.add(getString(R.string.essays));
    }
    if (cbCFantasy.isChecked()) {
      favoriteGenres.add(getString(R.string.fantasy));
    }
    if (cbCFiction.isChecked()) {
      favoriteGenres.add(getString(R.string.literary_fiction));
    }
    if (cbCHistoricalFiction.isChecked()) {
      favoriteGenres.add(getString(R.string.historical_fiction));
    }
    if (cbCHorror.isChecked()) {
      favoriteGenres.add(getString(R.string.horror));
    }
    if (cbCRomance.isChecked()) {
      favoriteGenres.add(getString(R.string.romance));
    }
    if (cbCSciFi.isChecked()) {
      favoriteGenres.add(getString(R.string.science_fiction));
    }
    if (cbCShortStories.isChecked()) {
      favoriteGenres.add(getString(R.string.short_stories));
    }
    if (cbCWomen.isChecked()) {
      favoriteGenres.add(getString(R.string.women_s_fiction));
    }
    if (cbCHistory.isChecked()) {
      favoriteGenres.add(getString(R.string.history));
    }
    if (cbCMemoir.isChecked()) {
      favoriteGenres.add(getString(R.string.memoir));
    }
    if (cbCPoetry.isChecked()) {
      favoriteGenres.add(getString(R.string.poetry));
    }
    if (cbCSelfHelp.isChecked()) {
      favoriteGenres.add(getString(R.string.self_help));
    }
    if (cbCTrueCrime.isChecked()) {
      favoriteGenres.add(getString(R.string.true_crime));
    }
    if (cbCSuspense.isChecked()) {
      favoriteGenres.add(getString(R.string.suspense_and_thrillers));
    }
  }
}
