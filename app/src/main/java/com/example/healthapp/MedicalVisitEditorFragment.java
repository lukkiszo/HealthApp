package com.example.healthapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class MedicalVisitEditorFragment extends Fragment {
    private int clickedItem;
    private String date = "";
    String[] items = {"Nadchodząca", "Zakończona", "Przełożona"};
    TextInputLayout statusTextInputLayout;
    AutoCompleteTextView statusAutoCompleteTextView;
    private ArrayList<MedicalVisitScheduleItem> medicalVisitScheduleItemArrayList;
    private String statusChoice = "";
    private String name = "";
    private String address = "";
    private String doctor = "";
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button timeButton;
    private int hour, minute;
    private int year, month, day;
    private EditText visitName;
    private EditText visitAddress;
    private EditText visitDoctor;
    private ImageButton addButton;
    private ImageButton deleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedItem = getArguments().getInt("position");
            date = getArguments().getString("date");
        }
        loadResults();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_visit_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        initDatePicker();
        dateButton = view.findViewById(R.id.visit_date_picker_button);
        timeButton = view.findViewById(R.id.visit_time_picker_button);
        statusTextInputLayout = view.findViewById(R.id.visitStatus);
        statusAutoCompleteTextView = view.findViewById(R.id.visitStatus_items);
        addButton = view.findViewById(R.id.confirmVisitButton);
        deleteButton = view.findViewById(R.id.deleteVisitButton);
        visitName = view.findViewById(R.id.visitNameEdit);
        visitAddress = view.findViewById(R.id.visitAddressEdit);
        visitDoctor = view.findViewById(R.id.visitDoctorEdit);

        if (clickedItem >= 0){
            dateButton.setText(medicalVisitScheduleItemArrayList.get(clickedItem).getDate());
            timeButton.setText(medicalVisitScheduleItemArrayList.get(clickedItem).getTime());
            statusAutoCompleteTextView.setText(medicalVisitScheduleItemArrayList.get(clickedItem).getStatus());
            visitName.setText(medicalVisitScheduleItemArrayList.get(clickedItem).getVisitName());
            visitAddress.setText(medicalVisitScheduleItemArrayList.get(clickedItem).getAddress());
            visitDoctor.setText(medicalVisitScheduleItemArrayList.get(clickedItem).getDoctorName());
        }
        else
        {
            dateButton.setText(SugarEditorFragment.makeDateString(Integer.parseInt(date.split(" ")[0]),
                    Integer.parseInt(date.split(" ")[1]), Integer.parseInt(date.split(" ")[2])));
            timeButton.setText(SugarEditorFragment.getCurrentTime());
            statusAutoCompleteTextView.setText(items[0]);
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

        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, items);
        statusAutoCompleteTextView.setAdapter(itemAdapter);
        statusAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                statusChoice = parent.getItemAtPosition(position).toString();
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
                if(!visitName.getText().toString().equals("")){
                    if(statusChoice.equals("")){
                        statusChoice = items[0];
                    }
                    if(!visitAddress.getText().toString().equals("")){
                        address = visitAddress.getText().toString();
                    }
                    if(!visitDoctor.getText().toString().equals("")){
                        doctor = visitDoctor.getText().toString();
                    }

                    addNewResult(dateButton.getText().toString(), timeButton.getText().toString(), visitName.getText().toString(), address, doctor, statusChoice);
                }
                else {
                    Toast.makeText(getActivity(), "Proszę uzupełnić nazwę wizyty", Toast.LENGTH_LONG).show();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            medicalVisitScheduleItemArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(medicalVisitScheduleItemArrayList);
            editor.putString("visits", json1);
            editor.apply();
        }
        getActivity().finish();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("visits", null);

        Type type = new TypeToken<ArrayList<MedicalVisitScheduleItem>>() {}.getType();

        medicalVisitScheduleItemArrayList = gson.fromJson(json, type);

        if (medicalVisitScheduleItemArrayList == null) {
            medicalVisitScheduleItemArrayList = new ArrayList<>();
        }
    }

    private void addNewResult(String givenDate, String givenHour, String givenName, String givenAddress, String givenDoctor, String givenStatus){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        MedicalVisitScheduleItem result = new MedicalVisitScheduleItem(givenDate, givenHour, givenName, givenAddress, givenDoctor, givenStatus);
        if(clickedItem >= 0){
            medicalVisitScheduleItemArrayList.remove(clickedItem);
        }
        medicalVisitScheduleItemArrayList.add(result);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(medicalVisitScheduleItemArrayList);
        editor.putString("visits", json1);
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
        timePickerDialog.setTitle("Wybierz godzinę wizyty");
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

        year = Integer.parseInt(date.split(" ")[2]);
        month = SugarResult.getMonthInt(date.split(" ")[1]);
        day = Integer.parseInt(date.split(" ")[0]);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month - 1, day);
    }

}