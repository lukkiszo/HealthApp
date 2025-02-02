package com.example.healthapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import org.jetbrains.annotations.NotNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.load.DecodeFormat;

public class ProfileActivity extends AppCompatActivity {
    public String user_name;
    public String user_photo;
    public Integer user_age;
    public Integer user_height;
    public Integer user_weight;
    public String user_ICE_number;
    private ImageButton changePhotoButton;
    public static final String mypreference = "HealthAppProfile";
    private static final int IMAGE_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText age = (EditText) findViewById(R.id.age);
        EditText height = (EditText) findViewById(R.id.height);
        EditText weight = (EditText) findViewById(R.id.weight);
        age.setFilters(new InputFilter[]{new InputFilterMinMax("1", "150")});
        height.setFilters(new InputFilter[]{new InputFilterMinMax("1", "250")});
        weight.setFilters(new InputFilter[]{new InputFilterMinMax("1", "350")});

        changePhotoButton = (ImageButton) findViewById(R.id.photo);
        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, 1001);
                    } else {
                        changePhoto();
                    }
                }
            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.MyProfile);
        }

        customizeDimension();
        loadSettings();
    }

    private void customizeDimension() {
        View photo = findViewById(R.id.photo);
        View username = findViewById(R.id.name);
        View phone = findViewById(R.id.phonenumber);

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

        photo.getLayoutParams().height = height / 4;
        photo.getLayoutParams().width = width / 2;
        username.getLayoutParams().width = (8 * width) / 10;
        phone.getLayoutParams().width = (8 * width) / 10;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            saveSettings();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    private void loadSettings() {
        SharedPreferences sharedPref = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        user_name = sharedPref.getString("username", "");
        user_ICE_number = sharedPref.getString("number", "");
        user_age = sharedPref.getInt("age", 0);
        user_height = sharedPref.getInt("height", 0);
        user_weight = sharedPref.getInt("weight", 0);
        user_photo = sharedPref.getString("photo", "");
        // We can then use the data

        EditText age = (EditText) findViewById(R.id.age);
        EditText height = (EditText) findViewById(R.id.height);
        EditText weight = (EditText) findViewById(R.id.weight);
        EditText username = (EditText) findViewById(R.id.name);
        EditText number = (EditText) findViewById(R.id.phonenumber);
        ImageButton photo = (ImageButton) findViewById(R.id.photo);

        age.setText(String.valueOf(user_age));
        height.setText(String.valueOf(user_height));
        weight.setText(String.valueOf(user_weight));
        username.setText(user_name);
        number.setText(user_ICE_number);

        Uri imageUri = Uri.parse(user_photo);
        if (imageUri != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .apply(new RequestOptions()
                            .override(512)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .encodeQuality(80))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            photo.setImageBitmap(resource);
                        }
                    });

        }
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        EditText age = (EditText) findViewById(R.id.age);
        EditText height = (EditText) findViewById(R.id.height);
        EditText weight = (EditText) findViewById(R.id.weight);
        EditText username = (EditText) findViewById(R.id.name);
        EditText number = (EditText) findViewById(R.id.phonenumber);

        editor.putString("username", username.getText().toString());
        editor.putString("number", number.getText().toString());
        if (!age.getText().toString().equals("")) {
            editor.putInt("age", Integer.parseInt(age.getText().toString()));
        }

        if (!height.getText().toString().equals("")) {
            editor.putInt("height", Integer.parseInt(height.getText().toString()));
        }

        if (!weight.getText().toString().equals("")) {
            editor.putInt("weight", Integer.parseInt(weight.getText().toString()));
        }

        editor.apply();

    }

    private void changePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    @SuppressLint({"MissingSuperCall", "ShowToast"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                changePhoto();
            } else {
                Toast.makeText(this, getString(R.string.PermissionDeniedText), Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint({"MissingSuperCall"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    Glide.with(this)
                            .asBitmap()
                            .load(imageUri)
                            .apply(new RequestOptions()
                                    .override(512)
                                    .format(DecodeFormat.PREFER_RGB_565)
                                    .encodeQuality(80))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    changePhotoButton.setImageBitmap(resource);

                                    SharedPreferences.Editor editor = getSharedPreferences(mypreference, Context.MODE_PRIVATE).edit();
                                    editor.putString("photo", imageUri.toString());
                                    editor.apply();
                                }
                            });
                }
            }
        }
    }
}