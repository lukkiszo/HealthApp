package com.example.healthapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TextRecognitionActivity extends AppCompatActivity {

    private TextView textView;
    private SurfaceView surfaceView;

    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;

    private static String stringResult = "";

    public static double value1 = -1;
    public static int value2 = -1;
    public static int value3 = -1;
    public static int value4 = -1;

    private TextView typeTextView;
    private TextView value1TextView;
    private TextView value2TextView;
    private TextView value3TextView;
    private TextView value4TextView;
    private static EditText valueRead;

    private Button confirmResultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);
        clearVariables();

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.camera_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Dodanie wyniku za pomocą kamery");
        }

        typeTextView = findViewById(R.id.camera_type);
        value1TextView = findViewById(R.id.value1TextView);
        value2TextView = findViewById(R.id.value2TextView);
        value3TextView = findViewById(R.id.value3TextView);
        value4TextView = findViewById(R.id.value4TextView);
        valueRead = findViewById(R.id.textRecognitionResult);
        confirmResultButton = findViewById(R.id.buttonConfirm);

        confirmResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (intent.getStringExtra("type")){
                    case "Cukier":
                        SugarEditorFragment.valueFromCamera = Double.parseDouble(String.valueOf(valueRead.getText()));
                        clearVariables();
                        finish();
                        break;
                    case "Ciśnienie krwi, puls i saturacja":
                        if (value1 != -1){
                            if (value2 != -1){
                                if (value3 != -1){
                                    BloodPressureEditorFragment.value4FromCamera = Integer.parseInt(String.valueOf(valueRead.getText()));
                                    value4 = Integer.parseInt(String.valueOf(valueRead.getText()));
                                    changeShownValues(intent.getStringExtra("type"));
                                    clearVariables();
                                    finish();
                                } else {
                                    BloodPressureEditorFragment.value3FromCamera = Integer.parseInt(String.valueOf(valueRead.getText()));
                                    value3 = Integer.parseInt(String.valueOf(valueRead.getText()));
                                    changeShownValues(intent.getStringExtra("type"));
                                }
                            } else {
                                BloodPressureEditorFragment.value2FromCamera = Integer.parseInt(String.valueOf(valueRead.getText()));
                                value2 = Integer.parseInt(String.valueOf(valueRead.getText()));
                                changeShownValues(intent.getStringExtra("type"));
                            }
                        } else {
                            BloodPressureEditorFragment.value1FromCamera = Integer.parseInt(String.valueOf(valueRead.getText()));
                            value1 = Integer.parseInt(String.valueOf(valueRead.getText()));
                            changeShownValues(intent.getStringExtra("type"));
                        }
                        valueRead.setText("");
                        break;
                    case "Temperatura ciała":
                        TemperatureEditorFragment.valueFromCamera = Double.parseDouble(String.valueOf(valueRead.getText()));
                        clearVariables();
                        finish();
                        break;
                    default:
                        break;

                }
            }
        });

        typeTextView.setText("Typ wprowadzanego badania : \n" + intent.getStringExtra("type"));

        changeShownValues(intent.getStringExtra("type"));

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

    }

    private void clearVariables(){
        value1 = -1;
        value2 = -1;
        value3 = -1;
        value4 = -1;
    }

    private void changeShownValues(String type){
        switch (type){
            case "Cukier":
                if (value1 != -1){
                    value1TextView.setText("Cukier we krwi : " + value1 + " mg/dl");
                } else {
                    value1TextView.setText("Cukier we krwi : - mg/dl");
                }

                value2TextView.setVisibility(View.GONE);
                value3TextView.setVisibility(View.GONE);
                value4TextView.setVisibility(View.GONE);
                break;
            case "Ciśnienie krwi, puls i saturacja":
                if (value1 != -1){
                    value1TextView.setText("Ciśnienie skurczowe : " + Math.floor(value1));
                } else {
                    value1TextView.setText("Ciśnienie skurczowe : -");
                }

                if (value2 != -1){
                    value2TextView.setText("Ciśnienie rozkurczowe : " + value2);
                } else {
                    value2TextView.setText("Ciśnienie rozkurczowe : -");
                }

                if (value3!= -1){
                    value3TextView.setText("Puls : " + value3 + " BPM");
                } else {
                    value3TextView.setText("Puls : - BPM");
                }

                if (value4!= -1){
                    value4TextView.setText("Saturacja : " + value4 + " %");
                } else {
                    value4TextView.setText("Saturacja : - %");
                }
                break;
            case "Temperatura ciała":
                if (value1 != -1){
                    value1TextView.setText("Temperatura ciała : " + value1 + " °C");
                } else {
                    value1TextView.setText("Temperatura ciała : - °C");
                }
                value2TextView.setVisibility(View.GONE);
                value3TextView.setVisibility(View.GONE);
                value4TextView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null){
            cameraSource.release();
        }

    }

    public static void setValue(String value){
        stringResult = value;
        valueRead.setText(stringResult);
    }


    public void buttonStart(View view){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.cameraView, new CameraFragment());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Czy chcesz odrzucić wprowadzone dane?");
        alertDialogBuilder
                .setMessage("Naciśnięcie przycisku Tak spowoduje usunięcie wprowadzonych danych!")
                .setCancelable(false)
                .setPositiveButton("Tak",
                        (dialog, id) -> {
                            finish();
                        })

                .setNegativeButton("Nie", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}