package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class VisitScheduleActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ImageButton addNewVisitButton;
    private ArrayList<MedicalVisitScheduleItem> medicalVisitScheduleItems = new ArrayList<>();
    private String date = "";
    private Calendar cal;
    private ListView listView;
    private List<String> results = new ArrayList<>();
    private ArrayList<MedicalVisitScheduleItem> listViewArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.visit_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Harmonogram wizyt");
        }

        loadResults();
        cal = Calendar.getInstance();
        date = cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.MONTH) + 1) + " " + cal.get(Calendar.YEAR);

        calendarView = findViewById(R.id.visit_calendar);
        addNewVisitButton = findViewById(R.id.addVisitScheduleButton);
        listView = findViewById(R.id.visit_listview);
        reloadListView();

        addNewVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewVisit();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i2 + " " + (i1 + 1) + " " + i;
                reloadListView();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResults();
        reloadListView();
    }

    private ArrayList<MedicalVisitScheduleItem> sortVisits(ArrayList<MedicalVisitScheduleItem> results){
        SharedPreferences sharedPreferences = this.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(SortHelperClass.sortVisits(results));
        editor.putString("visits", json1);
        editor.apply();
        return SortHelperClass.sortVisits(results);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        results.clear();

        listViewArrayList = new ArrayList<>();

        for (MedicalVisitScheduleItem visit : medicalVisitScheduleItems){
            if (visit.getDate().equals(Utils.makeDateString(Integer.parseInt(date.split(" ")[0]),
                    Integer.parseInt(date.split(" ")[1]), Integer.parseInt(date.split(" ")[2])))){
                listViewArrayList.add(visit);
            }
        }

        medicalVisitScheduleItems = sortVisits(medicalVisitScheduleItems);
        listViewArrayList = SortHelperClass.sortVisits(listViewArrayList);

        for (MedicalVisitScheduleItem visit : listViewArrayList) {
            results.add(visit.getVisitName() + " - " + visit.getDate() + ", " + visit.getTime() + "\nAdres: " + visit.getAddress() + "\nLekarz: " + visit.getDoctorName());
        }

        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, R.layout.medicine_item, results);
        listView.setAdapter(arr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                int index = -1;
                for (int i = 0; i < medicalVisitScheduleItems.size(); i++) {
                    if (medicalVisitScheduleItems.get(i).getNumberId() == listViewArrayList.get(position).getNumberId()) {
                        index = i;
                        break;
                    }
                }

                Intent intent = new Intent(getApplicationContext(), ResultEditor.class);
                intent.putExtra("type", "Harmonogram wizyt");
                intent.putExtra("position", index);
                intent.putExtra("date", medicalVisitScheduleItems.get(index).getDate());

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

    private void loadResults(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("visits", null);

        Type type = new TypeToken<ArrayList<MedicalVisitScheduleItem>>() {}.getType();

        medicalVisitScheduleItems = gson.fromJson(json, type);

        if (medicalVisitScheduleItems == null) {
            medicalVisitScheduleItems = new ArrayList<>();
        }
        Log.d("wizyta", String.valueOf(medicalVisitScheduleItems.size()));
    }

    private void addNewVisit(){
        Intent intent = new Intent(this, ResultEditor.class);
        intent.putExtra("type", "Harmonogram wizyt");
        intent.putExtra("date", date);
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