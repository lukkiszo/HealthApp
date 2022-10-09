package com.example.healthapp;

import android.util.Log;

import java.util.Date;

public class SugarResult implements Comparable<SugarResult>{

    private String date;
    private int year;
    private int month;
    private int day;
    private int thour;
    private int minutes;
    private String hour;
    private double result;
    private String annotation;
    private int number;

    public SugarResult(String addedDate, String addedHour, double res, String annot, int year, int month, int day){
        this.date = addedDate;
        this.hour = addedHour;
        this.result = res;
        this.annotation = annot;
        this.year = year;
        this.month = month;
        this.day = day;
        this.thour = Integer.parseInt(addedHour.split(":")[0]);
        this.minutes = Integer.parseInt(addedHour.split(":")[1]);
        this.number = (year-2020) * 365 * 1440 + month * 31 * 1440 + day * 1440 + thour * 60 + minutes;
    }

    public String getDate() {
        return date;
    }

    public String getHour() { return hour; }

    public void setHour(String hour) { this.hour = hour; }

    public double getResult() {
        return result;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
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

    public int compareTo(SugarResult compareResult) {

        int compareQuantity = ((SugarResult) compareResult).getNumber();

        //ascending order
        return this.getNumber() - compareQuantity;

        //descending order
        //return compareQuantity - this.quantity;

    }

}
