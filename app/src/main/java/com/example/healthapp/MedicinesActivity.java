package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.TagLostException;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class MedicinesActivity extends AppCompatActivity {
    private ImageButton addNewMedicineButton;
    private ListView listView;
    private List<String> medicines = new ArrayList<>();
    private ArrayList<MedicineScheduleItem> medicineScheduleItemArrayList;
    private ArrayList<MedicineScheduleItem> listViewArrayList;
    private Button previousListViewResultsButton;
    private Button nextListViewResultsButton;
    private TextView dateText;
    private Date todayDate;
    private int year, month, day, dayOfWeek;
    private Calendar cal;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicines);

        Toolbar toolbar = (Toolbar) findViewById(R.id.medicines_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Harmonogram leków");
        }

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        dateText = findViewById(R.id.medicine_dateText);
        dateText.setText(String.format("%s\n%s", Utils.getDayString(dayOfWeek - 1), Utils.makeDateString(day, month + 1, year)));

        listView = findViewById(R.id.medicines_listview);

        previousListViewResultsButton = findViewById(R.id.date_dayBefore);
        previousListViewResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayBefore();
            }
        });

        nextListViewResultsButton = findViewById(R.id.date_dayAfter);
        nextListViewResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayAfter();
            }
        });

        addNewMedicineButton = findViewById(R.id.addMedicineScheduleButton);
        addNewMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMedicine();
            }
        });
        loadMedicines();
        getDailyMedicines();
        reloadListView();
    }

    private void addNewMedicine(){
        Intent intent = new Intent(this, ResultEditor.class);
        intent.putExtra("type", "Harmonogram przyjmowania leku");
        startActivity(intent);
    }

    private void dayBefore(){
        cal.add(Calendar.DAY_OF_YEAR, -1);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        dateText.setText(String.format("%s\n%s", Utils.getDayString(dayOfWeek - 1), Utils.makeDateString(day, month + 1, year)));
        getDailyMedicines();
    }

    private void dayAfter(){
        cal.add(Calendar.DAY_OF_YEAR, 1);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        dateText.setText(String.format("%s\n%s", Utils.getDayString(dayOfWeek - 1), Utils.makeDateString(day, month + 1, year)));
        getDailyMedicines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
        getDailyMedicines();
        reloadListView();
    }

    @SuppressLint("DefaultLocale")
    private void getDailyMedicines(){
        listViewArrayList = new ArrayList<>();

        for (MedicineScheduleItem medicineItem : medicineScheduleItemArrayList) {
            if (medicineItem.getPeriodicity().equals("Codziennie")){
                listViewArrayList.add(medicineItem);
            } else if (medicineItem.getPeriodicity().equals("Cyklicznie") && medicineItem.getDayOfWeek().equals(Utils.getDayString(dayOfWeek - 1))){
                listViewArrayList.add(medicineItem);
            } else if (medicineItem.getPeriodicity().equals("W zakresie dat")){
                Calendar medicineDateFrom = Calendar.getInstance();
                medicineDateFrom.set(medicineItem.getYearFrom(), medicineItem.getMonthFrom(), medicineItem.getDayFrom());
                Calendar medicineDateTo = Calendar.getInstance();
                medicineDateTo.set(medicineItem.getYearTo(), medicineItem.getMonthTo(), medicineItem.getDayTo());
                Calendar currentDate = Calendar.getInstance();
                currentDate.set(year, month, day);

                if (currentDate.compareTo(medicineDateFrom) >= 0 && currentDate.compareTo(medicineDateTo) <= 0){
                    listViewArrayList.add(medicineItem);
                }
            } else if (medicineItem.getDay() == day && medicineItem.getMonth() == month && medicineItem.getYear() == year){
                // jednorazowe
                listViewArrayList.add(medicineItem);
            }

        }

        reloadListView();
    }

    private void loadMedicines(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("medicines", null);

        Type type = new TypeToken<ArrayList<MedicineScheduleItem>>() {}.getType();

        medicineScheduleItemArrayList = gson.fromJson(json, type);

        if (medicineScheduleItemArrayList == null) {
            medicineScheduleItemArrayList = new ArrayList<>();
        }
    }

    private ArrayList<MedicineScheduleItem> sortMedicineItems(ArrayList<MedicineScheduleItem> items){
        SharedPreferences sharedPreferences = this.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<MedicineScheduleItem> sorted = SortHelperClass.sortMedicineItems(items);
        String json1 = gson.toJson(sorted);
        editor.putString("medicines", json1);
        editor.apply();
        return sorted;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        medicines.clear();

        medicineScheduleItemArrayList = sortMedicineItems(medicineScheduleItemArrayList);
        listViewArrayList = SortHelperClass.sortMedicineItems(listViewArrayList);

        for (MedicineScheduleItem item : listViewArrayList) {
            medicines.add("Lek : " + item.getMedicineName() + "\nGodzina : " + item.getTime() + "\nDawka: " + item.getDose());
        }

        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, R.layout.medicine_item, medicines);
        listView.setAdapter(arr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                int index = -1;
                for (int i = 0; i < medicineScheduleItemArrayList.size(); i++) {
                    if (medicineScheduleItemArrayList.get(i).getIdNumber() == listViewArrayList.get(position).getIdNumber()) {
                        index = i;
                        break;
                    }
                }

                Intent intent = new Intent(getApplicationContext(), ResultEditor.class);
                intent.putExtra("type", "Harmonogram przyjmowania leku");
                intent.putExtra("position", index);
                startActivity(intent);
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });

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