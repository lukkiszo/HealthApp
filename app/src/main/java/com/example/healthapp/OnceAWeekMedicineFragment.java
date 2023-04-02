package com.example.healthapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Objects;

public class OnceAWeekMedicineFragment extends Fragment {
    private ArrayList<MedicineScheduleItem> medicineScheduleItemArrayList;
    private int clickedItem;
    private String dayChosen = "";
    String[] days = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
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
        return inflater.inflate(R.layout.fragment_once_a_week_medicine, container, false);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        listener = (DayChosenInterface) context;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInputLayout = view.findViewById(R.id.medicineDayChoice);
        autoCompleteTextView = view.findViewById(R.id.medicineDayChoice_items);
        if (clickedItem >= 0){
            autoCompleteTextView.setText(medicineScheduleItemArrayList.get(clickedItem).getDayOfWeek());
            ResultEditor.medicineDayChosen = medicineScheduleItemArrayList.get(clickedItem).getDayOfWeek();
        } else {
            autoCompleteTextView.setText(days[0]);
            ResultEditor.medicineDayChosen = days[0];
        }

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, days);
        autoCompleteTextView.setAdapter(daysAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                listener.onDaySelected(parent.getItemAtPosition(position).toString());
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
}