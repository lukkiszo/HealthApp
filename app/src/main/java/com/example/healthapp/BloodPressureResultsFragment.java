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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class BloodPressureResultsFragment extends Fragment {
    private View currentView;
    private static ArrayList<BloodPressureResult> bloodPressureResultArrayList;
    private static ArrayList<BloodPressureResult> last7DaysBloodPressureResultsArrayList;
    private ListView listView;
    private TextView lastResult;
    private TextView meanResults;
    private List<String> results = new ArrayList<>();
    private ArrayList chartSystolicArrayList;
    private ArrayList chartDiastolicArrayList;
    private LineChart chart;
    private Button resetChartButton;
    private Button previousListViewResultsButton;
    private Button nextListViewResultsButton;
    private Integer lastResultIndex;
    private TextView listViewResultsInfo;
    private ArrayList<BloodPressureResult> listViewArrayList;
    private Integer currentListViewPage;
    private Date todayDate;
    private Date chartLastDay;
    private Button chartLeftButton;
    private Button chartRightButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentView = inflater.inflate(R.layout.fragment_blood_pressure_results, container, false);
        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        todayDate = new Date();
        chartLastDay = todayDate;

        listView = view.findViewById(R.id.bloodPressureListview);
        lastResult = view.findViewById(R.id.lastBloodPressureResult);
        meanResults = view.findViewById(R.id.meanBloodPressureResults);
        resetChartButton = view.findViewById(R.id.bloodPressureResetChartButton);
        previousListViewResultsButton = view.findViewById(R.id.previousBloodPressureResults);
        nextListViewResultsButton = view.findViewById(R.id.nextBloodPressureResults);
        listViewResultsInfo = view.findViewById(R.id.bloodPressureListviewText);
        chartLeftButton = view.findViewById(R.id.bloodPressureChartLeft);
        chartRightButton = view.findViewById(R.id.bloodPressureChartRight);
        chart = (LineChart) view.findViewById(R.id.bloodPressureChart);

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
        lastResultIndex = bloodPressureResultArrayList.size() - 1;
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysBloodPressureResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("DefaultLocale")
    private void loadLastResults(){
        listViewArrayList = new ArrayList<>();
        int i = 0;
        while   (listViewArrayList.size() < 7 &&
                (i < bloodPressureResultArrayList.size() || bloodPressureResultArrayList.size() < 2) &&
                (lastResultIndex - i) >= 0 &&
                (lastResultIndex - i) < bloodPressureResultArrayList.size()){

            BloodPressureResult result = new BloodPressureResult(bloodPressureResultArrayList.get(lastResultIndex - i).getDate(),
                    bloodPressureResultArrayList.get(lastResultIndex - i).getHour(),
                    bloodPressureResultArrayList.get(lastResultIndex - i).getSystolicResult(),
                    bloodPressureResultArrayList.get(lastResultIndex - i).getDiastolicResult(),
                    bloodPressureResultArrayList.get(lastResultIndex - i).getPulse(),
                    bloodPressureResultArrayList.get(lastResultIndex - i).getSaturation(),
                    bloodPressureResultArrayList.get(lastResultIndex - i).getAnnotation()
            );
            listViewArrayList.add(result);

            i += 1;
            if (bloodPressureResultArrayList.size() < 2) {
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
        if (lastResultIndex >= bloodPressureResultArrayList.size()){
            lastResultIndex = bloodPressureResultArrayList.size() - 1;
        }
        if (lastResultIndex >= bloodPressureResultArrayList.size() - 7){
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
        if (lastResultIndex < bloodPressureResultArrayList.size() - 7){
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
        LineDataSet lineDataSet = new LineDataSet(chartSystolicArrayList, "Ciśnienie skurczowe [mmHg]");
        lineDataSet.setColors(Color.RED);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(10f);

        LineDataSet lineDataSet1 = new LineDataSet(chartDiastolicArrayList, "Ciśnienie rozkurczowe [mmHg]");
        lineDataSet1.setColors(Color.BLUE);
        lineDataSet1.setLineWidth(2f);
        lineDataSet1.setValueTextColor(Color.BLACK);
        lineDataSet1.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(chartSystolicArrayList.size() - 1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));
        chart.getDescription().setEnabled(false);
        chart.setData(data);
        chart.animateXY(2000, 2000);
        chart.notifyDataSetChanged();
        chart.getAxisRight().setEnabled(true);
        chart.invalidate();
    }

    @Override
    public void onStart() {
        loadResults();
        lastResultIndex = bloodPressureResultArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysBloodPressureResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onStart();
    }

    @Override
    public void onResume() {
        loadResults();
        lastResultIndex = bloodPressureResultArrayList.size() - 1;
        currentListViewPage = 1;
        nextListViewResultsButton.setEnabled(false);
        previousListViewResultsButton.setEnabled(true);
        if (lastResultIndex < 7) {
            previousListViewResultsButton.setEnabled(false);
        }
        last7DaysBloodPressureResultsArrayList = getLast7DaysResults();
        loadLastResults();
        loadChart();
        reloadListView();
        super.onResume();
    }

    private void getData(){
        chartSystolicArrayList = new ArrayList();
        chartDiastolicArrayList = new ArrayList();
        int i = 0;
        bloodPressureResultArrayList = sortBloodPressureResults(bloodPressureResultArrayList);
        last7DaysBloodPressureResultsArrayList = sortBloodPressureResults(last7DaysBloodPressureResultsArrayList);
        for(BloodPressureResult bloodPressureResult : last7DaysBloodPressureResultsArrayList){
            chartSystolicArrayList.add(new Entry(i, (float) bloodPressureResult.getSystolicResult()));
            chartDiastolicArrayList.add(new Entry(i, (float) bloodPressureResult.getDiastolicResult()));
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

    private ArrayList<BloodPressureResult> getLast7DaysResults(){
        ArrayList<BloodPressureResult> chartArrayList = new ArrayList<>();

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

            BloodPressureResult blankResult = new BloodPressureResult(day + " " + SugarEditorFragment.getMonthFormat(month) + " " + year, "00:00", 0, 0, 0, 0, "");
            chartArrayList.add(blankResult);

            for (BloodPressureResult bloodPressureResult : bloodPressureResultArrayList){
                if (bloodPressureResult.getDay() == day && bloodPressureResult.getMonth() == month && bloodPressureResult.getYear() == year){
                    chartArrayList.set(i, bloodPressureResult);
                }
            }
        }

        return chartArrayList;
    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        bloodPressureResultArrayList = sortBloodPressureResults(bloodPressureResultArrayList);
        for(BloodPressureResult bloodPressureResult : last7DaysBloodPressureResultsArrayList){
            xAxis.add(String.format(Locale.getDefault(), "%02d.%02d", bloodPressureResult.getDay(), bloodPressureResult.getMonth()));
        }
        return xAxis;
    }

    private void getLastResult(){
        lastResult.setText(MessageFormat.format("Ostatni wynik = {0} / {1}", 0, 0));
        if (!bloodPressureResultArrayList.isEmpty()){
            lastResult.setText(MessageFormat.format("Ostatni wynik = {0} / {1} [ mmHg / mmHg ]",
                    bloodPressureResultArrayList.get(bloodPressureResultArrayList.size() - 1).getSystolicResult(),
                    bloodPressureResultArrayList.get(bloodPressureResultArrayList.size() - 1).getDiastolicResult()));
        }
    }

    private void meanBloodPressureResults(){
        double sum1 = 0;
        double sum2 = 0;
        for (BloodPressureResult bloodPressureResult : bloodPressureResultArrayList) {
            sum1 += bloodPressureResult.getSystolicResult();
            sum2 += bloodPressureResult.getDiastolicResult();
        }
        meanResults.setText(MessageFormat.format("Średnia wyników = {0} / {1} [ mmHg / mmHg ]", sum1/bloodPressureResultArrayList.size(), sum2/bloodPressureResultArrayList.size() ));
    }

    private void reloadChart(){
        chart.fitScreen();
        last7DaysBloodPressureResultsArrayList = getLast7DaysResults();
        loadChart();
    }

    private void resetChart(){
        chartLastDay = todayDate;
        reloadChart();
    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("bloodPressure", null);

        Type type = new TypeToken<ArrayList<BloodPressureResult>>() {}.getType();

        bloodPressureResultArrayList = gson.fromJson(json, type);

        if (bloodPressureResultArrayList == null) {
            bloodPressureResultArrayList = new ArrayList<>();
        }
    }

    private ArrayList<BloodPressureResult> sortBloodPressureResults(ArrayList<BloodPressureResult> results){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<BloodPressureResult> sorted = SortHelperClass.sortBloodPressureResults(results);
        String json1 = gson.toJson(sorted);
        editor.putString("bloodPressure", json1);
        editor.apply();
        return sorted;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        results.clear();

        int numberOfElementsOnPage = 0;
        bloodPressureResultArrayList = sortBloodPressureResults(bloodPressureResultArrayList);
        listViewArrayList = SortHelperClass.sortBloodPressureResults(listViewArrayList);

        for (BloodPressureResult bloodPressureResult : listViewArrayList) {
            results.add(bloodPressureResult.getDate() + ", " + bloodPressureResult.getHour());
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
                intent.putExtra("type", "Ciśnienie krwi, puls i saturacja");
                if (finalNumberOfElementsOnPage == 7){
                    intent.putExtra("position", bloodPressureResultArrayList.size() - 7 * currentListViewPage + position);
                }
                else {
                    intent.putExtra("position", bloodPressureResultArrayList.size() - 7 * (currentListViewPage - 1) + position - finalNumberOfElementsOnPage);
                }
                startActivity(intent);
            }
        });

        getLastResult();
        meanBloodPressureResults();

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