package com.example.healthapp;

import android.app.AlertDialog;
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
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MedicineEditorFragment extends Fragment{
    String[] items = {"-", "Na czczo", "Przed posiłkiem", "Po posiłku"};
    String[] periodicity = {"Jednorazowo", "Cyklicznie (co tydzień)", "Codziennie", "W zakresie dat"};
    TextInputLayout periodicityTextInputLayout;
    AutoCompleteTextView periodicityAutoCompleteTextView;
    TextInputLayout additionalInfoTextInputLayout;
    AutoCompleteTextView additionalInfoAutoCompleteTextView;
    private int clickedItem;
    private ArrayList<MedicineScheduleItem> medicineScheduleItemArrayList;
    private String periodicityChoice = "";
    private String annotation = "";
    private Button timeButton;
    private int hour, minute;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private EditText medicineNameEditText;
    private EditText doseEditText;
    private String dayChosen = "";

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
        return inflater.inflate(R.layout.fragment_medicine_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        periodicityTextInputLayout = view.findViewById(R.id.receptionType);
        periodicityAutoCompleteTextView = view.findViewById(R.id.receptionType_items);
        additionalInfoTextInputLayout = view.findViewById(R.id.additionalInfo);
        additionalInfoAutoCompleteTextView = view.findViewById(R.id.additionalInfo_items);
        timeButton = view.findViewById(R.id.medicine_time_picker_button);
        addButton = view.findViewById(R.id.confirmMedicineButton);
        deleteButton = view.findViewById(R.id.deleteMedicineButton);
        medicineNameEditText = view.findViewById(R.id.medicineNameEdit);
        doseEditText = view.findViewById(R.id.doseValue);

        if (clickedItem >= 0){
            periodicityChoice = medicineScheduleItemArrayList.get(clickedItem).getPeriodicity();
            additionalInfoAutoCompleteTextView.setText(medicineScheduleItemArrayList.get(clickedItem).getAnnotation());
            timeButton.setText(medicineScheduleItemArrayList.get(clickedItem).getTime());
            medicineNameEditText.setText(medicineScheduleItemArrayList.get(clickedItem).getMedicineName());
            doseEditText.setText(medicineScheduleItemArrayList.get(clickedItem).getDose());
            if (!medicineScheduleItemArrayList.get(clickedItem).getPeriodicity().equals("")){
                periodicityAutoCompleteTextView.setText(medicineScheduleItemArrayList.get(clickedItem).getPeriodicity());
                changeFragment(medicineScheduleItemArrayList.get(clickedItem).getPeriodicity(), clickedItem);
            } else {
                periodicityChoice = periodicity[0];
                periodicityAutoCompleteTextView.setText(periodicityChoice);
                changeFragment(periodicityChoice, clickedItem);
            }
        }
        else {
            periodicityAutoCompleteTextView.setText(periodicity[0]);
            periodicityChoice = periodicity[0];
            additionalInfoAutoCompleteTextView.setText(items[0]);
            timeButton.setText(SugarEditorFragment.getCurrentTime());
            changeFragment(periodicity[0], clickedItem);
        }


        ArrayAdapter<String> periodicityAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, periodicity);
        periodicityAutoCompleteTextView.setAdapter(periodicityAdapter);
        periodicityAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                periodicityChoice = parent.getItemAtPosition(position).toString();
                changeFragment(periodicityChoice, clickedItem);
            }
        });

        ArrayAdapter<String> additionalInfoAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, items);
        additionalInfoAutoCompleteTextView.setAdapter(additionalInfoAdapter);
        additionalInfoAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                annotation = parent.getItemAtPosition(position).toString();
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
                if(!medicineNameEditText.getText().toString().equals("") && !doseEditText.getText().toString().equals("")){
                    if(annotation.equals("")){
                        annotation = items[0];
                    }

                    switch (periodicityChoice) {
                        case "Jednorazowo":
                            addNewOneTimeMedicine(ResultEditor.medicineDateChosen, timeButton.getText().toString(), medicineNameEditText.getText().toString(), doseEditText.getText().toString(), annotation);
                            break;
                        case "Cyklicznie (co tydzień)":
                            addNewCyclicMedicine(ResultEditor.medicineDayChosen, timeButton.getText().toString(), medicineNameEditText.getText().toString(), doseEditText.getText().toString(), annotation);
                            break;
                        case "Codziennie":
                            addNewEveryDayMedicine(timeButton.getText().toString(), medicineNameEditText.getText().toString(), doseEditText.getText().toString(), annotation);
                            break;
                        case "W zakresie dat":
                            addNewRangeMedicine(ResultEditor.medicineDateFromChosen, ResultEditor.medicineDateToChosen, timeButton.getText().toString(), medicineNameEditText.getText().toString(), doseEditText.getText().toString(), annotation);
                            break;
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Proszę uzupełnić wszystkie wartości", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void addNewOneTimeMedicine(String date, String hour, String name, String dose, String annot){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        MedicineScheduleItem medicineScheduleItem = new MedicineScheduleItem(date, hour, name, dose, annot);
        if(clickedItem >= 0){
            medicineScheduleItemArrayList.remove(clickedItem);
        }
        medicineScheduleItemArrayList.add(medicineScheduleItem);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(medicineScheduleItemArrayList);
        editor.putString("medicines", json1);
        editor.apply();
        getActivity().finish();
    }

    private void addNewRangeMedicine(String dateFrom, String dateTo, String hour, String name, String dose, String annot){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        MedicineScheduleItem medicineScheduleItem = new MedicineScheduleItem("W zakresie dat", dateFrom, dateTo, hour, name, dose, annot);
        if(clickedItem >= 0){
            medicineScheduleItemArrayList.remove(clickedItem);
        }
        medicineScheduleItemArrayList.add(medicineScheduleItem);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(medicineScheduleItemArrayList);
        editor.putString("medicines", json1);
        editor.apply();
        getActivity().finish();
    }

    private void addNewEveryDayMedicine(String hour, String name, String dose, String annot){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        MedicineScheduleItem medicineScheduleItem = new MedicineScheduleItem("Codziennie", "", hour, name, dose, annot);
        if(clickedItem >= 0){
            medicineScheduleItemArrayList.remove(clickedItem);
        }
        medicineScheduleItemArrayList.add(medicineScheduleItem);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(medicineScheduleItemArrayList);
        editor.putString("medicines", json1);
        editor.apply();
        getActivity().finish();
    }

    private void addNewCyclicMedicine(String day, String hour, String name, String dose, String annot){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        MedicineScheduleItem medicineScheduleItem = new MedicineScheduleItem("Cyklicznie", day, hour, name, dose, annot);
        if(clickedItem >= 0){
            medicineScheduleItemArrayList.remove(clickedItem);
        }
        medicineScheduleItemArrayList.add(medicineScheduleItem);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(medicineScheduleItemArrayList);
        editor.putString("medicines", json1);
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

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            medicineScheduleItemArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(medicineScheduleItemArrayList);
            editor.putString("medicines", json1);
            editor.apply();
        }
        getActivity().finish();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("medicines", null);

        Type type = new TypeToken<ArrayList<MedicineScheduleItem>>() {}.getType();

        medicineScheduleItemArrayList = gson.fromJson(json, type);

        if (medicineScheduleItemArrayList == null) {
            medicineScheduleItemArrayList = new ArrayList<>();
        }
    }

    private void changeFragment(String choice, int position){
        // zmiana wyświetlanych fragmentów
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        switch (choice){
            case "Jednorazowo":
                Bundle bundle = new Bundle();
                bundle.putInt("position", clickedItem);

                OneTimeMedicineFragment oneTimeMedicineFragment = new OneTimeMedicineFragment();
                oneTimeMedicineFragment.setArguments(bundle);
                ft.replace(R.id.additionalInfoMedicine, oneTimeMedicineFragment);
                break;

            case "Cyklicznie (co tydzień)":
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position", clickedItem);

                OnceAWeekMedicineFragment onceAWeekMedicineFragment = new OnceAWeekMedicineFragment();
                onceAWeekMedicineFragment.setArguments(bundle1);
                ft.replace(R.id.additionalInfoMedicine, onceAWeekMedicineFragment);
                break;

            case "Codziennie":
                ft.replace(R.id.additionalInfoMedicine, new Fragment());
                break;

            case "W zakresie dat":
                Bundle bundle3 = new Bundle();
                bundle3.putInt("position", clickedItem);

                DateRangeMedicineFragment dateRangeMedicineFragment = new DateRangeMedicineFragment();
                dateRangeMedicineFragment.setArguments(bundle3);
                ft.replace(R.id.additionalInfoMedicine, dateRangeMedicineFragment);
                break;

            default:
                break;
        }
        ft.commit();
    }
}