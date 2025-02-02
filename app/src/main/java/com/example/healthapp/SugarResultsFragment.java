package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;


public class SugarResultsFragment extends Fragment implements OnChartValueSelectedListener {

    private static ArrayList<SugarResult> sugarResultsArrayList;
    private static ArrayList<SugarResult> last7DaysSugarResultsArrayList;
    private View currentView;
    private ListView listView;
    private TextView BMI_text;
    private TextView BMI_info;
    private TextView lastResult;
    private TextView meanResults;
    private List<String> results = new ArrayList<>();
    private ArrayList chartArrayList;
    private LineChart chart;
    private Button resetChartButton;
    private Button previousListViewResultsButton;
    private Button nextListViewResultsButton;
    private Integer lastResultIndex;
    private TextView listViewResultsInfo;
    private ArrayList<SugarResult> listViewArrayList;
    private Integer currentListViewPage;
    private Date todayDate;
    private Date chartLastDay;
    private Button chartLeftButton;
    private Button chartRightButton;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        currentView = inflater.inflate(R.layout.fragment_sugar_results, parent, false);
        return currentView;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        todayDate = new Date();
        chartLastDay = todayDate;
        BMI_text = view.findViewById(R.id.BMI);
        BMI_info = view.findViewById(R.id.BMI_info);
        listView = view.findViewById(R.id.listview);
        lastResult = view.findViewById(R.id.lastSugarResult);
        meanResults = view.findViewById(R.id.meanSugarResults);
        resetChartButton = view.findViewById(R.id.resetChartButton);
        previousListViewResultsButton = view.findViewById(R.id.previousSugarResults);
        nextListViewResultsButton = view.findViewById(R.id.nextSugarResults);
        listViewResultsInfo = view.findViewById(R.id.listviewText);
        chartLeftButton = view.findViewById(R.id.chartLeft);
        chartRightButton = view.findViewById(R.id.chartRight);
        chart = (LineChart) view.findViewById(R.id.sugarChart);
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        BMI_text.setText(MessageFormat.format("BMI = {0}", BMI_count()));
        checkBMI(BMI_count());

        resetChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { resetChart(); }
        });

        nextListViewResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { nextListViewResults(); }
        });

        previousListViewResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { previousListViewResults(); }
        });

        chartRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { moveChartRight(); }
        });

        chartLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { moveChartLeft(); }
        });

        loadResults();
        lastResultIndex = sugarResultsArrayList.size() - 1;
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysSugarResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
    }

    @SuppressLint("DefaultLocale")
    private void loadLastResults(){
        listViewArrayList = new ArrayList<>();
        Integer i = 0;
        while   (listViewArrayList.size() < 7 &&
                (i < sugarResultsArrayList.size() || sugarResultsArrayList.size() < 2) &&
                (lastResultIndex - i) >= 0 &&
                (lastResultIndex - i) < sugarResultsArrayList.size()){

            SugarResult result = new SugarResult(sugarResultsArrayList.get(lastResultIndex - i).getDate(),
                    sugarResultsArrayList.get(lastResultIndex - i).getHour(),
                    sugarResultsArrayList.get(lastResultIndex - i).getResult(),
                    sugarResultsArrayList.get(lastResultIndex - i).getAnnotation()
            );
            listViewArrayList.add(result);

            i += 1;
            if (sugarResultsArrayList.size() < 2) {
                break;
            }
        }

        if (listViewArrayList.size() != 0){
            listViewResultsInfo.setText(String.format("%d-%d - %d-%d", listViewArrayList.get(listViewArrayList.size() - 1).getDay(),
                    listViewArrayList.get(listViewArrayList.size() - 1).getMonth(),
                    listViewArrayList.get(0).getDay(),
                    listViewArrayList.get(0).getMonth()));
            reloadListView();
        }

    }

    @SuppressLint("DefaultLocale")
    private void nextListViewResults(){
        lastResultIndex += 7;
        if (lastResultIndex >= sugarResultsArrayList.size()){
            lastResultIndex = sugarResultsArrayList.size() - 1;
        }
        if (lastResultIndex >= sugarResultsArrayList.size() - 7){
            nextListViewResultsButton.setEnabled(false);
        }
        if (lastResultIndex >= 7){
            previousListViewResultsButton.setEnabled(true);
        }
        currentListViewPage -= 1;
        loadLastResults();
    }

    @SuppressLint("DefaultLocale")
    private void previousListViewResults(){

        lastResultIndex -= 7;
        if (lastResultIndex < 0){
            lastResultIndex = 6;
        }
        if (lastResultIndex < sugarResultsArrayList.size() - 7){
            nextListViewResultsButton.setEnabled(true);
        }
        if (lastResultIndex < 7){
            previousListViewResultsButton.setEnabled(false);
        }
        currentListViewPage += 1;
        loadLastResults();
    }

    private void loadChart(){
        getData();
        LineDataSet lineDataSet = new LineDataSet(chartArrayList, getString(R.string.SugarChartLabel));
        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setColors(Color.GREEN);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(chartArrayList.size() - 1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));
        chart.getDescription().setEnabled(false);
        chart.setData(lineData);
        chart.animateXY(2000, 2000);
        chart.notifyDataSetChanged();
        chart.getAxisRight().setEnabled(true);
        chart.invalidate();
    }

    @Override
    public void onStart() {
        loadResults();
        lastResultIndex = sugarResultsArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysSugarResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onStart();
    }

    @Override
    public void onResume() {
        loadResults();
        lastResultIndex = sugarResultsArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysSugarResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onResume();
    }

    private void getData(){
        chartArrayList = new ArrayList();
        int i = 0;
        sugarResultsArrayList = sortSugarResults(sugarResultsArrayList);
        last7DaysSugarResultsArrayList = sortSugarResults(last7DaysSugarResultsArrayList);
        for(SugarResult sugarResult : last7DaysSugarResultsArrayList){
            chartArrayList.add(new Entry(i, (int) sugarResult.getResult()));
            i += 1;
        }
    }

    private void moveChartLeft(){
        Calendar ca = Calendar.getInstance();
        ca.setTime(chartLastDay);
        ca.add(Calendar.DATE, -1);
        chartLastDay = ca.getTime();

        reloadChart();
    }

    private void moveChartRight(){
        if (chartLastDay.compareTo(todayDate) < 0){
            Calendar ca = Calendar.getInstance();
            ca.setTime(chartLastDay);
            ca.add(Calendar.DATE, 1);
            chartLastDay = ca.getTime();

            reloadChart();
        }
    }

    private ArrayList<SugarResult> getLast7DaysResults(){
        ArrayList<SugarResult> chartArrayList = new ArrayList<>();

        Date date = chartLastDay;
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        date = ca.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());;
        String formattedDate;
        int day;
        int month;
        int year;


        for (int i = 0; i < 7; i++){
            formattedDate = df.format(date);
            day = Integer.parseInt(formattedDate.split("-")[0]);
            month = Integer.parseInt(formattedDate.split("-")[1]);
            year = Integer.parseInt(formattedDate.split("-")[2]);
            ca.setTime(date);
            ca.add(Calendar.DATE, -1);
            date = ca.getTime();

            SugarResult blankResult = new SugarResult(day + " " + Utils.getMonthFormat(month) + " " + year, "00:00", 0, "");
            chartArrayList.add(blankResult);

            for (SugarResult sugarResult : sugarResultsArrayList){
                if (sugarResult.getDay() == day && sugarResult.getMonth() == month && sugarResult.getYear() == year){
                    chartArrayList.set(i, sugarResult);
                }
            }
        }

        return chartArrayList;
    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        sugarResultsArrayList = sortSugarResults(sugarResultsArrayList);
        for(SugarResult sugarResult : last7DaysSugarResultsArrayList){
            xAxis.add(String.format(Locale.getDefault(), "%02d.%02d", sugarResult.getDay(), sugarResult.getMonth()));
        }
        return xAxis;
    }

    private void getLastResult(){
        lastResult.setText(MessageFormat.format("{0} {1} mg/dl", getString(R.string.LastResult), 0));
        if (!sugarResultsArrayList.isEmpty()){
            lastResult.setText(MessageFormat.format("{0} {1} mg/dl",
                    getString(R.string.LastResult), (int) sugarResultsArrayList.get(sugarResultsArrayList.size() - 1).getResult()));
        }
    }

    private void meanSugarResults(){
        double sum = 0;
        for (SugarResult sugarResult : sugarResultsArrayList) {
            sum += sugarResult.getResult();
        }
        meanResults.setText(MessageFormat.format("{0} {1} mg/dl", getString(R.string.MeanResult), sum/sugarResultsArrayList.size()));
    }

    private void reloadChart(){
        chart.fitScreen();
        last7DaysSugarResultsArrayList = getLast7DaysResults();
        loadChart();
    }

    private void resetChart(){
        chartLastDay = todayDate;
        reloadChart();
    }

    private double BMI_count(){
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences(ProfileActivity.mypreference, Context.MODE_PRIVATE);

        int weight = sharedPref.getInt("weight", 0);
        double height = sharedPref.getInt("height", 0);

        return (double) weight / ((height/100)*(height/100));
    }

    private void checkBMI(double BMI){
        if(BMI < 17 || BMI >= 30){
            BMI_text.setTextColor(Color.RED);
            if(BMI < 17){
                BMI_info.setText(R.string.BMI_Emaciation);
                if(BMI < 16) BMI_info.setText(R.string.BMI_Starvation);
            }

            if(BMI >= 30){
                BMI_info.setText(R.string.BMI_Obesity1);
                if(BMI >= 35) BMI_info.setText(R.string.BMI_Obesity2);
                if(BMI >= 40) BMI_info.setText(R.string.BMI_Obesity3);
            }

        }
        else if (BMI < 18.5 || BMI >= 25){
            if (BMI < 18.5) BMI_info.setText(R.string.BMI_Underweight);
            if (BMI >= 25) BMI_info.setText(R.string.BMI_Overweight);

            BMI_text.setTextColor(Color.YELLOW);
        }
        else {
            BMI_text.setTextColor(Color.GREEN);
            BMI_info.setText(R.string.BMI_Correct);
        }
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MainActivity.resultsPreferencesName, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();

        sugarResultsArrayList = gson.fromJson(json, type);

        if (sugarResultsArrayList == null) {
            sugarResultsArrayList = new ArrayList<>();
        }
    }

    private ArrayList<SugarResult> sortSugarResults(ArrayList<SugarResult> results){

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(MainActivity.resultsPreferencesName, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(SortHelperClass.sortSugarResults(results));
        editor.putString("sugar", json1);
        editor.apply();
        return SortHelperClass.sortSugarResults(results);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        results.clear();

        int numberOfElementsOnPage = 0;
        sugarResultsArrayList = sortSugarResults(sugarResultsArrayList);
        listViewArrayList = SortHelperClass.sortSugarResults(listViewArrayList);

        for (SugarResult sugarResult : listViewArrayList) {
            results.add(sugarResult.getDate() + ", " + sugarResult.getHour());
            numberOfElementsOnPage += 1;
        }

        ArrayAdapter<String> arr = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, results);
        listView.setAdapter(arr);

        int finalNumberOfElementsOnPage = numberOfElementsOnPage;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent intent = new Intent(getActivity(), ResultEditor.class);
                intent.putExtra("type", getString(R.string.Sugar));
                if (finalNumberOfElementsOnPage == 7){
                    intent.putExtra("position", sugarResultsArrayList.size() - 7 * currentListViewPage + position);
                }
                else {
                    intent.putExtra("position", sugarResultsArrayList.size() - 7 * (currentListViewPage - 1) + position - finalNumberOfElementsOnPage);
                }
                startActivity(intent);
            }
        });

        getLastResult();
        meanSugarResults();

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {}
}