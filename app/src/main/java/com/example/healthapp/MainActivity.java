package com.example.healthapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView sugarInfo;
    private TextView bloodPressureInfo;
    private TextView stepsInfo;
    private TextView medicinesInfo;
    private int lastSugarResult;
    private double lastTemperatureResult;
    private int todayStepsResult = 0;
    private int lastSystolicBloodPressureResult;
    private int lastDiastolicBloodPressureResult;
    private int lastPulseResult;
    private int lastSaturationResult;
    private static final int REQUEST_CALL = 1;
    private String number;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private Boolean isCounterSensorPresent;
    int steps = 0;
    private Boolean isCounterRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        sugarInfo = findViewById(R.id.sugar);
        bloodPressureInfo = findViewById(R.id.blood_pressure);
        stepsInfo = findViewById(R.id.steps);
        medicinesInfo = findViewById(R.id.medicines);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            isCounterSensorPresent = false;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        getLastResults();
        setProfilePhoto();
        customizeDimension();
        checkResults();
        checkIfResultsAddedToday();
    }

    @SuppressLint("SetTextI18n")
    private void getLastResults(){
        SharedPreferences sharedPreferences = getSharedPreferences("results", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("schedule", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);
        String json1 = sharedPreferences.getString("temperature", null);
        String json2 = sharedPreferences.getString("bloodPressure", null);
        String json3 = sharedPreferences.getString("steps", null);
        String json4 = sharedPreferences1.getString("medicines", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();
        Type type1 = new TypeToken<ArrayList<TemperatureResult>>() {}.getType();
        Type type2 = new TypeToken<ArrayList<BloodPressureResult>>() {}.getType();
        Type type3 = new TypeToken<ArrayList<StepsResult>>() {}.getType();
        Type type4 = new TypeToken<ArrayList<MedicineScheduleItem>>() {}.getType();

        ArrayList<SugarResult> sugarResultsArrayList = gson.fromJson(json, type);
        ArrayList<TemperatureResult> temperatureResultArrayList = gson.fromJson(json1, type1);
        ArrayList<BloodPressureResult> bloodPressureResultArrayList = gson.fromJson(json2, type2);
        ArrayList<StepsResult> stepsResultArrayList = gson.fromJson(json3, type3);
        ArrayList<MedicineScheduleItem> medicineScheduleItemArrayList = gson.fromJson(json4, type4);

        if (sugarResultsArrayList == null) {
            sugarResultsArrayList = new ArrayList<>();
        }

        if (temperatureResultArrayList == null) {
            temperatureResultArrayList = new ArrayList<>();
        }

        if (bloodPressureResultArrayList == null) {
            bloodPressureResultArrayList = new ArrayList<>();
        }

        if (stepsResultArrayList == null) {
            stepsResultArrayList = new ArrayList<>();
        }

        if (medicineScheduleItemArrayList == null) {
            medicineScheduleItemArrayList = new ArrayList<>();
        }

        ArrayList<SugarResult> sorted = SortHelperClass.sortSugarResults(sugarResultsArrayList);
        if (sorted.size() > 0){
            lastSugarResult = (int) sorted.get(sorted.size() - 1).getResult();
        }


        ArrayList<TemperatureResult> sortedTemperature = SortHelperClass.sortTemperatureResults(temperatureResultArrayList);
        if (sortedTemperature.size() > 0) {
            lastTemperatureResult = sortedTemperature.get(sortedTemperature.size() - 1).getResult();
        }

        ArrayList<BloodPressureResult> sortedBloodPressure = SortHelperClass.sortBloodPressureResults(bloodPressureResultArrayList);
        if (sortedBloodPressure.size() > 0) {
            lastSystolicBloodPressureResult = sortedBloodPressure.get(sortedBloodPressure.size() - 1).getSystolicResult();
            lastDiastolicBloodPressureResult = sortedBloodPressure.get(sortedBloodPressure.size() - 1).getDiastolicResult();
            lastPulseResult = sortedBloodPressure.get(sortedBloodPressure.size() - 1).getPulse();
            lastSaturationResult = sortedBloodPressure.get(sortedBloodPressure.size() - 1).getSaturation();
        }

        ArrayList<StepsResult> sortedSteps = SortHelperClass.sortStepsResults(stepsResultArrayList);
        if (sortedSteps.size() > 0) {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            todayStepsResult = sortedSteps.get(sortedSteps.size() - 1).getAbsoluteResult();

            if (sortedSteps.get(sortedSteps.size() - 1).getDay() == Integer.parseInt(currentDate.split("-")[0])
                    && sortedSteps.get(sortedSteps.size() - 1).getMonth() == Integer.parseInt(currentDate.split("-")[1])){
                isCounterRead = true;
            }
        }

        ArrayList<MedicineScheduleItem> todayMedicines = new ArrayList<>();
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);

        for (MedicineScheduleItem medicineItem : medicineScheduleItemArrayList) {
            if (medicineItem.getPeriodicity().equals("Codziennie")){
                todayMedicines.add(medicineItem);
            } else if (medicineItem.getPeriodicity().equals("Cyklicznie") && medicineItem.getDayOfWeek().equals(MedicinesActivity.getDayString(dayOfWeek - 1))){
                todayMedicines.add(medicineItem);
            } else if (medicineItem.getPeriodicity().equals("W zakresie dat")){
                Calendar medicineDateFrom = Calendar.getInstance();
                medicineDateFrom.set(medicineItem.getYearFrom(), medicineItem.getMonthFrom(), medicineItem.getDayFrom());
                Calendar medicineDateTo = Calendar.getInstance();
                medicineDateTo.set(medicineItem.getYearTo(), medicineItem.getMonthTo(), medicineItem.getDayTo());

                if (currentDate.compareTo(medicineDateFrom) >= 0 && currentDate.compareTo(medicineDateTo) <= 0){
                    todayMedicines.add(medicineItem);
                }
            } else if (medicineItem.getDay() == day && medicineItem.getMonth() == month && medicineItem.getYear() == year){
                todayMedicines.add(medicineItem);
            }
        }

        if (todayMedicines.size() == 0){
            medicinesInfo.setText("Brak leków do zażycia dzisiaj");
        } else {
            ArrayList<MedicineScheduleItem> sortedMedicines = SortHelperClass.sortMedicineItems(todayMedicines);
            MedicineScheduleItem closestMedicine = sortedMedicines.get(0);
            int hour = currentDate.get(Calendar.HOUR_OF_DAY);
            int minute = currentDate.get(Calendar.MINUTE);
            int closest = 24 * 60;
            for (MedicineScheduleItem item : sortedMedicines){
                if ((item.getNumber() - hour * 60 - minute) > 0 && (item.getNumber() - hour * 60 - minute) < closest){
                    closest = item.getNumber() - hour * 60 - minute;
                    closestMedicine = item;
                }
            }
            if (closest > 60){
                int hourToGet = (int) Math.floor(closest / 60);
                int minutesToGet = closest - hourToGet * 60;
                medicinesInfo.setText("Przyjmij " + closestMedicine.getMedicineName() + "\nza " + hourToGet + " h " + minutesToGet + " min" );
            } else {
                medicinesInfo.setText("Przyjmij " + closestMedicine.getMedicineName() + "\nza " + closest + " min" );
            }
        }

        bloodPressureInfo.setText(lastSystolicBloodPressureResult + " / " + lastDiastolicBloodPressureResult + "\t " + lastPulseResult + " BPM");
        sugarInfo.setText("Poziom cukru \nwe krwi: " + lastSugarResult + " mg/dl");

        stepsInfo.setText("Liczba kroków:\n" + todayStepsResult + " / 6000");
    }

    private void checkResults(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channelSugar = new NotificationChannel("Sugar", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channelTemperature = new NotificationChannel("Temperature", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channelBloodPressure = new NotificationChannel("BloodPressure", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channelPulse = new NotificationChannel("Pulse", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channelSaturation = new NotificationChannel("Saturation", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelSugar);
            manager.createNotificationChannel(channelTemperature);
            manager.createNotificationChannel(channelBloodPressure);
            manager.createNotificationChannel(channelPulse);
            manager.createNotificationChannel(channelSaturation);
        }

        if (lastSugarResult > 100 || lastSugarResult < 70){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Sugar")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle("Nieprawidłowy poziom cukru we krwi!")
                    .setContentText("Twój ostatni wprowadzony wynik badania poziomu cukru we krwi nie mieścił się w granicach normy")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(1001, builder.build());
        }

        if (lastTemperatureResult > 37.5 || lastTemperatureResult < 36){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Temperature")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle("Nieprawidłowa temperatura ciała!")
                    .setContentText("Twój ostatni wprowadzony wynik badania temperatury ciała nie mieścił się w granicach normy")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(1002, builder.build());
        }

        if (lastSystolicBloodPressureResult > 135 || lastDiastolicBloodPressureResult > 85 || lastSystolicBloodPressureResult < 100 || lastDiastolicBloodPressureResult < 60){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "BloodPressure")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle("Nieprawidłowe ciśnienie krwi!")
                    .setContentText("Twój ostatni wprowadzony wynik badania ciśnienia tętniczego krwi nie mieścił się w granicach normy")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(1003, builder.build());
        }

        if (lastPulseResult > 100 || lastPulseResult < 50){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Pulse")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle("Nieprawidłowe tętno!")
                    .setContentText("Twój ostatni wprowadzony wynik badania tętna nie mieścił się w granicach normy")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(1004, builder.build());
        }

        if (lastSaturationResult < 95){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Saturation")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setContentTitle("Nieprawidłowa saturacja!")
                    .setContentText("Twój ostatni wprowadzony wynik saturacji krwi był poniżej normy")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.notify(1005, builder.build());
        }

    }

    private void checkIfResultsAddedToday(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
            }
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 16);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        showNotification(c);
    }

    private void showNotification(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_MUTABLE);
        Log.d("reminderBroadcast", pendingIntent1.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent1);
        }
    }

    private void getDailySteps(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 0);

        startAlarm(c);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == stepSensor){
            steps = (int) event.values[0];
            stepsInfo.setText("Liczba kroków:\n" + (steps - todayStepsResult) + " / 6000");
            if (!isCounterRead){
                getDailySteps();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StepsReceiver.class);
        intent.putExtra("steps", steps);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

    private void startFallDetectionService(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            return;
        }

        Intent intent = new Intent(getApplicationContext(), FallDetectionService.class);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        startFallDetectionService();
        super.onDestroy();
    }

    private void customizeDimension(){
        View bloodpressure = findViewById(R.id.blood_pressure);
        View sugar = findViewById(R.id.sugar);
        View steps = findViewById(R.id.steps);
        View medicines = findViewById(R.id.medicines);
        Integer height = Resources.getSystem().getDisplayMetrics().heightPixels;

        bloodpressure.getLayoutParams().height = height / 8;
        sugar.getLayoutParams().height = height / 8;
        steps.getLayoutParams().height = height / 8;
        medicines.getLayoutParams().height = height / 8;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Czy na pewno chcesz wyjść z aplikacji?");
        alertDialogBuilder
                .setMessage("Kliknij tak aby wyjść!")
                .setCancelable(false)
                .setPositiveButton("Tak",
                        (dialog, id) -> {
                            moveTaskToBack(true);
                            System.exit(0);
                        })

                .setNegativeButton("Nie", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void exit(View view){
        onBackPressed();
    }

    public void emergencyCall(View view){
        number = "112";
        call(number);
    }

    public void call_ICE(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(ProfileActivity.mypreference, Context.MODE_PRIVATE);
        number = sharedPreferences.getString("number", "112");
        call(number);
    }

    private void call(String number){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @SuppressLint({"MissingSuperCall", "ShowToast"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                call(number);
            }
            else {
                Toast.makeText(this, "Odmowa dostępu", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.call_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void goToEditor(View view){
        Intent intent = new Intent(this, ResultEditor.class);
        intent.putExtra("type", "Cukier");
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.temperatureNav:
                intent = new Intent(this, TemperatureActivity.class);
                startActivity(intent);
                break;

            case R.id.bloodPressureNav:
                intent = new Intent(this, BloodPressureActivity.class);
                intent.putExtra("type", "Ciśnienie krwi");
                startActivity(intent);
                break;

            case R.id.pulseNav:
                intent = new Intent(this, BloodPressureActivity.class);
                intent.putExtra("type", "Tętno");
                startActivity(intent);
                break;

            case R.id.saturationNav:
                intent = new Intent(this, BloodPressureActivity.class);
                intent.putExtra("type", "Saturacja krwi");
                startActivity(intent);
                break;

            case R.id.stepsNav:
                intent = new Intent(this, StepsActivity.class);
                startActivity(intent);
                break;

            case R.id.sugarNav:
                intent = new Intent(this, SugarActivity.class);
                startActivity(intent);
                break;

            case R.id.medicineScheduleNav:
                intent = new Intent(this, MedicinesActivity.class);
                startActivity(intent);
                break;

            case R.id.summaryNav:
                intent = new Intent(this, MonthSummaryActivity.class);
                startActivity(intent);
                break;

            case R.id.myProfile:
                goToProfile(getCurrentFocus());
                break;

            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.exit:
                exit(findViewById(android.R.id.content).getRootView());
                break;

            case R.id.visitScheduleNav:
                intent = new Intent(this, VisitScheduleActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return false;
    }

    @Override
    protected void onResume() {
        startFallDetectionService();
        setProfilePhoto();
        getLastResults();
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManager.unregisterListener(this, stepSensor);
        }
    }

    private void setProfilePhoto(){
        SharedPreferences sharedPref = getSharedPreferences(ProfileActivity.mypreference, Context.MODE_PRIVATE);

        ImageButton headerProfile = (ImageButton) findViewById(R.id.header_profile);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.username);
        ImageView navigationPhoto = (ImageView) headerView.findViewById(R.id.userPhoto);

        String photo = sharedPref.getString("photo", "");
        String name = sharedPref.getString("username", "");

        Uri imageUri = Uri.parse(photo);
        if (imageUri != null) {

            // Skaluj i skompresuj zdjęcie z wykorzystaniem biblioteki Glide
            Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .apply(new RequestOptions()
                            .override(256) // Ustaw maksymalną szerokość lub wysokość na 1024 pikseli
                            .format(DecodeFormat.PREFER_RGB_565) // Ustaw format zdjęcia na RGB_565
                            .encodeQuality(80)) // Ustaw jakość kompresji na 80%
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Wyświetl skompresowane i przeskalowane zdjęcie w widoku ImageView
                            headerProfile.setImageBitmap(resource);
                            nav_user.setText(name);
                            navigationPhoto.setImageBitmap(resource);
                        }
                    });
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}