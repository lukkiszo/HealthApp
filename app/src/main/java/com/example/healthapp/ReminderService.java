package com.example.healthapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderService extends Service {

    ScheduledExecutorService myschedule_executor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myschedule_executor = Executors.newScheduledThreadPool(1);
        myschedule_executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                if (!checkLastAddedResult()) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "My Notification")
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setContentTitle("Wprowadź dzisiejszy poziom cukru we krwi")
                            .setContentText("Dzisiaj nie uzupełniłeś swoich wyników!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                    managerCompat.notify(100, builder.build());
                }
            }
        }, 1, 1, TimeUnit.DAYS);

        return super.onStartCommand(intent, flags, startId);
    }

    SugarResult lastSugarResult;

    @SuppressLint("SetTextI18n")
    private void getLastResults(){
        SharedPreferences sharedPreferences = getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();

        ArrayList<SugarResult> sugarResultsArrayList = gson.fromJson(json, type);

        if (sugarResultsArrayList == null) {
            sugarResultsArrayList = new ArrayList<>();
        }

        ArrayList<SugarResult> sorted = MainActivity.sortSugarResults(sugarResultsArrayList);
        lastSugarResult = sorted.get(sorted.size() - 1);

    }


    private Boolean checkLastAddedResult(){

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int day = Integer.parseInt(currentDate.split("-")[0]);
        int month = Integer.parseInt(currentDate.split("-")[1]);
        int year = Integer.parseInt(currentDate.split("-")[2]);

        getLastResults();
        return lastSugarResult.getYear() == year && lastSugarResult.getMonth() == month && lastSugarResult.getDay() == day;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        myschedule_executor.shutdown();
    }

}
