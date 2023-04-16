package com.example.healthapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.textfield.TextInputLayout;

public class ResultEditor extends AppCompatActivity implements DayChosenInterface{

    String[] items = {"Cukier", "Ciśnienie krwi, puls i saturacja", "Temperatura ciała", "Harmonogram przyjmowania leku"};

    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    public static String medicineDayChosen = "";
    public static String medicineDateChosen = "";
    public static String medicineDateFromChosen = "";
    public static String medicineDateToChosen = "";
    private ImageButton addResultFromCamera;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_editor);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edycja wyniku");
        }

        textInputLayout = findViewById(R.id.menu);
        autoCompleteTextView = findViewById(R.id.drop_items);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(ResultEditor.this, R.layout.dropdown_item, items);
        autoCompleteTextView.setAdapter(itemAdapter);
        type = intent.getStringExtra("type");
        autoCompleteTextView.setText(type, false);

        addResultFromCamera = findViewById(R.id.openCamera);
        addResultFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteTextView.getText().toString().equals("Harmonogram przyjmowania leku")){
                    Toast.makeText(getApplicationContext(), "Nie ma możliwości dodania rekordu za pomocą kamery", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TextRecognitionActivity.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }

            }
        });

        changeFragment(type, intent.getIntExtra("position", -1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                type = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + type, Toast.LENGTH_LONG).show();


                changeFragment(type, intent.getIntExtra("position", -1));
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
        Log.d("dayChosenDateFrom", medicineDateFromChosen);
    }

    @Override
    public void onDateToSelected(String dateTo) {
        medicineDateToChosen = dateTo;
        Log.d("dayChosenDateTo", medicineDateToChosen);
    }

    @Override
    public void onDateSelected(String date) {
        medicineDateChosen = date;
        Log.d("dayChosenDate", medicineDateChosen);
    }



    private void changeFragment(String choice, int position){
        // zmiana wyświetlanych fragmentów
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        switch (choice){
            case "Cukier":
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                SugarEditorFragment fragobj = new SugarEditorFragment();
                fragobj.setArguments(bundle);
                ft.replace(R.id.resultEdit, fragobj);
                break;

            case "Ciśnienie krwi, puls i saturacja":
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position", position);

                BloodPressureEditorFragment bloodPressureEditorFragment = new BloodPressureEditorFragment();
                bloodPressureEditorFragment.setArguments(bundle1);
                ft.replace(R.id.resultEdit, bloodPressureEditorFragment);
                break;

            case "Temperatura ciała":
                Bundle bundle2 = new Bundle();
                bundle2.putInt("position", position);

                TemperatureEditorFragment temperatureEditorFragment = new TemperatureEditorFragment();
                temperatureEditorFragment.setArguments(bundle2);
                ft.replace(R.id.resultEdit, temperatureEditorFragment);
                break;

            case "Kroki":
                Bundle bundle3 = new Bundle();
                bundle3.putInt("position", position);

                StepsEditorFragment stepsEditorFragment = new StepsEditorFragment();
                stepsEditorFragment.setArguments(bundle3);
                ft.replace(R.id.resultEdit, stepsEditorFragment);
                break;

            case "Harmonogram przyjmowania leku":
                Bundle bundle4 = new Bundle();
                bundle4.putInt("position", position);

                MedicineEditorFragment medicineEditorFragment = new MedicineEditorFragment();
                medicineEditorFragment.setArguments(bundle4);
                ft.replace(R.id.resultEdit, medicineEditorFragment);
                break;

            default:
                break;
        }

        ft.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Czy chcesz odrzucić wprowadzone dane?");
        alertDialogBuilder
                .setMessage("Naciśnięcie przycisku Tak spowoduje usunięcie wprowadzonych zmian!")
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