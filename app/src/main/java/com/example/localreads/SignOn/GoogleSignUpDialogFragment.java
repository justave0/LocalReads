package com.example.localreads.SignOn;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.localreads.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class GoogleSignUpDialogFragment extends DialogFragment {

    private HashMap<String, String> userData;
    EditText etGoogleSignUpEmail;
    Button btGoogleMoreInfo;
    public TextInputEditText etGoogleSignUpUsername;
    public MaterialButtonToggleGroup bgGoogleChoice;
    public Button btGoogleSignUpContinue;


    public GoogleSignUpDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static GoogleSignUpDialogFragment newInstance(HashMap<String, String> userData) {
        GoogleSignUpDialogFragment frag = new GoogleSignUpDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("userData", userData);
        frag.setArguments(args);
        return frag;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_sign_up, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userData = (HashMap<String, String>) getArguments().getSerializable("userData");

        etGoogleSignUpEmail = view.findViewById(R.id.etGoogleSignUpEmail);
        etGoogleSignUpEmail.setKeyListener(null);
        etGoogleSignUpEmail.setText(userData.get("email"));
        
        btGoogleMoreInfo = view.findViewById(R.id.btGoogleMoreInfo);

        etGoogleSignUpUsername = view.findViewById(R.id.etGoogleSignUpUsername);
        bgGoogleChoice = view.findViewById(R.id.bgGoogleChoice);
        btGoogleSignUpContinue = view.findViewById(R.id.btGoogleSignUpContinue);


        // Get field from view
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        
        btGoogleMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateInfoMenu(v);
            }
        });

        btGoogleSignUpContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etGoogleSignUpUsername.getText().toString();
                String tag = (String) ((Button)view.findViewById(bgGoogleChoice.getCheckedButtonId())).getText();
                ((LoginActivity)getActivity()).signUpUserIntent(username, tag);
            }
        });
    }

    private void inflateInfoMenu(View v) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("User Type")
                .setMessage("Local Reads allows users to create their account as either a reader or " +
                        "an author. Users are encouraged to start as reader and get a feel for the app" +
                        " and then change to an author to publish their books to the app. Authors are " +
                        "required to give sensitive information like their city and state of residence.")
                .show();
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
