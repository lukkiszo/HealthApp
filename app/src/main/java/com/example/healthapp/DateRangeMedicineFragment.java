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

public class DateRangeMedicineFragment extends Fragment {
    private DatePickerDialog datePickerDialogFrom;
    private Button dateButtonFrom;
    private DatePickerDialog datePickerDialogTo;
    private Button dateButtonTo;
    private int yearFrom, monthFrom, dayFrom, yearTo, monthTo, dayTo;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_range_medicine, container, false);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        listener = (DayChosenInterface) context;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDateFromPicker();
        initDateToPicker();
        dateButtonFrom = view.findViewById(R.id.medicine_dateFrom_picker_button);
        dateButtonTo = view.findViewById(R.id.medicine_dateTo_picker_button);

        if (clickedItem >= 0){
            dateButtonFrom.setText(medicineScheduleItemArrayList.get(clickedItem).getDateFrom());
            dateButtonTo.setText(medicineScheduleItemArrayList.get(clickedItem).getDateTo());
        } else {
            dateButtonFrom.setText(Utils.getTodayDate());
            dateButtonTo.setText(Utils.getTodayDate());
        }

        dateButtonFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogFrom.show();
            }
        });

        dateButtonTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogTo.show();
            }
        });

    }

    private void initDateFromPicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = Utils.makeDateString(day, month, year);
                dateButtonFrom.setText(date);
                listener.onDateFromSelected(Utils.makeDateString(day, month - 1, year));
            }
        };

        Calendar cal = Calendar.getInstance();
        yearFrom = cal.get(Calendar.YEAR);
        monthFrom = cal.get(Calendar.MONTH);
        dayFrom = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialogFrom = new DatePickerDialog(getActivity(), style, dateSetListener, yearFrom, monthFrom, dayFrom);
        datePickerDialogFrom.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);

        ResultEditor.medicineDateFromChosen = Utils.makeDateString(dayFrom, monthFrom, yearFrom);
    }

    private void initDateToPicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = Utils.makeDateString(day, month, year);
                dateButtonTo.setText(date);
                listener.onDateToSelected(Utils.makeDateString(day, month - 1, year));
            }
        };

        Calendar cal = Calendar.getInstance();
        yearTo = cal.get(Calendar.YEAR);
        monthTo = cal.get(Calendar.MONTH);
        dayTo = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialogTo = new DatePickerDialog(getActivity(), style, dateSetListener, yearTo, monthTo, dayTo);
        datePickerDialogTo.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
        ResultEditor.medicineDateToChosen = Utils.makeDateString(dayTo, monthTo, yearTo);
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
}