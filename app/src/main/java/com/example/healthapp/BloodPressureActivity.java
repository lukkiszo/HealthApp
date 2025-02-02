package com.example.healthapp;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.textfield.TextInputLayout;

public class BloodPressureActivity extends AppCompatActivity {
    private ImageButton addNewResultButton;
    String[] items;
    String[] itemsPl = {"Ciśnienie krwi", "Tętno", "Saturacja krwi"};
    String[] itemsEn = {"Blood pressure", "Heart Rate", "Blood Saturation"};
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.language.equals("English")){
            items = itemsEn;
        } else {
            items = itemsPl;
        }

        setContentView(R.layout.activity_blood_pressure);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.BloodParameters);
        }

        textInputLayout = findViewById(R.id.bloodMenu);
        autoCompleteTextView = findViewById(R.id.blood_drop_items);

        addNewResultButton = findViewById(R.id.addBloodPressureResultButton);
        addNewResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewResult();
            }
        });

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(BloodPressureActivity.this, R.layout.dropdown_item, items);
        autoCompleteTextView.setAdapter(itemAdapter);
        autoCompleteTextView.setText(intent.getStringExtra("type"), false);

        changeFragment(intent.getStringExtra("type"));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item = parent.getItemAtPosition(position).toString();
                changeFragment(item);
            }
        });
    }

    private void addNewResult(){
        Intent intent = new Intent(this, ResultEditor.class);
        intent.putExtra("type", getString(R.string.Blood_Pressure_Pulse_Saturation));
        startActivity(intent);
    }

    private void changeFragment(String choice){
        // zmiana wyświetlanych fragmentów
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (choice){
            case "Ciśnienie krwi":
            case "Blood pressure":
                ft.replace(R.id.bloodPressureResults, new BloodPressureResultsFragment());
                break;

            case "Tętno":
            case "Heart Rate":
                ft.replace(R.id.bloodPressureResults, new PulseResultsFragment());
                break;

            case "Saturacja krwi":
            case "Blood Saturation":
                ft.replace(R.id.bloodPressureResults, new SaturationResultsFragment());
                break;

            default:
                break;
        }

        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}