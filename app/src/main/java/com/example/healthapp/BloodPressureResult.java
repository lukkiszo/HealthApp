package com.example.healthapp;

public class BloodPressureResult implements Comparable<BloodPressureResult>{
    private String date;
    private int year;
    private int month;
    private int day;
    private int thour;
    private int minutes;
    private String hour;
    private int systolicResult; // skurczowe
    private int diastolicResult; // rozkurczowe
    private int pulse;
    private int saturation;
    private String annotation;
    private int number;

    public BloodPressureResult(String addedDate, String addedHour, int res1, int res2, int res3, int res4, String annot){
        this.date = addedDate;
        this.hour = addedHour;
        this.systolicResult = res1;
        this.diastolicResult = res2;
        this.pulse = res3;
        this.saturation = res4;
        this.annotation = annot;
        this.year = Integer.parseInt(addedDate.split(" ")[2]);
        this.month = Utils.getMonthInt(addedDate.split(" ")[1]);
        this.day = Integer.parseInt(addedDate.split(" ")[0]);
        this.thour = Integer.parseInt(addedHour.split(":")[0]);
        this.minutes = Integer.parseInt(addedHour.split(":")[1]);
        this.number = (year-2020) * 365 * 1440 + month * 31 * 1440 + day * 1440 + thour * 60 + minutes;
    }

    public int getYear() {
        return year;
    }

    public int getThour() {
        return thour;
    }

    public int getMonth() {
        return month;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getAnnotation() {
        return annotation;
    }

    public int getDiastolicResult() {
        return diastolicResult;
    }

    public int getNumber() {
        return number;
    }

    public int getPulse() {
        return pulse;
    }

    public int getSaturation() {
        return saturation;
    }

    public int getSystolicResult() {
        return systolicResult;
    }

    public String getDate() {
        return date;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setThour(int thour) {
        this.thour = thour;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setDiastolicResult(int diastolicResult) {
        this.diastolicResult = diastolicResult;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public void setSystolicResult(int systolicResult) {
        this.systolicResult = systolicResult;
    }

    public int compareTo(BloodPressureResult compareResult) {

        int compareQuantity = ((BloodPressureResult) compareResult).getNumber();

        //ascending order
        return this.getNumber() - compareQuantity;

        //descending order
        //return compareQuantity - this.quantity;

    }
}

