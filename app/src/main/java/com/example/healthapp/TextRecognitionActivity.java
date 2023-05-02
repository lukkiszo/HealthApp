package com.example.healthapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import java.text.MessageFormat;

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
            getSupportActionBar().setTitle(R.string.CameraActivityLabel);
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
                    case "Sugar":
                        SugarEditorFragment.valueFromCamera = Double.parseDouble(String.valueOf(valueRead.getText()));
                        clearVariables();
                        finish();
                        break;
                    case "Ciśnienie krwi, puls i saturacja":
                    case "Blood pressure, pulse and saturation":
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
                    case "Body temperature":
                        TemperatureEditorFragment.valueFromCamera = Double.parseDouble(String.valueOf(valueRead.getText()));
                        clearVariables();
                        finish();
                        break;
                    default:
                        break;

                }
            }
        });

        typeTextView.setText(MessageFormat.format("{0}{1}", getString(R.string.ResultType), intent.getStringExtra("type")));

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
            case "Sugar":
                if (value1 != -1){
                    value1TextView.setText(MessageFormat.format("{0} {1} mg/dl", getString(R.string.BloodSugar), value1));
                } else {
                    value1TextView.setText(MessageFormat.format("{0} - mg/dl", getString(R.string.BloodSugar)));
                }

                value2TextView.setVisibility(View.GONE);
                value3TextView.setVisibility(View.GONE);
                value4TextView.setVisibility(View.GONE);
                break;
            case "Ciśnienie krwi, puls i saturacja":
            case "Blood pressure, pulse and saturation":
                if (value1 != -1){
                    value1TextView.setText(MessageFormat.format("{0} {1}", getString(R.string.SystolicBloodPressure), Math.floor(value1)));
                } else {
                    value1TextView.setText(MessageFormat.format("{0} -", getString(R.string.SystolicBloodPressure)));
                }

                if (value2 != -1){
                    value2TextView.setText(MessageFormat.format("{0} {1}", getString(R.string.DiastolicBloodPressure), value2));
                } else {
                    value2TextView.setText(MessageFormat.format("{0} -", getString(R.string.DiastolicBloodPressure)));
                }

                if (value3!= -1){
                    value3TextView.setText(MessageFormat.format("{0} {1} BPM", getString(R.string.PulseTextRecogn), value3));
                } else {
                    value3TextView.setText(MessageFormat.format("{0} - BPM", getString(R.string.PulseTextRecogn)));
                }

                if (value4!= -1){
                    value4TextView.setText(MessageFormat.format("{0} {1} %", getString(R.string.SaturationTextRecogn), value4));
                } else {
                    value4TextView.setText(MessageFormat.format("{0} - %", getString(R.string.SaturationTextRecogn)));
                }
                break;
            case "Temperatura ciała":
            case "Body temperature":
                if (value1 != -1){
                    value1TextView.setText(MessageFormat.format("{0} : {1} °C", getString(R.string.Temperature), value1));
                } else {
                    value1TextView.setText(MessageFormat.format("{0} : - °C", getString(R.string.Temperature)));
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
        alertDialogBuilder.setTitle(getString(R.string.EditorQuitQuestion));
        alertDialogBuilder
                .setMessage(getString(R.string.EditorQuitPrompt))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.Yes),
                        (dialog, id) -> {
                            finish();
                        })

                .setNegativeButton(getString(R.string.No), (dialog, id) -> dialog.cancel());

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