package com.example.healthapp;

import java.util.ArrayList;

public class SortHelperClass {

    public static ArrayList<SugarResult> sortSugarResults(ArrayList<SugarResult> sugarResultsArrayList) {
        ArrayList<SugarResult> sorted = sugarResultsArrayList;

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

        return sorted;
    }

    public static ArrayList<TemperatureResult> sortTemperatureResults(ArrayList<TemperatureResult> temperatureResultArrayList) {
        ArrayList<TemperatureResult> sorted = temperatureResultArrayList;

        int pos;
        TemperatureResult temp;

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

        return sorted;
    }

    public static ArrayList<BloodPressureResult> sortBloodPressureResults(ArrayList<BloodPressureResult> bloodPressureResultArrayList) {
        ArrayList<BloodPressureResult> sorted = bloodPressureResultArrayList;

        int pos;
        BloodPressureResult temp;

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

        return sorted;
    }

    public static ArrayList<StepsResult> sortStepsResults(ArrayList<StepsResult> stepsResultArrayList) {
        ArrayList<StepsResult> sorted = stepsResultArrayList;

        int pos;
        StepsResult temp;

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

        return sorted;
    }

    public static ArrayList<MedicineScheduleItem> sortMedicineItems(ArrayList<MedicineScheduleItem> medicinesArrayList) {
        ArrayList<MedicineScheduleItem> sorted = medicinesArrayList;

        int pos;
        MedicineScheduleItem temp;

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

        return sorted;
    }

}
