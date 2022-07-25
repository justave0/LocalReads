package com.example.localreads.CreateBook;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.localreads.R;
import com.google.android.material.transition.MaterialSharedAxis;

import java.io.File;
import java.io.IOException;

public class CreateFragment2 extends Fragment {
  FragmentActivity listener;
  EditText etCreateURL;
  TextView tvPhotoMenu;
  Button btSamplePhoto;
  ImageView ivCreatePreview;
  CardView cvCreateImage;
  public String photoFileName = "photo.jpg";
  File photoFile;
  public final String APP_TAG = "LocalReads";
  public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
  public static final int GET_FROM_GALLERY = 3;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof Activity) {
      this.listener = (FragmentActivity) context;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_create_2, parent, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    etCreateURL = view.findViewById(R.id.etCreateURL);
    cvCreateImage = view.findViewById(R.id.cvCreateImage);
    tvPhotoMenu = view.findViewById(R.id.tvPhotoMenu);
    btSamplePhoto = view.findViewById(R.id.btSamplePhoto);
    ivCreatePreview = view.findViewById(R.id.ivCreatePreview);
    ivCreatePreview.setVisibility(View.INVISIBLE);
    // Inflate the menu on clCreateImage press
    cvCreateImage.setOnClickListener(this::showMenu);
  }

  private void showMenu(View v) {
    PopupMenu popupMenu = new PopupMenu(getContext(), tvPhotoMenu);
    popupMenu.getMenuInflater().inflate(R.menu.pick_photo_type_menu, popupMenu.getMenu());
    popupMenu.setOnMenuItemClickListener(
        item -> {
          if (item.getItemId() == R.id.mi_camera_photo) {
            onLaunchCamera();
          } else if (item.getItemId() == R.id.mi_saved_photo) {
            startActivityForResult(
                new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                GET_FROM_GALLERY);
          }
          return true;
        });
    popupMenu.show();
  }

  public void onLaunchCamera() {
    // create Intent to take a picture and return control to the calling application
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Create a File reference for future access
    photoFile = getPhotoFileUri(photoFileName);

    // wrap File object into a content provider
    // required for API >= 24
    // See
    // https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
    Uri fileProvider =
        FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

    // If you call startActivityForResult() using an intent that no app can handle, your app will
    // crash.
    // So as long as the result is not null, it's safe to use the intent.
    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
      // Start the image capture intent to take photo
      startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
  }

  public File getPhotoFileUri(String fileName) {
    // Get safe storage directory for photos
    // Use `getExternalFilesDir` on Context to access package-specific directories.
    // This way, we don't need to request external read/write runtime permissions.
    File mediaStorageDir =
        new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
      Log.d(APP_TAG, "failed to create directory");
    }

    // Return the file target for the photo based on filename
    return new File(mediaStorageDir.getPath() + File.separator + fileName);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        // by this point we have the camera photo on disk
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        // RESIZE BITMAP, see section below
        // Load the taken image into a preview

        ivCreatePreview.setImageBitmap(takenImage);
        ivCreatePreview.setVisibility(View.VISIBLE);
      } else { // Result was a failure
        Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
      Uri selectedImage = data.getData();
      Bitmap bitmap;
      try {
        bitmap =
            MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
        ivCreatePreview.setImageBitmap(bitmap);
        ivCreatePreview.setVisibility(View.VISIBLE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    this.listener = null;
  }
}
