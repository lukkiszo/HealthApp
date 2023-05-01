package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StepsResultsFragment extends Fragment {
    private static ArrayList<StepsResult> stepsResultArrayList;
    private static ArrayList<StepsResult> last7DaysStepsResultsArrayList;
    private View currentView;
    private ListView listView;
    private TextView lastResult;
    private TextView meanResults;
    private List<String> results = new ArrayList<>();
    private ArrayList chartArrayList;
    private BarChart chart;
    private Button resetChartButton;
    private Button previousListViewResultsButton;
    private Button nextListViewResultsButton;
    private Integer lastResultIndex;
    private TextView listViewResultsInfo;
    private ArrayList<StepsResult> listViewArrayList;
    private Integer currentListViewPage;
    private Date todayDate;
    private Date chartLastDay;
    private Button chartLeftButton;
    private Button chartRightButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_steps_results, container, false);
        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        todayDate = new Date();
        chartLastDay = todayDate;
        listView = view.findViewById(R.id.stepsListview);
        lastResult = view.findViewById(R.id.lastStepsResult);
        meanResults = view.findViewById(R.id.meanStepsResults);
        resetChartButton = view.findViewById(R.id.stepsResetChartButton);
        previousListViewResultsButton = view.findViewById(R.id.previousStepsResults);
        nextListViewResultsButton = view.findViewById(R.id.nextStepsResults);
        listViewResultsInfo = view.findViewById(R.id.stepsListviewText);
        chartLeftButton = view.findViewById(R.id.stepsChartLeft);
        chartRightButton = view.findViewById(R.id.stepsChartRight);
        chart = (BarChart) view.findViewById(R.id.stepsChart);

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
        lastResultIndex = stepsResultArrayList.size() - 1;
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysStepsResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    private void loadLastResults(){
        listViewArrayList = new ArrayList<>();
        int i = 0;
        while   (listViewArrayList.size() < 7 &&
                (i < stepsResultArrayList.size() || stepsResultArrayList.size() < 2) &&
                (lastResultIndex - i) >= 0 &&
                (lastResultIndex - i) < stepsResultArrayList.size()){

            StepsResult result = new StepsResult(stepsResultArrayList.get(lastResultIndex - i).getDate(),
                    stepsResultArrayList.get(lastResultIndex - i).getResult(),
                    stepsResultArrayList.get(lastResultIndex - i).getAbsoluteResult()
            );
            listViewArrayList.add(result);

            i += 1;
            if (stepsResultArrayList.size() < 2) {
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
        if (lastResultIndex >= stepsResultArrayList.size()){
            lastResultIndex = stepsResultArrayList.size() - 1;
        }
        if (lastResultIndex >= stepsResultArrayList.size() - 7){
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
        if (lastResultIndex < stepsResultArrayList.size() - 7){
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
        BarDataSet barDataSet = new BarDataSet(chartArrayList, "Wykonane kroki");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColors(Color.GREEN);
        barData.setBarWidth(0.5f);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(chartArrayList.size() - 1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));
        chart.getDescription().setEnabled(false);
        chart.setData(barData);
        chart.animateXY(2000, 2000);
        chart.notifyDataSetChanged();
        chart.getAxisRight().setEnabled(true);
        chart.invalidate();
    }

    @Override
    public void onStart() {
        loadResults();
        lastResultIndex = stepsResultArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysStepsResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onStart();
    }

    @Override
    public void onResume() {
        loadResults();
        lastResultIndex = stepsResultArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysStepsResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onResume();
    }

    private void getData(){
        chartArrayList = new ArrayList();
        int i = 0;
        stepsResultArrayList = sortStepsResults(stepsResultArrayList);
        last7DaysStepsResultsArrayList = sortStepsResults(last7DaysStepsResultsArrayList);
        for(StepsResult stepsResult : last7DaysStepsResultsArrayList){
            chartArrayList.add(new BarEntry(i, (float) stepsResult.getResult()));
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

    private ArrayList<StepsResult> getLast7DaysResults(){
        ArrayList<StepsResult> chartArrayList = new ArrayList<>();

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

            StepsResult blankResult = new StepsResult(day + " " + Utils.getMonthFormat(month) + " " + year, 0, 0);
            chartArrayList.add(blankResult);

            for (StepsResult stepsResult : stepsResultArrayList){
                if (stepsResult.getDay() == day && stepsResult.getMonth() == month && stepsResult.getYear() == year){
                    chartArrayList.set(i, stepsResult);
                }
            }
        }

        return chartArrayList;
    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        stepsResultArrayList = sortStepsResults(stepsResultArrayList);
        for(StepsResult stepsResult : last7DaysStepsResultsArrayList){
            xAxis.add(String.format(Locale.getDefault(), "%02d.%02d", stepsResult.getDay(), stepsResult.getMonth()));
        }
        return xAxis;
    }

    private void getLastResult(){
        lastResult.setText(MessageFormat.format("Ostatni wynik: {0} kroków", 0));
        if (!stepsResultArrayList.isEmpty()){
            lastResult.setText(MessageFormat.format("Ostatni wynik = {0} kroków", stepsResultArrayList.get(stepsResultArrayList.size() - 1).getResult()));
        }
    }

    private void meanStepsResults(){
        double sum = 0;
        for (StepsResult stepsResult : stepsResultArrayList) {
            sum += stepsResult.getResult();
        }
        meanResults.setText(MessageFormat.format("Średnia wyników = {0} kroków", sum/stepsResultArrayList.size()));
    }

    private void reloadChart(){
        chart.fitScreen();
        last7DaysStepsResultsArrayList = getLast7DaysResults();
        loadChart();
    }

    private void resetChart(){
        chartLastDay = todayDate;
        reloadChart();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("steps", null);

        Type type = new TypeToken<ArrayList<StepsResult>>() {}.getType();

        stepsResultArrayList = gson.fromJson(json, type);

        if (stepsResultArrayList == null) {
            stepsResultArrayList = new ArrayList<>();
        }
    }

    private ArrayList<StepsResult> sortStepsResults(ArrayList<StepsResult> results){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<StepsResult> sorted = SortHelperClass.sortStepsResults(results);
        String json1 = gson.toJson(sorted);
        editor.putString("steps", json1);
        editor.apply();
        return sorted;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        results.clear();

        int numberOfElementsOnPage = 0;
        stepsResultArrayList = sortStepsResults(stepsResultArrayList);
        listViewArrayList = SortHelperClass.sortStepsResults(listViewArrayList);

        for (StepsResult stepsResult : listViewArrayList) {
            results.add(stepsResult.getDate());
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
                intent.putExtra("type", "Kroki");
                if (finalNumberOfElementsOnPage == 7){
                    intent.putExtra("position", stepsResultArrayList.size() - 7 * currentListViewPage + position);
                }
                else {
                    intent.putExtra("position", stepsResultArrayList.size() - 7 * (currentListViewPage - 1) + position - finalNumberOfElementsOnPage);
                }
                Toast.makeText(getActivity(), String.valueOf(stepsResultArrayList.get(stepsResultArrayList.size() - 7 * currentListViewPage + position).getAbsoluteResult()), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        getLastResult();
        meanStepsResults();

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

}