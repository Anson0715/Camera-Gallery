package com.example.cameragallery;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView imageView;
    private Button btnTakePhoto;
    private Button btnSelectPhoto;
    private final int TAKE_PHOTO = 1;
    private final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);

        // The parameter of setOnClickListener() method is an anonymous inner class.
        // View.OnClickListener() is an Interface using a callback to be invoked when a view is clicked.
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Initialize the File object, that used to store the photo.
                    File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                    try {
                        // There is a image stored in cache, that needs to be deleted.
                        if(outputImage.exists()){
                            outputImage.delete();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    // Use different methods to obtain the image URI based on the SDK version.
                    if(Build.VERSION.SDK_INT >= 24){ // If the SDK Version is 24 or more.
                        imageUri = FileProvider.getUriForFile(MainActivity.this, "CameraGallery.fileprovider", outputImage);
                    }
                    else{
                        imageUri = Uri.fromFile(outputImage);
                    }

                    // Initialize an intent object, that used to invoke the system build-in camera application.
                    // Set the action as take a photo.
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    // store the photo after taking to a certain URI.
                    /*
                        A Uri represents the data to be manipulated,
                        and every resource available on Android - images, video clips, etc.
                        - can be represented by a Uri. The Uri uniquely identifies each resource.
                     */
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    // start the camera application.
                    startActivityForResult(intent, TAKE_PHOTO);
                }
            });

        btnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                // set the intent type to get image files only.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, CHOOSE_PHOTO);
            }
        });
    }

    // Because "take a photo" and "select a photo" use the same startActivityForResult() method,
    // then I use switch condition method to encrypt these two click events based on requestCode.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO: // When user click Take a photo button.
                if (resultCode == RESULT_OK) {
                    try {
                        // Display the photo using bitmap.
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOOSE_PHOTO: // When user click Select a photo button.
                if (resultCode == RESULT_OK) {

                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            // Convert selected photo to bitmap format.
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Display the selected photo.
                        imageView.setImageBitmap(selectedImageBitmap);
                    }
                }
                break;

            default:
                break;
        }
    }
}



























