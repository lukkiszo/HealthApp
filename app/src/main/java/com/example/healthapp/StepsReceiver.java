package com.example.healthapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class StepsReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;
    private ArrayList<StepsResult> stepsResultArrayList = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private Boolean isCounterSensorPresent;
    private int stepCount = 0;
    private Context currentContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        currentContext = context;
        sharedPreferences = context.getSharedPreferences("results", Context.MODE_PRIVATE);
        stepCount = intent.getIntExtra("steps", 0);
        saveStepsResults();
    }

    private void saveStepsResults(){
        int lastDaySteps;
        Gson gson = new Gson();
        String json = sharedPreferences.getString("steps", null);
        Type type = new TypeToken<ArrayList<StepsResult>>() {}.getType();

        stepsResultArrayList = gson.fromJson(json, type);

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, -1);
        dt = c.getTime();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dt);
        int day = Integer.parseInt(currentDate.split("-")[0]);
        int month = Integer.parseInt(currentDate.split("-")[1]);
        int year = Integer.parseInt(currentDate.split("-")[2]);

        StepsResult result;

        if (stepsResultArrayList == null || stepsResultArrayList.size() == 0) {
            stepsResultArrayList = new ArrayList<>();
            result = new StepsResult(day + " " + Utils.getMonthFormat(month) + " " + year, stepCount, stepCount);
            stepsResultArrayList.add(result);
        } else if (stepsResultArrayList.get(stepsResultArrayList.size() - 1).getDay() != day
                || stepsResultArrayList.get(stepsResultArrayList.size() - 1).getMonth() != month){
            lastDaySteps = stepsResultArrayList.get(stepsResultArrayList.size() - 1).getAbsoluteResult();

            if (lastDaySteps > stepCount){
                result = new StepsResult(day + " " + Utils.getMonthFormat(month) + " " + year, stepCount, stepCount);
            } else {
                result = new StepsResult(day + " " + Utils.getMonthFormat(month) + " " + year, stepCount - lastDaySteps, stepCount);
            }
            stepsResultArrayList.add(result);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(stepsResultArrayList);
        editor.putString("steps", json1);
        editor.apply();
    }
}
