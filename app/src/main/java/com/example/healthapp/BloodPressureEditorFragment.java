package com.example.healthapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BloodPressureEditorFragment extends Fragment {
    String[] items = {"Nieokreślone", "Po wysiłku", "W trakcie wysiłku", "Zdenerwowany/a", "Po przebudzeniu"};
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    private int clickedItem;
    private ArrayList<BloodPressureResult> bloodPressureResultArrayList;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button timeButton;
    private int hour, minute;
    private int year, month, day;
    private NumberPicker systolicValuePicker;
    private NumberPicker diastolicValuePicker;
    private EditText pulseValue;
    private EditText saturationValue;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private String annotation = "";

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
        return inflater.inflate(R.layout.fragment_blood_pressure_editor, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        initDatePicker();
        dateButton = view.findViewById(R.id.blood_date_picker_button);
        timeButton = view.findViewById(R.id.blood_time_picker_button);
        systolicValuePicker = view.findViewById(R.id.systolicBloodPressureValue);
        diastolicValuePicker = view.findViewById(R.id.diastolicBloodPressureValue);
        textInputLayout = view.findViewById(R.id.bloodType);
        autoCompleteTextView = view.findViewById(R.id.bloodType_items);
        pulseValue = view.findViewById(R.id.pulseValue);
        saturationValue = view.findViewById(R.id.saturationValue);
        addButton = view.findViewById(R.id.blood_confirmResultButton);
        deleteButton = view.findViewById(R.id.blood_deleteResultButton);

        systolicValuePicker.setMaxValue(220);
        systolicValuePicker.setMinValue(30);
        systolicValuePicker.setValue(120);
        diastolicValuePicker.setMaxValue(180);
        diastolicValuePicker.setMinValue(20);
        diastolicValuePicker.setValue(80);

        if (clickedItem >= 0){
            dateButton.setText(bloodPressureResultArrayList.get(clickedItem).getDate());
            timeButton.setText(bloodPressureResultArrayList.get(clickedItem).getHour());
            int systolicResult = bloodPressureResultArrayList.get(clickedItem).getSystolicResult();
            int diastolicResult = bloodPressureResultArrayList.get(clickedItem).getDiastolicResult();
            int pulseResult = bloodPressureResultArrayList.get(clickedItem).getPulse();
            int saturationResult = bloodPressureResultArrayList.get(clickedItem).getSaturation();

            autoCompleteTextView.setText(bloodPressureResultArrayList.get(clickedItem).getAnnotation());
            pulseValue.setText("" + pulseResult);
            saturationValue.setText("" + saturationResult);
            systolicValuePicker.setValue(systolicResult);
            diastolicValuePicker.setValue(diastolicResult);
        }
        else
        {
            dateButton.setText(SugarEditorFragment.getTodayDate());
            timeButton.setText(SugarEditorFragment.getCurrentTime());
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteResult();
            }
        });

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, items);
        autoCompleteTextView.setAdapter(itemAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                annotation = parent.getItemAtPosition(position).toString();
            }
        });

        pulseValue.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "250")});
        saturationValue.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pulseValue.getText().toString().equals("") && !saturationValue.getText().toString().equals("")){
                    if(annotation.equals("")){
                        annotation = items[0];
                    }
                    addNewResult(dateButton.getText().toString(), timeButton.getText().toString(), systolicValuePicker.getValue(),
                            diastolicValuePicker.getValue(), Integer.parseInt(pulseValue.getText().toString()),
                            Integer.parseInt(saturationValue.getText().toString()), annotation);
                }
                else {
                    Toast.makeText(getActivity(), "Proszę uzupełnić wszystkie wartości", Toast.LENGTH_LONG).show();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("bloodPressure", null);

        Type type = new TypeToken<ArrayList<BloodPressureResult>>() {}.getType();

        bloodPressureResultArrayList = gson.fromJson(json, type);

        if (bloodPressureResultArrayList == null) {
            bloodPressureResultArrayList = new ArrayList<>();
        }

    }

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            bloodPressureResultArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(bloodPressureResultArrayList);
            editor.putString("bloodPressure", json1);
            editor.apply();
        }
        getActivity().finish();
    }

    private void addNewResult(String givenDate, String givenHour, int givenSystolicBloodPressureResult,
                              int givenDiastolicBloodPressureResult, int givenPulseResult, int givenSaturationResult, String givenAnnotations){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        BloodPressureResult result = new BloodPressureResult(givenDate, givenHour, givenSystolicBloodPressureResult,
                givenDiastolicBloodPressureResult, givenPulseResult, givenSaturationResult, givenAnnotations);

        if(clickedItem >= 0){
            bloodPressureResultArrayList.remove(clickedItem);
        }
        bloodPressureResultArrayList.add(result);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(bloodPressureResultArrayList);
        editor.putString("bloodPressure", json1);
        editor.apply();
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
                String date = SugarEditorFragment.makeDateString(day, month, year);
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