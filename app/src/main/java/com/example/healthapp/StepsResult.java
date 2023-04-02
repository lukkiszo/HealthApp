package com.example.healthapp;

public class StepsResult {
    private String date;
    private int year;
    private int month;
    private int day;
    private int result;
    private int number;
    private int absoluteResult;

    public StepsResult(String addedDate, int res, int absoluteResult){
        this.date = addedDate;
        this.result = res;
        this.year = Integer.parseInt(addedDate.split(" ")[2]);
        this.month = SugarResult.getMonthInt(addedDate.split(" ")[1]);
        this.day = Integer.parseInt(addedDate.split(" ")[0]);
        this.absoluteResult = absoluteResult;
        this.number = (year-2020) * 365 * 1440 + month * 31 * 1440 + day * 1440 + 23 * 60;
    }

    public String getDate() {
        return date;
    }

    public int getDay() {
        return day;
    }

    public int getNumber() {
        return number;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getResult() {
        return result;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setAbsoluteResult(int absoluteResult) {
        this.absoluteResult = absoluteResult;
    }

    public int getAbsoluteResult() {
        return absoluteResult;
    }
}
