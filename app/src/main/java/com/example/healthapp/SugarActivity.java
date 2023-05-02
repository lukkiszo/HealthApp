package com.example.healthapp;

import android.content.Intent;
import android.view.*;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class SugarActivity extends AppCompatActivity {
    private ImageButton addNewResultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.sugarResults, new SugarResultsFragment());
        ft.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.SugarAndBMI);
        }

        addNewResultButton = findViewById(R.id.addSugarResultButton);
        addNewResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewResult();
            }
        });

    }

    private void addNewResult(){
        Intent intent = new Intent(this, ResultEditor.class);
        intent.putExtra("type", getString(R.string.Sugar));
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