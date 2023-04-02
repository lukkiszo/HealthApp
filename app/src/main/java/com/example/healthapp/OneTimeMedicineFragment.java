package com.example.healthapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
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

public class OneTimeMedicineFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private int year, month, day;
    private int clickedItem;
    private ArrayList<MedicineScheduleItem> medicineScheduleItemArrayList;
    private DayChosenInterface listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedItem = getArguments().getInt("position");
        }
        loadResults();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        listener = (DayChosenInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_time_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDatePicker();
        dateButton = view.findViewById(R.id.once_medicine_date_picker_button);

        if (clickedItem >= 0){
            dateButton.setText(medicineScheduleItemArrayList.get(clickedItem).getDate());
        } else {
            dateButton.setText(SugarEditorFragment.getTodayDate());
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

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

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = SugarEditorFragment.makeDateString(day, month, year);
                dateButton.setText(date);
                listener.onDateSelected(SugarEditorFragment.makeDateString(day, month - 1, year));
            }
        };

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);

        ResultEditor.medicineDateChosen = SugarEditorFragment.makeDateString(day, month, year);
    }
}