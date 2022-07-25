package com.example.localreads.CreateBook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localreads.MainActivity;
import com.example.localreads.Models.Book;
import com.example.localreads.R;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {
  Button btCreateGoBack;
  Button btCreateGoNext;
  TextView tvCreateStep;
  CreateFragment1 create_fragment_1 = new CreateFragment1();
  CreateFragment2 create_fragment_2 = new CreateFragment2();
  int currentFragment;
  String bookName;
  String bookDescription;
  List<String> bookGenres = new ArrayList<>();
  String bookLink;
  File bookPhoto;
  String address;
  String authorID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create);
    Intent intent = getIntent();
    address = intent.getStringExtra("address");
    authorID = intent.getStringExtra("authorID");
    btCreateGoBack = findViewById(R.id.btCreateGoBack);
    btCreateGoNext = findViewById(R.id.btCreateGoNext);
    tvCreateStep = findViewById(R.id.tvCreateStep);
    startFragment1();
    Fade enter = new Fade();
    getWindow().setExitTransition(enter.addTarget(R.id.clCreate));
    getWindow().setAllowEnterTransitionOverlap(true);

    btCreateGoNext.setOnClickListener(v -> goNextFragment());
    btCreateGoBack.setOnClickListener(v -> goBackFragment());
  }

  private void goBackFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.flPlaceholderCreate, create_fragment_1);
    ft.commit();
    tvCreateStep.setText("Step 1 of 2");
    btCreateGoBack.setVisibility(View.INVISIBLE);
    bookGenres.clear();
    create_fragment_1.favoriteGenres.clear();
    currentFragment = 1;
  }

  private void goNextFragment() {
    if (currentFragment == 1 && checkFragment1()) {
      bookName = create_fragment_1.etCreateBookTitle.getText().toString();
      bookDescription = create_fragment_1.etCreateBookDescription.getText().toString();
      bookGenres.addAll(create_fragment_1.favoriteGenres);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.flPlaceholderCreate, create_fragment_2);
      ft.commit();
      tvCreateStep.setText("Step 2 of 2");
      btCreateGoBack.setVisibility(View.VISIBLE);
      currentFragment = 2;
    } else if (currentFragment == 2 && checkFragment2()) {
      bookLink = create_fragment_2.etCreateURL.getText().toString();
      bookPhoto = create_fragment_2.photoFile;
      postBook();
    }
  }

  private void postBook() {
    Book book = new Book();
    book.setDescription(bookDescription);
    book.setImage(new ParseFile(create_fragment_2.photoFile));
    book.setLink(bookLink);
    book.setName(bookName);
    book.setReads(1);
    book.setUser(ParseUser.getCurrentUser());
    book.setGenres(bookGenres);
    book.setLocationString(address);
    book.getReadBy().add(ParseUser.getCurrentUser());
    // Saves the new object.
    book.saveInBackground(
        e -> {
          if (e == null) {
            updateAuthor(book);
            book.getReadBy();
          } else {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void updateAuthor(Book book) {
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Author");
    query.getInBackground(
        authorID,
        (object, e) -> {
          if (e == null) {
            List<String> books = object.getList("books");
            books.add(book.getObjectId());
            object.put("books", books);
            object.saveInBackground();
            Intent intent = new Intent(CreateActivity.this, MainActivity.class);
            startActivity(intent);
          } else {
            // something went wrong
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  private boolean checkFragment1() {
    create_fragment_1.setGenres();
    if (create_fragment_1.etCreateBookTitle.getText().toString().length() == 0) {
      Toast.makeText(CreateActivity.this, "No Book Title", Toast.LENGTH_LONG).show();
      return false;
    } else if (create_fragment_1.etCreateBookTitle.getText().toString().length() >= 101) {
      Toast.makeText(CreateActivity.this, "Book Title too Long", Toast.LENGTH_LONG).show();
      return false;
    }
    if (create_fragment_1.etCreateBookDescription.getText().toString().length() == 0) {
      Toast.makeText(CreateActivity.this, "No Description", Toast.LENGTH_LONG).show();
      return false;
    } else if (create_fragment_1.etCreateBookDescription.getText().toString().length() >= 2001) {
      Toast.makeText(CreateActivity.this, "Description too Long", Toast.LENGTH_LONG).show();
      return false;
    }
    if (create_fragment_1.favoriteGenres.size() == 0) {
      Toast.makeText(CreateActivity.this, "No Genres Selected", Toast.LENGTH_LONG).show();
      return false;
    }
    if (create_fragment_1.favoriteGenres.size() >= 4) {
      Toast.makeText(CreateActivity.this, "More than 3 Genres Selected", Toast.LENGTH_LONG).show();
      return false;
    }
    return true;
  }

  private boolean checkFragment2() {
    if (create_fragment_2.etCreateURL.getText().toString().length() == 0) {
      Toast.makeText(CreateActivity.this, "No Book URL Provided", Toast.LENGTH_LONG).show();
      return false;
    }
    if (create_fragment_2.photoFile == null) {
      Toast.makeText(CreateActivity.this, "No Photo Provided", Toast.LENGTH_LONG).show();
      return false;
    }
    return true;
  }

  private void startFragment1() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    // Replace the contents of the container with the new fragment
    ft.replace(R.id.flPlaceholderCreate, create_fragment_1);
    // Complete the changes added above
    ft.commit();
    currentFragment = 1;
  }
}
