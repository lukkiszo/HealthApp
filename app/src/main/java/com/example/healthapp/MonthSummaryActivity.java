package com.example.healthapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthSummaryActivity extends AppCompatActivity {

    private Button monthBeforeButton;
    private Button monthAfterButton;
    private TextView monthTextView;
    private int summaryMonth;
    private int summaryYear;
    private int currentMonth;
    private int currentYear;
    private ArrayList<SugarResult> sugarResults;
    private ArrayList<BloodPressureResult> bloodPressureResults;
    private ArrayList<TemperatureResult> temperatureResults;
    private ArrayList<StepsResult> stepsResults;
    private String stepsDailyGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        stepsDailyGoal = sharedPreferences.getString("stepsGoal", "");
        setContentView(R.layout.activity_month_summary);

        loadResults();

        Toolbar toolbar = (Toolbar) findViewById(R.id.summary_activity_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.MonthSummary);
        }

        monthAfterButton = findViewById(R.id.summaryDate_monthAfter);
        monthBeforeButton = findViewById(R.id.summaryDate_monthBefore);
        monthTextView = findViewById(R.id.summary_dateText);

        Calendar c = Calendar.getInstance();
        summaryMonth = c.get(Calendar.MONTH);
        summaryYear = c.get(Calendar.YEAR);
        currentMonth = summaryMonth;
        currentYear = summaryYear;

        if (MainActivity.language.equals("English")){
            monthTextView.setText(Utils.getMonthFormatEnglish(summaryMonth + 1) + " " + summaryYear);
        } else {
            monthTextView.setText(Utils.getMonthFormat(summaryMonth + 1) + " " + summaryYear);
        }

        monthBeforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneMonthBefore();
            }
        });

        monthAfterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneMonthAfter();
            }
        });

        checkSugarMonthResults();
        checkBloodPressureMonthResults();
        checkTemperatureMonthResults();
        checkStepsMonthResults();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(MainActivity.resultsPreferencesName, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);
        String json1 = sharedPreferences.getString("bloodPressure", null);
        String json2 = sharedPreferences.getString("temperature", null);
        String json3 = sharedPreferences.getString("steps", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();
        Type type1 = new TypeToken<ArrayList<BloodPressureResult>>() {}.getType();
        Type type2 = new TypeToken<ArrayList<TemperatureResult>>() {}.getType();
        Type type3 = new TypeToken<ArrayList<StepsResult>>() {}.getType();

        sugarResults = gson.fromJson(json, type);
        bloodPressureResults = gson.fromJson(json1, type1);
        temperatureResults = gson.fromJson(json2, type2);
        stepsResults = gson.fromJson(json3, type3);

        if (sugarResults == null) {
            sugarResults = new ArrayList<>();
        }
        if (bloodPressureResults == null) {
            bloodPressureResults = new ArrayList<>();
        }
        if (temperatureResults == null) {
            temperatureResults = new ArrayList<>();
        }
        if (stepsResults == null) {
            stepsResults = new ArrayList<>();
        }
    }

    private void checkSugarMonthResults(){
        ArrayList<SugarResult> summaryMonthResults = new ArrayList<>();

        for (SugarResult result : sugarResults){
            if (result.getMonth() == summaryMonth + 1 && result.getYear() == summaryYear){
                summaryMonthResults.add(result);
            }
        }

        double sum = 0;
        int numberOfDaysInNorm = 0;

        for (SugarResult result : summaryMonthResults){
            sum += result.getResult();
            if (result.getResult() <= 100 && result.getResult() >= 70){
                numberOfDaysInNorm++;
            }
        }
        TextView meanResultsTextView = findViewById(R.id.sugarSummaryMean);
        TextView normTextView = findViewById(R.id.sugarSummaryDays);

        int daysInMonth = 0;

        YearMonth yearMonthObject;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(summaryYear, summaryMonth + 1);
            daysInMonth = yearMonthObject.lengthOfMonth();
        }

        if (summaryMonthResults.size() != 0){
            meanResultsTextView.setText(MessageFormat.format("{0} {1} mg/dl",
                    getString(R.string.SummaryMean),
                    String.format("%.2f", (sum / summaryMonthResults.size()))));
        } else {
            meanResultsTextView.setText(R.string.SummaryMeanNoValues);
        }
        normTextView.setText(MessageFormat.format("{0} {1} / {2}", getString(R.string.SummaryNoDaysInNorm), numberOfDaysInNorm, daysInMonth));
    }

    private void checkBloodPressureMonthResults(){
        ArrayList<BloodPressureResult> summaryMonthResults = new ArrayList<>();

        for (BloodPressureResult result : bloodPressureResults){
            if (result.getMonth() == summaryMonth + 1 && result.getYear() == summaryYear){
                summaryMonthResults.add(result);
            }
        }

        double sumSystolic = 0;
        double sumDiastolic = 0;
        double sumPulse = 0;
        double sumSaturation = 0;
        int numberOfDaysInNorm = 0;

        for (BloodPressureResult result : summaryMonthResults){
            sumSystolic += result.getSystolicResult();
            sumDiastolic += result.getDiastolicResult();
            sumPulse += result.getPulse();
            sumSaturation += result.getSaturation();

            if (result.getSystolicResult() <= 135 && result.getSystolicResult() >= 100 && result.getDiastolicResult() <= 85 && result.getDiastolicResult() >= 60 &&
                    result.getPulse() <= 100 && result.getPulse() >= 50 && result.getSaturation() >= 95){
                numberOfDaysInNorm++;
            }
        }
        TextView meanBloodPressureResultsTextView = findViewById(R.id.bloodPressureSummaryMean);
        TextView meanSaturationResultsTextView = findViewById(R.id.saturationSummaryMean);
        TextView meanPulseResultsTextView = findViewById(R.id.pulseSummaryMean);
        TextView normTextView = findViewById(R.id.bloodPressureSummaryDays);

        int daysInMonth = 0;

        YearMonth yearMonthObject;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(summaryYear, summaryMonth + 1);
            daysInMonth = yearMonthObject.lengthOfMonth();
        }

        if (summaryMonthResults.size() != 0){
            meanBloodPressureResultsTextView.setText(MessageFormat.format("{0} {1} / {2}", getString(R.string.SummaryMeanBloodPressure), (int) (sumSystolic / summaryMonthResults.size()), (int) (sumDiastolic / summaryMonthResults.size())));
            meanSaturationResultsTextView.setText(MessageFormat.format("{0} {1} %", getString(R.string.SummaryMeanSaturation), String.format("%.1f", (sumSaturation / summaryMonthResults.size()))));
            meanPulseResultsTextView.setText(MessageFormat.format("{0} {1} BPM", getString(R.string.SummaryMeanPulse), (int) (sumPulse / summaryMonthResults.size())));
        } else {
            meanBloodPressureResultsTextView.setText(R.string.SummaryMeanBloodPressureNoResults);
            meanSaturationResultsTextView.setText(R.string.SummaryMeanSaturationNoResults);
            meanPulseResultsTextView.setText(R.string.SummaryMeanPulseNoResults);
        }
        normTextView.setText(MessageFormat.format("{0} {1} / {2}", getString(R.string.SummaryNoDaysInNorm), numberOfDaysInNorm, daysInMonth));
    }

    private void checkTemperatureMonthResults(){
        ArrayList<TemperatureResult> summaryMonthResults = new ArrayList<>();

        for (TemperatureResult result : temperatureResults){
            if (result.getMonth() == summaryMonth + 1 && result.getYear() == summaryYear){
                summaryMonthResults.add(result);
            }
        }

        double sum = 0;
        int numberOfDaysInNorm = 0;

        for (TemperatureResult result : summaryMonthResults){
            sum += result.getResult();
            if (result.getResult() <= 37.5 && result.getResult() >= 36){
                numberOfDaysInNorm++;
            }
        }
        TextView meanResultsTextView = findViewById(R.id.temperatureSummaryMean);
        TextView normTextView = findViewById(R.id.temperatureSummaryDays);

        int daysInMonth = 0;

        YearMonth yearMonthObject;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(summaryYear, summaryMonth + 1);
            daysInMonth = yearMonthObject.lengthOfMonth();
        }

        if (summaryMonthResults.size() != 0){
            meanResultsTextView.setText(MessageFormat.format("{0} {1} °C", getString(R.string.SummaryMean), String.format("%.1f", (sum / summaryMonthResults.size()))));
        } else {
            meanResultsTextView.setText(R.string.SummaryMeanNoValues);
        }
        normTextView.setText(MessageFormat.format("{0} {1} / {2}", getString(R.string.SummaryNoDaysInNorm), numberOfDaysInNorm, daysInMonth));
    }

    private void checkStepsMonthResults(){
        ArrayList<StepsResult> summaryMonthResults = new ArrayList<>();

        for (StepsResult result : stepsResults){
            if (result.getMonth() == summaryMonth + 1 && result.getYear() == summaryYear){
                summaryMonthResults.add(result);
            }
        }

        double sum = 0;
        int numberOfDaysInNorm = 0;

        for (StepsResult result : summaryMonthResults){
            sum += result.getResult();
            if (result.getResult() >= Integer.parseInt(stepsDailyGoal)){
                numberOfDaysInNorm++;
            }
        }
        TextView meanResultsTextView = findViewById(R.id.stepsSummaryMean);
        TextView normTextView = findViewById(R.id.stepsSummaryDays);

        int daysInMonth = 0;

        YearMonth yearMonthObject;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(summaryYear, summaryMonth + 1);
            daysInMonth = yearMonthObject.lengthOfMonth();
        }

        if (summaryMonthResults.size() != 0){
            meanResultsTextView.setText(MessageFormat.format("{0} {1}", getString(R.string.SummaryMean), (int) (sum / summaryMonthResults.size())));
        } else {
            meanResultsTextView.setText(R.string.SummaryMeanNoValues);
        }
        normTextView.setText(MessageFormat.format("{0} {1} / {2}", getString(R.string.SummaryStepsNoDays), numberOfDaysInNorm, daysInMonth));
    }


    private void oneMonthBefore(){
        summaryMonth -= 1;
        if (summaryMonth < 0){
            summaryMonth = 11;
            summaryYear -= 1;
        }
        if (MainActivity.language.equals("English")){
            monthTextView.setText(Utils.getMonthFormatEnglish(summaryMonth + 1) + " " + summaryYear);
        } else {
            monthTextView.setText(Utils.getMonthFormat(summaryMonth + 1) + " " + summaryYear);
        }
        checkSugarMonthResults();
        checkBloodPressureMonthResults();
        checkTemperatureMonthResults();
        checkStepsMonthResults();
    }

    private void oneMonthAfter(){
        if (!(summaryMonth == currentMonth && summaryYear == currentYear)){
            summaryMonth += 1;
            if (summaryMonth > 11){
                summaryMonth = 0;
                summaryYear += 1;
            }
            if (MainActivity.language.equals("English")){
                monthTextView.setText(Utils.getMonthFormatEnglish(summaryMonth + 1) + " " + summaryYear);
            } else {
                monthTextView.setText(Utils.getMonthFormat(summaryMonth + 1) + " " + summaryYear);
            }

            checkSugarMonthResults();
            checkBloodPressureMonthResults();
            checkTemperatureMonthResults();
            checkStepsMonthResults();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}