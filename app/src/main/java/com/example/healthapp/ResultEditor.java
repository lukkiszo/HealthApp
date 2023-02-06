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

public class ResultEditor extends AppCompatActivity{

    String[] items = {"Cukier", "Ciśnienie krwi, puls i saturacja", "Temperatura ciała"};

    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;

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
        autoCompleteTextView.setText(intent.getStringExtra("type"), false);

        changeFragment(intent.getStringExtra("type"), intent.getIntExtra("position", -1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

                changeFragment(item, intent.getIntExtra("position", -1));

            }
        });

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
                break;

            case "Temperatura ciała":
                Bundle bundle2 = new Bundle();
                bundle2.putInt("position", position);

                TemperatureEditorFragment temperatureEditorFragment = new TemperatureEditorFragment();
                temperatureEditorFragment.setArguments(bundle2);
                ft.replace(R.id.resultEdit, temperatureEditorFragment);
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