package com.example.healthapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
    String[] items;
    String[] itemsPl = {"Nadchodząca", "Zakończona", "Przełożona"};
    String[] itemsEn = {"Upcoming", "Completed", "Rescheduled"};
    TextInputLayout statusTextInputLayout;
    AutoCompleteTextView statusAutoCompleteTextView;
    private ArrayList<MedicalVisitScheduleItem> medicalVisitScheduleItemArrayList;
    private String statusChoice = "";
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

        if (MainActivity.language.equals("English")){
            items = itemsEn;
        } else {
            items = itemsPl;
        }

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
            dateButton.setText(Utils.makeDateString(Integer.parseInt(date.split(" ")[0]),
                    Integer.parseInt(date.split(" ")[1]), Integer.parseInt(date.split(" ")[2])));
            timeButton.setText(Utils.getCurrentTime());
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
                    Toast.makeText(getActivity(), getString(R.string.FillValuesOfVisit), Toast.LENGTH_LONG).show();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void deleteResult(){
        if(clickedItem >= 0){
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MainActivity.schedulePreferencesName, Context.MODE_PRIVATE);
            Gson gson = new Gson();

            MedicalVisitScheduleItem result = medicalVisitScheduleItemArrayList.get(clickedItem);
            medicalVisitScheduleItemArrayList.remove(clickedItem);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String json1 = gson.toJson(medicalVisitScheduleItemArrayList);
            editor.putString("visits", json1);
            editor.apply();
            deleteNotifications(result.getNumberId());
        }
        getActivity().finish();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MainActivity.schedulePreferencesName, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("visits", null);

        Type type = new TypeToken<ArrayList<MedicalVisitScheduleItem>>() {}.getType();

        medicalVisitScheduleItemArrayList = gson.fromJson(json, type);

        if (medicalVisitScheduleItemArrayList == null) {
            medicalVisitScheduleItemArrayList = new ArrayList<>();
        }
    }

    private void addNewResult(String givenDate, String givenHour, String givenName, String givenAddress, String givenDoctor, String givenStatus){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MainActivity.schedulePreferencesName, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        MedicalVisitScheduleItem result = new MedicalVisitScheduleItem(givenDate, givenHour, givenName, givenAddress, givenDoctor, givenStatus);
        if(clickedItem >= 0){
            MedicalVisitScheduleItem resultToDelete = medicalVisitScheduleItemArrayList.get(clickedItem);
            medicalVisitScheduleItemArrayList.remove(clickedItem);
            deleteNotifications(resultToDelete.getNumberId());
        }
        medicalVisitScheduleItemArrayList.add(result);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(medicalVisitScheduleItemArrayList);
        editor.putString("visits", json1);
        editor.apply();
        addNotifications(result);
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
        timePickerDialog.setTitle(getString(R.string.PickVisitTime));
        timePickerDialog.show();
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date;
                if (MainActivity.language.equals("English")){
                    date = Utils.makeDateStringEnglish(day, month, year);
                } else {
                    date = Utils.makeDateString(day, month, year);
                }
                dateButton.setText(date);
            }
        };

        year = Integer.parseInt(date.split(" ")[2]);
        month = Utils.getMonthInt(date.split(" ")[1]);
        day = Integer.parseInt(date.split(" ")[0]);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month - 1, day);
    }

    private void deleteNotifications(int id){
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), VisitReminderReceiver.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_MUTABLE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        Intent intent1 = new Intent(getActivity(), VisitReminderReceiver.class);
        intent1.putExtra("id", -1 * id);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getActivity(), -1 * id, intent1, PendingIntent.FLAG_MUTABLE);

        alarmManager.cancel(pendingIntent1);
        pendingIntent1.cancel();
    }

    private void addNotifications(MedicalVisitScheduleItem item){
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);

        // powiadomienie godzine przed wizyta
        Calendar calendar = Calendar.getInstance();
        calendar.set(item.getYear(), item.getMonth() - 1, item.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, item.getHour() - 1);
        calendar.set(Calendar.MINUTE, item.getMinutes());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getActivity(), VisitReminderReceiver.class);
        intent.putExtra("id", item.getNumberId());
        intent.putExtra("time", "hour");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), item.getNumberId(), intent, PendingIntent.FLAG_MUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        // powiadomienie dzien przed wizyta
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(item.getYear(), item.getMonth() - 1, item.getDay());
        calendar1.add(Calendar.DATE, -1);
        calendar1.set(Calendar.HOUR_OF_DAY, item.getHour());
        calendar1.set(Calendar.MINUTE, item.getMinutes());
        calendar1.set(Calendar.SECOND, 0);

        Intent intent1 = new Intent(getActivity(), VisitReminderReceiver.class);
        intent1.putExtra("id", item.getNumberId());
        intent1.putExtra("time", "day");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getActivity(), -1 * item.getNumberId(), intent1, PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
        }
    }

}