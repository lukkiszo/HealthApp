package com.example.healthapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class VisitReminderReceiver extends BroadcastReceiver {
    private Context currentContext;
    private int id;
    private String notificationType;
    private String medicineName;

    @Override
    public void onReceive(Context context, Intent intent) {
        currentContext = context;
        id = intent.getIntExtra("id", 100000);
        notificationType = intent.getStringExtra("time");
        if (notificationType.equals("exact")){
            medicineName = intent.getStringExtra("medicineName");
            makeMedicineNotification();
        } else {
            makeVisitNotification();
        }

    }

    private void makeVisitNotification(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MedicalVisits", "MedicalVisits", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = currentContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = null;
        if (notificationType.equals("day")){
            builder = new NotificationCompat.Builder(currentContext, "MedicalVisits")
                    .setSmallIcon(R.drawable.ic_timesheet2)
                    .setContentTitle(currentContext.getString(R.string.VisitNotifTitle))
                    .setContentText(currentContext.getString(R.string.VisitNotifTextTomorrow))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else if (notificationType.equals("hour")){
            builder = new NotificationCompat.Builder(currentContext, "MedicalVisits")
                    .setSmallIcon(R.drawable.ic_timesheet2)
                    .setContentTitle(currentContext.getString(R.string.VisitNotifTitle))
                    .setContentText(currentContext.getString(R.string.VisitNotifTextHour))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
        }


        if (builder != null) {
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(currentContext);
            managerCompat.notify(id, builder.build());
        }
    }

    private void makeMedicineNotification(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Medicines", "Medicines", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = currentContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(currentContext, "Medicines")
                .setSmallIcon(R.drawable.ic_timesheet)
                .setContentTitle(currentContext.getString(R.string.MedicineNotifTitle))
                .setContentText(currentContext.getString(R.string.MedicineNotifText) + medicineName + "!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(currentContext);
        managerCompat.notify(id, builder.build());
    }
}