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
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

public class SugarResultsFragment extends Fragment {

    private static ArrayList<SugarResult> sugarResultsArrayList;
    private View currentView;
    private ListView listView;
    private TextView BMI_text;
    private TextView BMI_info;
    private ImageButton addButton;
//    private ArrayList<SugarResult> sugarResultsArrayList;
    private TextView lastResult;
    private TextView meanResults;
    private List<String> results = new ArrayList<>();

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

        BMI_text = view.findViewById(R.id.BMI);
        BMI_info = view.findViewById(R.id.BMI_info);
        listView = view.findViewById(R.id.listview);
        addButton = view.findViewById(R.id.addSugarResultButton);
        lastResult = view.findViewById(R.id.lastSugarResult);
        meanResults = view.findViewById(R.id.meanSugarResults);

        BMI_text.setText(MessageFormat.format("BMI = {0}", BMI_count()));
        checkBMI(BMI_count());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewResult();
            }
        });

        loadResults();
    }

    @Override
    public void onStart() {
        loadResults();
        super.onStart();
    }

    private int getLastResult(){
        if (!sugarResultsArrayList.isEmpty()){
            return (int) sugarResultsArrayList.get(sugarResultsArrayList.size() - 1).getResult();
        }
        return 0;
    }

    private double meanSugarResults(){
        double sum = 0;
        for (SugarResult sugarResult : sugarResultsArrayList) {
            sum += sugarResult.getResult();
        }
        return sum/sugarResultsArrayList.size();
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
                BMI_info.setText("Stan wychudzenia!");
                if(BMI < 16) BMI_info.setText("Stan wygłodzenia!");
            }

            if(BMI >= 30){
                BMI_info.setText("Stan otyłości 1 stopnia!");
                if(BMI >= 35) BMI_info.setText("Stan otyłości 2 stopnia!");
                if(BMI >= 40) BMI_info.setText("Stan otyłości 3 stopnia!");
            }

        }
        else if (BMI < 18.5 || BMI >= 25){
            if (BMI < 18.5) BMI_info.setText("Stan niedowagi!");
            if (BMI >= 25) BMI_info.setText("Stan nadwagi!");

            BMI_text.setTextColor(Color.YELLOW);
        }
        else {
            BMI_text.setTextColor(Color.GREEN);
            BMI_info.setText("BMI prawidłowe.");
        }
    }

    private void addNewResult(){
        Intent intent = new Intent(getActivity(), ResultEditor.class);
        intent.putExtra("type", "Cukier");
        startActivity(intent);

    }

    private void loadResults(){
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("sugar", null);

        Type type = new TypeToken<ArrayList<SugarResult>>() {}.getType();

        sugarResultsArrayList = gson.fromJson(json, type);

        if (sugarResultsArrayList == null) {
            sugarResultsArrayList = new ArrayList<>();
        }

        reloadListView();
    }

    private void sortSugarResults(ArrayList<SugarResult> results){
        ArrayList<SugarResult> sorted = results;

        int pos;
        SugarResult temp;

        for(int i = 0; i < sorted.size() - 1; i++){
            pos = i;
            for (int j = i + 1; j < sorted.size(); j++){
                if(sorted.get(j).getNumber() < sorted.get(pos).getNumber()){
                    pos = j;
                }
            }

            temp = sorted.get(pos);
            sorted.set(pos, sorted.get(i));
            sorted.set(i, temp);

        }

        sugarResultsArrayList = sorted;

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("results", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json1 = gson.toJson(sugarResultsArrayList);
        editor.putString("sugar", json1);
        editor.apply();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void reloadListView(){
        results.clear();

        sortSugarResults(sugarResultsArrayList);

        for (SugarResult sugarResult : sugarResultsArrayList) {
            results.add(sugarResult.getDate() + ", " + sugarResult.getHour());
        }

        ArrayAdapter<String> arr = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, results);
        listView.setAdapter(arr);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent intent = new Intent(getActivity(), ResultEditor.class);
                intent.putExtra("type", "Cukier");
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        lastResult.setText(MessageFormat.format("Ostatni wynik = {0} mg/dl", getLastResult()));
        meanResults.setText(MessageFormat.format("Średnia wyników = {0} mg/dl", meanSugarResults()));

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