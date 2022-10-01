package com.example.healthapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private static final int REQUEST_CALL = 1;
    private String number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        setProfilePhoto();
        customizeDimension();
    }


    private void customizeDimension(){
        View bloodpressure = findViewById(R.id.blood_pressure);
        View sugar = findViewById(R.id.sugar);
        View steps = findViewById(R.id.steps);
        View medicines = findViewById(R.id.medicines);
        Integer height = Resources.getSystem().getDisplayMetrics().heightPixels;

        bloodpressure.getLayoutParams().height = height / 8;
        sugar.getLayoutParams().height = height / 8;
        steps.getLayoutParams().height = height / 8;
        medicines.getLayoutParams().height = height / 8;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Czy na pewno chcesz wyjść z aplikacji?");
        alertDialogBuilder
                .setMessage("Kliknij tak aby wyjść!")
                .setCancelable(false)
                .setPositiveButton("Tak",
                        (dialog, id) -> {
                            moveTaskToBack(true);
                            System.exit(0);
                        })

                .setNegativeButton("Nie", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void exit(View view){
        onBackPressed();
    }

    public void emergencyCall(View view){
        number = "112";
        call(number);
    }

    public void call_ICE(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(ProfileActivity.mypreference, Context.MODE_PRIVATE);
        number = sharedPreferences.getString("number", "112");
        call(number);
    }

    private void call(String number){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    @SuppressLint({"MissingSuperCall", "ShowToast"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                call(number);
            }
            else {
                Toast.makeText(this, "Odmowa dostępu", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.call_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.sugarNav:
                intent = new Intent(this, SugarActivity.class);
                startActivity(intent);
                break;

            case R.id.myProfile:
                goToProfile(getCurrentFocus());
                break;

            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.exit:
                exit(findViewById(android.R.id.content).getRootView());
                break;

            default:
                break;
        }

        return false;
    }

    @Override
    protected void onResume() {
        setProfilePhoto();
        super.onResume();
    }

    private void setProfilePhoto(){
        SharedPreferences sharedPref = getSharedPreferences(ProfileActivity.mypreference, Context.MODE_PRIVATE);

        ImageButton headerProfile = (ImageButton) findViewById(R.id.header_profile);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.username);
        ImageView navigationPhoto = (ImageView) headerView.findViewById(R.id.userPhoto);

        String photo = sharedPref.getString("photo", "");
        String name = sharedPref.getString("username", "");

        headerProfile.setImageURI(Uri.parse(photo));
        nav_user.setText(name);
        navigationPhoto.setImageURI(Uri.parse(photo));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}