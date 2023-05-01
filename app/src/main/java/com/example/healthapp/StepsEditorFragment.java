package com.example.healthapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
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
import java.util.Objects;

public class StepsEditorFragment extends Fragment {
    private ArrayList<StepsResult> stepsResultArrayList;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private int year, month, day;
    private NumberPicker valuePicker;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private int clickedItem;

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
        return inflater.inflate(R.layout.fragment_steps_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDatePicker();
        dateButton = view.findViewById(R.id.steps_date_picker_button);
        valuePicker = view.findViewById(R.id.steps_value);
        valuePicker.setMaxValue(100000);
        valuePicker.setMinValue(0);
        addButton = view.findViewById(R.id.confirmStepsResultButton);
        deleteButton = view.findViewById(R.id.deleteStepsResultButton);

        if (clickedItem >= 0){
            dateButton.setText(stepsResultArrayList.get(clickedItem).getDate());
            double value = stepsResultArrayList.get(clickedItem).getResult();
            valuePicker.setValue((int) value);
        }
        else
        {
            dateButton.setText(Utils.getTodayDate());
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
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
                int result = valuePicker.getValue();
                editResult(dateButton.getText().toString(), result);
            }
        });

    }

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            stepsResultArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(stepsResultArrayList);
            editor.putString("steps", json1);
            editor.apply();
        }
        getActivity().finish();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("steps", null);

        Type type = new TypeToken<ArrayList<StepsResult>>() {}.getType();

        stepsResultArrayList = gson.fromJson(json, type);

        if (stepsResultArrayList == null) {
            stepsResultArrayList = new ArrayList<>();
        }
    }

    private void editResult(String givenDate, int givenResult){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        StepsResult result = new StepsResult(givenDate, givenResult, stepsResultArrayList.get(clickedItem).getAbsoluteResult());
        if(clickedItem >= 0){
            stepsResultArrayList.remove(clickedItem);
        }
        stepsResultArrayList.add(result);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(stepsResultArrayList);
        editor.putString("steps", json1);
        editor.apply();
        getActivity().finish();
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