package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TemperatureResultsFragment extends Fragment implements OnChartValueSelectedListener {

    private static ArrayList<TemperatureResult> temperatureResultArrayList;
    private static ArrayList<TemperatureResult> last7DaysTemperatureResultsArrayList;
    private View currentView;
    private ListView listView;
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
    private ArrayList<TemperatureResult> listViewArrayList;
    private Integer currentListViewPage;
    private Date todayDate;
    private Date chartLastDay;
    private Button chartLeftButton;
    private Button chartRightButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_temperature_results, container, false);
        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        todayDate = new Date();
        chartLastDay = todayDate;
        listView = view.findViewById(R.id.temperatureListview);
        lastResult = view.findViewById(R.id.lastTemperatureResult);
        meanResults = view.findViewById(R.id.meanTemperatureResults);
        resetChartButton = view.findViewById(R.id.temperatureResetChartButton);
        previousListViewResultsButton = view.findViewById(R.id.previousTemperatureResults);
        nextListViewResultsButton = view.findViewById(R.id.nextTemperatureResults);
        listViewResultsInfo = view.findViewById(R.id.temperatureListviewText);
        chartLeftButton = view.findViewById(R.id.temperatureChartLeft);
        chartRightButton = view.findViewById(R.id.temperatureChartRight);
        chart = (LineChart) view.findViewById(R.id.temperatureChart);

        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);

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
        lastResultIndex = temperatureResultArrayList.size() - 1;
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysTemperatureResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    private void loadLastResults(){
        listViewArrayList = new ArrayList<>();
        int i = 0;
        while   (listViewArrayList.size() < 7 &&
                (i < temperatureResultArrayList.size() || temperatureResultArrayList.size() < 2) &&
                (lastResultIndex - i) >= 0 &&
                (lastResultIndex - i) < temperatureResultArrayList.size()){

            TemperatureResult result = new TemperatureResult(temperatureResultArrayList.get(lastResultIndex - i).getDate(),
                    temperatureResultArrayList.get(lastResultIndex - i).getHour(),
                    temperatureResultArrayList.get(lastResultIndex - i).getResult()
            );
            listViewArrayList.add(result);

            i += 1;
            if (temperatureResultArrayList.size() < 2) {
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
        if (lastResultIndex >= temperatureResultArrayList.size()){
            lastResultIndex = temperatureResultArrayList.size() - 1;
        }
        if (lastResultIndex >= temperatureResultArrayList.size() - 7){
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
        if (lastResultIndex < temperatureResultArrayList.size() - 7){
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
        LineDataSet lineDataSet = new LineDataSet(chartArrayList, "Temperatura ciała [°C]");
        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setColors(Color.RED);
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
        lastResultIndex = temperatureResultArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysTemperatureResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onStart();
    }

    @Override
    public void onResume() {
        loadResults();
        lastResultIndex = temperatureResultArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysTemperatureResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onResume();
    }

    private void getData(){
        chartArrayList = new ArrayList();
        int i = 0;
        temperatureResultArrayList = sortTemperatureResults(temperatureResultArrayList);
        last7DaysTemperatureResultsArrayList = sortTemperatureResults(last7DaysTemperatureResultsArrayList);
        for(TemperatureResult temperatureResult : last7DaysTemperatureResultsArrayList){
            chartArrayList.add(new Entry(i, (float) temperatureResult.getResult()));
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

    private ArrayList<TemperatureResult> getLast7DaysResults(){
        ArrayList<TemperatureResult> chartArrayList = new ArrayList<>();

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

            TemperatureResult blankResult = new TemperatureResult(day + " " + SugarEditorFragment.getMonthFormat(month) + " " + year, "00:00", 0);
            chartArrayList.add(blankResult);

            for (TemperatureResult temperatureResult : temperatureResultArrayList){
                if (temperatureResult.getDay() == day && temperatureResult.getMonth() == month && temperatureResult.getYear() == year){
                    chartArrayList.set(i, temperatureResult);
                }
            }
        }

        return chartArrayList;
    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        temperatureResultArrayList = sortTemperatureResults(temperatureResultArrayList);
        for(TemperatureResult temperatureResult : last7DaysTemperatureResultsArrayList){
            xAxis.add(String.format(Locale.getDefault(), "%02d.%02d", temperatureResult.getDay(), temperatureResult.getMonth()));
        }
        return xAxis;
    }

    private void getLastResult(){
        lastResult.setText(MessageFormat.format("Ostatni wynik = {0} °C", 0));
        if (!temperatureResultArrayList.isEmpty()){
            lastResult.setText(MessageFormat.format("Ostatni wynik = {0} °C", temperatureResultArrayList.get(temperatureResultArrayList.size() - 1).getResult()));
        }
    }

    private void meanTemperatureResults(){
        double sum = 0;
        for (TemperatureResult temperatureResult : temperatureResultArrayList) {
            sum += temperatureResult.getResult();
        }
        meanResults.setText(MessageFormat.format("Średnia wyników = {0} °C", sum/temperatureResultArrayList.size()));
    }

    private void reloadChart(){
        chart.fitScreen();
        last7DaysTemperatureResultsArrayList = getLast7DaysResults();
        loadChart();
    }

    private void resetChart(){
        chartLastDay = todayDate;
        reloadChart();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("temperature", null);

        Type type = new TypeToken<ArrayList<TemperatureResult>>() {}.getType();

        temperatureResultArrayList = gson.fromJson(json, type);

        if (temperatureResultArrayList == null) {
            temperatureResultArrayList = new ArrayList<>();
        }
    }

    private ArrayList<TemperatureResult> sortTemperatureResults(ArrayList<TemperatureResult> results){

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<TemperatureResult> sorted = MainActivity.sortTemperatureResults(results);
        String json1 = gson.toJson(sorted);
        editor.putString("temperature", json1);
        editor.apply();
        return sorted;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        results.clear();

        int numberOfElementsOnPage = 0;
        temperatureResultArrayList = sortTemperatureResults(temperatureResultArrayList);
        listViewArrayList = MainActivity.sortTemperatureResults(listViewArrayList);

        for (TemperatureResult temperatureResult : listViewArrayList) {
            results.add(temperatureResult.getDate() + ", " + temperatureResult.getHour());
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
                intent.putExtra("type", "Temperatura ciała");
                if (finalNumberOfElementsOnPage == 7){
                    intent.putExtra("position", temperatureResultArrayList.size() - 7 * currentListViewPage + position);
                }
                else {
                    intent.putExtra("position", temperatureResultArrayList.size() - 7 * (currentListViewPage - 1) + position - finalNumberOfElementsOnPage);
                }
                startActivity(intent);
            }
        });

        getLastResult();
        meanTemperatureResults();

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