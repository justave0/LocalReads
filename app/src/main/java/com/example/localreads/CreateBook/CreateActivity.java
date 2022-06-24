package com.example.localreads.CreateBook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localreads.R;
import com.google.android.material.transition.MaterialSharedAxis;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        btCreateGoBack = findViewById(R.id.btCreateGoBack);
        btCreateGoNext = findViewById(R.id.btCreateGoNext);
        tvCreateStep = findViewById(R.id.tvCreateStep);
        startFragment1();




        btCreateGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextFragment(v);
            }
        });

        btCreateGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackFragment(v);
            }
        });
    }

    private void goBackFragment(View v) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.flPlaceholderCreate, create_fragment_1);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        tvCreateStep.setText("Step 1 of 2");
        btCreateGoBack.setVisibility(View.INVISIBLE);
        currentFragment = 1;
    }

    private void goNextFragment(View v) {
        if (currentFragment == 1 && checkFragment1(v)){
            bookName = create_fragment_1.etCreateBookTitle.getText().toString();
            bookDescription = create_fragment_1.etCreateBookDescription.getText().toString();
            bookGenres = create_fragment_1.favoriteGenres;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment
            ft.replace(R.id.flPlaceholderCreate, create_fragment_2);
            // or ft.add(R.id.your_placeholder, new FooFragment());
            // Complete the changes added above
            ft.commit();
            create_fragment_1.favoriteGenres.clear();
            tvCreateStep.setText("Step 2 of 2");
            btCreateGoBack.setVisibility(View.VISIBLE);
            currentFragment = 2;
        }
    }

    private boolean checkFragment1(View v) {
        create_fragment_1.setGenres();
        if (create_fragment_1.etCreateBookTitle.getText().toString().length() == 0 ){
            Toast.makeText(CreateActivity.this, "No Book Title", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (create_fragment_1.etCreateBookTitle.getText().toString().length() >= 101 ){
            Toast.makeText(CreateActivity.this, "Book Title too Long", Toast.LENGTH_LONG).show();
            return false;
        }
        if (create_fragment_1.etCreateBookDescription.getText().toString().length() == 0){
            Toast.makeText(CreateActivity.this, "No Description", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (create_fragment_1.etCreateBookDescription.getText().toString().length() >= 2001){
            Toast.makeText(CreateActivity.this, "Description too Long", Toast.LENGTH_LONG).show();
            return false;
        }
        // favorite genres because its copied from earlier code
        if (create_fragment_1.favoriteGenres.size() == 0){
            Toast.makeText(CreateActivity.this, "No Genres Selected", Toast.LENGTH_LONG).show();
            return false;
        }
        if (create_fragment_1.favoriteGenres.size() >= 4){
            Toast.makeText(CreateActivity.this, "More than 3 Genres Selected", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void startFragment1() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.flPlaceholderCreate, create_fragment_1);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
        currentFragment = 1;
    }


}