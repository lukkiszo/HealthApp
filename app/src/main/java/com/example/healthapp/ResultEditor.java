package com.example.healthapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class ResultEditor extends AppCompatActivity implements DayChosenInterface{

    String[] items;
    String[] itemsPl = {"Cukier", "Ciśnienie krwi, puls i saturacja", "Temperatura ciała", "Harmonogram przyjmowania leku", "Harmonogram wizyt"};
    String[] itemsEn = {"Sugar", "Blood pressure, pulse and saturation", "Body temperature", "Drug intake schedule", "Schedule of visits"};

    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    public static String medicineDayChosen = "";
    public static String medicineDateChosen = "";
    public static String medicineDateFromChosen = "";
    public static String medicineDateToChosen = "";
    private ImageButton addResultFromCamera;
    private String type = "";
    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MainActivity.language.equals("English")){
            items = itemsEn;
        } else {
            items = itemsPl;
        }

        setContentView(R.layout.activity_result_editor);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.ResultEdit);
        }

        textInputLayout = findViewById(R.id.menu);
        autoCompleteTextView = findViewById(R.id.drop_items);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(ResultEditor.this, R.layout.dropdown_item, items);
        autoCompleteTextView.setAdapter(itemAdapter);
        type = intent.getStringExtra("type");
        autoCompleteTextView.setText(type, false);

        if (type.equals(getString(R.string.VisitSchedule))){
            date = intent.getStringExtra("date");
        }

        addResultFromCamera = findViewById(R.id.openCamera);
        addResultFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteTextView.getText().toString().equals(getString(R.string.MedicineSchedule)) || autoCompleteTextView.getText().toString().equals(getString(R.string.VisitSchedule))){
                    Toast.makeText(getApplicationContext(), getString(R.string.NoCameraText), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TextRecognitionActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }

            }
        });

        changeFragment(type, intent.getIntExtra("position", -1), date);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                type = parent.getItemAtPosition(position).toString();

                if (type.equals(getString(R.string.VisitSchedule))){
                    Calendar cal = Calendar.getInstance();
                    date = cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.MONTH) + 1) + " " + cal.get(Calendar.YEAR);
                }

                changeFragment(type, intent.getIntExtra("position", -1), date);
            }
        });

    }

    @Override
    public void onDaySelected(String option) {
        medicineDayChosen = option;
    }

    @Override
    public void onDateFromSelected(String dateFrom) {
        medicineDateFromChosen = dateFrom;
    }

    @Override
    public void onDateToSelected(String dateTo) {
        medicineDateToChosen = dateTo;
    }

    @Override
    public void onDateSelected(String date) {
        medicineDateChosen = date;
    }



    private void changeFragment(String choice, int position, String date){
        // zmiana wyświetlanych fragmentów
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        switch (choice){
            case "Cukier":
            case "Sugar":
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                SugarEditorFragment fragobj = new SugarEditorFragment();
                fragobj.setArguments(bundle);
                ft.replace(R.id.resultEdit, fragobj);
                break;

            case "Ciśnienie krwi, puls i saturacja":
            case "Blood pressure, pulse and saturation":
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position", position);

                BloodPressureEditorFragment bloodPressureEditorFragment = new BloodPressureEditorFragment();
                bloodPressureEditorFragment.setArguments(bundle1);
                ft.replace(R.id.resultEdit, bloodPressureEditorFragment);
                break;

            case "Temperatura ciała":
            case "Body temperature":
                Bundle bundle2 = new Bundle();
                bundle2.putInt("position", position);

                TemperatureEditorFragment temperatureEditorFragment = new TemperatureEditorFragment();
                temperatureEditorFragment.setArguments(bundle2);
                ft.replace(R.id.resultEdit, temperatureEditorFragment);
                break;

            case "Kroki":
            case "Steps":
                Bundle bundle3 = new Bundle();
                bundle3.putInt("position", position);

                StepsEditorFragment stepsEditorFragment = new StepsEditorFragment();
                stepsEditorFragment.setArguments(bundle3);
                ft.replace(R.id.resultEdit, stepsEditorFragment);
                break;

            case "Harmonogram przyjmowania leku":
            case "Drug intake schedule":
                Bundle bundle4 = new Bundle();
                bundle4.putInt("position", position);

                MedicineEditorFragment medicineEditorFragment = new MedicineEditorFragment();
                medicineEditorFragment.setArguments(bundle4);
                ft.replace(R.id.resultEdit, medicineEditorFragment);
                break;

            case "Harmonogram wizyt":
            case "Schedule of visits":
                Bundle bundle5 = new Bundle();
                bundle5.putInt("position", position);
                bundle5.putString("date", date);
                MedicalVisitEditorFragment visitEditorFragment = new MedicalVisitEditorFragment();
                visitEditorFragment.setArguments(bundle5);
                ft.replace(R.id.resultEdit, visitEditorFragment);
                break;


            default:
                break;
        }

        ft.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.EditorQuitQuestion);
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