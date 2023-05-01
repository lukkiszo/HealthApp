package com.example.healthapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class TemperatureEditorFragment extends Fragment {
    private ArrayList<TemperatureResult> temperatureResultArrayList;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button timeButton;
    private int hour, minute;
    private int year, month, day;
    private NumberPicker valuePicker;
    private NumberPicker value1Picker;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private int clickedItem;
    public static double valueFromCamera = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedItem = getArguments().getInt("position");
        }
        loadResults();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_temperature_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        initDatePicker();
        dateButton = view.findViewById(R.id.temperature_date_picker_button);
        timeButton = view.findViewById(R.id.temperature_time_picker_button);
        valuePicker = view.findViewById(R.id.temperature_value1);
        value1Picker = view.findViewById(R.id.temperature_value2);
        valuePicker.setMaxValue(45);
        valuePicker.setMinValue(30);
        valuePicker.setValue(36);
        value1Picker.setMaxValue(9);
        value1Picker.setMinValue(0);
        value1Picker.setValue(6);
        addButton = view.findViewById(R.id.confirmTemperatureResultButton);
        deleteButton = view.findViewById(R.id.deleteTemperatureResultButton);

        if (clickedItem >= 0){
            dateButton.setText(temperatureResultArrayList.get(clickedItem).getDate());
            timeButton.setText(temperatureResultArrayList.get(clickedItem).getHour());
            double value = temperatureResultArrayList.get(clickedItem).getResult();
            valuePicker.setValue((int) Math.floor(value));
            value1Picker.setValue((int) (10*(value - Math.floor(value))));
        }
        else
        {
            dateButton.setText(Utils.getTodayDate());
            timeButton.setText(Utils.getCurrentTime());
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteResult();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = valuePicker.getValue() + "." + value1Picker.getValue();
                double result1 = Double.parseDouble(result);
                addNewResult(dateButton.getText().toString(), timeButton.getText().toString(), result1);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            temperatureResultArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(temperatureResultArrayList);
            editor.putString("temperature", json1);
            editor.apply();
        }
        valueFromCamera = -1;
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (valuePicker != null && value1Picker != null){
            if (valueFromCamera != -1){
                valuePicker.setValue((int) Math.floor(valueFromCamera));
                value1Picker.setValue((int) (10*(valueFromCamera - Math.floor(valueFromCamera))));
            }
        }
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("temperature", null);

        Type type = new TypeToken<ArrayList<TemperatureResult>>() {}.getType();

        temperatureResultArrayList = gson.fromJson(json, type);

        if (temperatureResultArrayList == null) {
            temperatureResultArrayList = new ArrayList<>();
        }

    }

    private void addNewResult(String givenDate, String givenHour, double givenResult){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        TemperatureResult result = new TemperatureResult(givenDate, givenHour, givenResult);
        if(clickedItem >= 0){
            temperatureResultArrayList.remove(clickedItem);
        }
        temperatureResultArrayList.add(result);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(temperatureResultArrayList);
        editor.putString("temperature", json1);
        editor.apply();
        valueFromCamera = -1;
        getActivity().finish();
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
                String date = Utils.makeDateString(day, month, year);
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


}