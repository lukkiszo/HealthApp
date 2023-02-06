package com.example.healthapp;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class TemperatureResult implements Comparable<TemperatureResult>{

    private String date;
    private int year;
    private int month;
    private int day;
    private int thour;
    private int minutes;
    private String hour;
    private double result;
    private int number;

    public TemperatureResult(String addedDate, String addedHour, double res){
        this.date = addedDate;
        this.hour = addedHour;
        this.result = res;
        this.year = Integer.parseInt(addedDate.split(" ")[2]);
        this.month = SugarResult.getMonthInt(addedDate.split(" ")[1]);
        this.day = Integer.parseInt(addedDate.split(" ")[0]);
        this.thour = Integer.parseInt(addedHour.split(":")[0]);
        this.minutes = Integer.parseInt(addedHour.split(":")[1]);
        this.number = year * 12 * 31 * 1440 + month * 31 * 1440 + day * 1440 + thour * 60 + minutes;
    }

    public String getDate() {
        return date;
    }

    public String getHour() { return hour; }

    public void setHour(String hour) { this.hour = hour; }

    public double getResult() {
        return result;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public int getNumber() { return number; }

    public int getDay() { return day; }

    public int getMinutes() { return minutes; }

    public int getMonth() { return month; }

    public int getThour() { return thour; }

    public int getYear() { return year; }

    public void setDay(int day) { this.day = day; }

    public void setMinutes(int minutes) { this.minutes = Integer.parseInt(this.hour.split(":")[1]); }

    public void setNumber(int number) { this.number = number; }

    public void setThour(int thour) { this.thour = Integer.parseInt(this.hour.split(":")[0]); }

    public void setMonth(int month) { this.month = month; }

    public void setYear(int year) { this.year = year; }

    @Override
    public int compareTo(TemperatureResult temperatureResult) {
        int compareQuantity = ((TemperatureResult) temperatureResult).getNumber();

        //ascending order
        return this.getNumber() - compareQuantity;
    }
}
