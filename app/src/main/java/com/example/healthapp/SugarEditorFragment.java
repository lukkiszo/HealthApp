package com.example.healthapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

public class SugarEditorFragment extends Fragment {

    String[] items = {"Nieokreślone", "Przed posiłkiem", "Po posiłku"};
//    private List<String> results = new ArrayList<>();
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    private ArrayList<SugarResult> sugarResultsArrayList;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button timeButton;
    private int hour, minute;
    private int year, month, day;
    EditText result;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private String annot = "";
    private int clickedItem;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        if (getArguments() != null) {
            clickedItem = getArguments().getInt("position");
        }
        loadResults();
        return inflater.inflate(R.layout.fragment_sugar_edit, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initDatePicker();
        dateButton = view.findViewById(R.id.date_picker_button);
        timeButton = view.findViewById(R.id.time_picker_button);
        textInputLayout = view.findViewById(R.id.sugarType);
        autoCompleteTextView = view.findViewById(R.id.sugarType_items);
        result = view.findViewById(R.id.sugarValue);
        addButton = view.findViewById(R.id.confirmResultButton);
        deleteButton = view.findViewById(R.id.deleteResultButton);


        if (clickedItem >= 0){
            dateButton.setText(sugarResultsArrayList.get(clickedItem).getDate());
            timeButton.setText(sugarResultsArrayList.get(clickedItem).getHour());
            autoCompleteTextView.setText(sugarResultsArrayList.get(clickedItem).getAnnotation());
            result.setText("" + (int) sugarResultsArrayList.get(clickedItem).getResult());
        }
        else
        {
            dateButton.setText(getTodayDate());
            timeButton.setText(getCurrentTime());
            autoCompleteTextView.setText(items[0]);
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });


        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker();
            }
        });

        autoCompleteTextView.setText(items[0]);

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, items);
        autoCompleteTextView.setAdapter(itemAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                annot = parent.getItemAtPosition(position).toString();
            }
        });

        result.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "400")});

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteResult();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!result.getText().toString().equals("")){
                    if(annot.equals("")){
                        annot = items[0];
                    }
                    addNewResult(dateButton.getText().toString(), timeButton.getText().toString(), Integer.parseInt(result.getText().toString()), annot);
                }
                else {
                    Toast.makeText(getActivity(), "Proszę uzupełnić wszystkie wartości", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            sugarResultsArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(sugarResultsArrayList);
            editor.putString("sugar", json1);
            editor.apply();
        }
        getActivity().finish();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();

        sugarResultsArrayList = gson.fromJson(json, type);

        if (sugarResultsArrayList == null) {
            sugarResultsArrayList = new ArrayList<>();
        }
    }

    private void addNewResult(String givenDate, String givenHour, int givenResult, String givenAnnotations){

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SugarResult result = new SugarResult(givenDate, givenHour, givenResult, givenAnnotations);
        if(clickedItem >= 0){
            sugarResultsArrayList.remove(clickedItem);
        }
        sugarResultsArrayList.add(result);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(sugarResultsArrayList);
        editor.putString("sugar", json1);
        editor.apply();
        getActivity().finish();
    }

    public static String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public static String getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute);
    }

    public void timePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener,  hour, minute, true);
        timePickerDialog.setTitle("Wybierz czas wykonania badania");
        timePickerDialog.show();
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    public static String makeDateString(int day, int month, int year){
        return day + " " + getMonthFormat(month) + " " + year;
    }

    public static String getMonthFormat(int month){
        switch (month) {
            case 1:
                return "Styczeń";

            case 2:
                return "Luty";

            case 3:
                return "Marzec";

            case 4:
                return "Kwiecień";

            case 5:
                return "Maj";

            case 6:
                return "Czerwiec";

            case 7:
                return "Lipiec";

            case 8:
                return "Sierpień";

            case 9:
                return "Wrzesień";

            case 10:
                return "Październik";

            case 11:
                return "Listopad";

            case 12:
                return "Grudzień";
        }
        return "";
    }

}