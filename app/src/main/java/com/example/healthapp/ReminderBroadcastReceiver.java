package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;
    private ArrayList<SugarResult> sugarResultArrayList = new ArrayList<>();
    private ArrayList<BloodPressureResult> bloodPressureResultArrayList = new ArrayList<>();
    private ArrayList<TemperatureResult> temperatureResultArrayList = new ArrayList<>();
    private SugarResult lastSugarResult;
    private BloodPressureResult lastBloodPressureResult;
    private TemperatureResult lastTemperatureResult;
    private Context currentContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        currentContext = context;
        sharedPreferences = context.getSharedPreferences(MainActivity.resultsPreferencesName, Context.MODE_PRIVATE);

        if (!checkLastAddedResult()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(currentContext, "My Notification")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle(context.getString(R.string.RemindNotifTitle))
                    .setContentText(context.getString(R.string.RemindNotifText))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(currentContext);
            managerCompat.notify(1010, builder.build());
        }
    }

    @SuppressLint("SetTextI18n")
    private void getLastResults(){
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);
        String json1 = sharedPreferences.getString("temperature", null);
        String json2 = sharedPreferences.getString("bloodPressure", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();
        Type type1 = new TypeToken<ArrayList<TemperatureResult>>() {}.getType();
        Type type2 = new TypeToken<ArrayList<BloodPressureResult>>() {}.getType();

        sugarResultArrayList = gson.fromJson(json, type);
        temperatureResultArrayList = gson.fromJson(json1, type1);
        bloodPressureResultArrayList = gson.fromJson(json2, type2);

        if (sugarResultArrayList == null) {
            sugarResultArrayList = new ArrayList<>();
        }
        if (temperatureResultArrayList == null) {
            temperatureResultArrayList = new ArrayList<>();
        }
        if (bloodPressureResultArrayList == null) {
            bloodPressureResultArrayList = new ArrayList<>();
        }

        ArrayList<SugarResult> sorted = SortHelperClass.sortSugarResults(sugarResultArrayList);
        ArrayList<TemperatureResult> sortedTemperature = SortHelperClass.sortTemperatureResults(temperatureResultArrayList);
        ArrayList<BloodPressureResult> sortedBloodPressure = SortHelperClass.sortBloodPressureResults(bloodPressureResultArrayList);
        lastSugarResult = sorted.get(sorted.size() - 1);
        lastTemperatureResult = sortedTemperature.get(sortedTemperature.size() - 1);
        lastBloodPressureResult = sortedBloodPressure.get(sortedBloodPressure.size() - 1);

    }

    private Boolean checkLastAddedResult(){
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int day = Integer.parseInt(currentDate.split("-")[0]);
        int month = Integer.parseInt(currentDate.split("-")[1]);
        int year = Integer.parseInt(currentDate.split("-")[2]);

        getLastResults();

        return (lastSugarResult.getYear() == year && lastSugarResult.getMonth() == month && lastSugarResult.getDay() == day) &&
                (lastTemperatureResult.getYear() == year && lastTemperatureResult.getMonth() == month && lastTemperatureResult.getDay() == day) &&
                (lastBloodPressureResult.getYear() == year && lastBloodPressureResult.getMonth() == month && lastBloodPressureResult.getDay() == day);
    }


}
