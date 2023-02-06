package com.example.healthapp;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class TemperatureActivity extends AppCompatActivity {
    private ImageButton addNewResultButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.temperatureResults, new TemperatureResultsFragment());
        ft.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.temperature_activity_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Temperatura");
        }

        addNewResultButton = findViewById(R.id.addTemperatureResultButton);
        addNewResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewResult();
            }
        });

    }

    private void addNewResult(){
        Intent intent = new Intent(this, ResultEditor.class);
        intent.putExtra("type", "Temperatura ciała");
        startActivity(intent);
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