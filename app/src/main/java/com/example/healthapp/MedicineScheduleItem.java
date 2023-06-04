package com.example.healthapp;

import java.util.Calendar;

public class MedicineScheduleItem {
    private String date;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;
    private String time;
    private String medicineName;
    private String dose;
    private String annotation;
    private int number;
    private int idNumber;
    private String periodicity = "";
    private String dateFrom;
    private int yearFrom;
    private int monthFrom;
    private int dayFrom;
    private String dateTo;
    private int yearTo;
    private int monthTo;
    private int dayTo;
    private String dayOfWeek;

    public MedicineScheduleItem(String date, String time, String name, String dose, String annot){
        this.date = date;
        this.time = time;
        this.year = Integer.parseInt(date.split(" ")[2]);
        this.month = Utils.getMonthInt(date.split(" ")[1]);
        this.day = Integer.parseInt(date.split(" ")[0]);
        this.hour = Integer.parseInt(time.split(":")[0]);
        this.minutes = Integer.parseInt(time.split(":")[1]);
        this.medicineName = name;
        this.dose = dose;
        this.annotation = annot;
        this.number = this.hour * 60 + this.minutes;
        this.idNumber = (year-2020) * 365 * 1440 * 60 + month * 31 * 1440 * 60 + day * 1440 * 60 + hour * 60 * 60 + minutes * 60 + (int) (Calendar.getInstance().getTimeInMillis() % 60);
    }

    public MedicineScheduleItem(String periodicity, String dayOfWeek, String time, String name, String dose, String annot){
        this.periodicity = periodicity;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.hour = Integer.parseInt(time.split(":")[0]);
        this.minutes = Integer.parseInt(time.split(":")[1]);
        this.medicineName = name;
        this.dose = dose;
        this.annotation = annot;
        this.number = this.hour * 60 + this.minutes;
        this.idNumber = (year-2020) * 365 * 1440 * 60 + month * 31 * 1440 * 60 + day * 1440 * 60 + hour * 60 * 60 + minutes * 60 + (int) (Calendar.getInstance().getTimeInMillis() % 60);
    }

    public MedicineScheduleItem(String periodicity, String dateFrom, String dateTo, String time, String name, String dose, String annot){
        this.periodicity = periodicity;
        this.dateFrom = dateFrom;
        this.yearFrom = Integer.parseInt(dateFrom.split(" ")[2]);
        this.monthFrom = Utils.getMonthInt(dateFrom.split(" ")[1]);
        this.dayFrom = Integer.parseInt(dateFrom.split(" ")[0]);
        this.dateTo = dateTo;
        this.yearTo = Integer.parseInt(dateTo.split(" ")[2]);
        this.monthTo = Utils.getMonthInt(dateTo.split(" ")[1]);
        this.dayTo = Integer.parseInt(dateTo.split(" ")[0]);
        this.time = time;
        this.hour = Integer.parseInt(time.split(":")[0]);
        this.minutes = Integer.parseInt(time.split(":")[1]);
        this.medicineName = name;
        this.dose = dose;
        this.annotation = annot;
        this.number = this.hour * 60 + this.minutes;
        this.idNumber = (year-2020) * 365 * 1440 * 60 + month * 31 * 1440 * 60 + day * 1440 * 60 + hour * 60 * 60 + minutes * 60 + (int) (Calendar.getInstance().getTimeInMillis() % 60);
    }

    public int getYear() {
        return year;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public int getMonth() {
        return month;
    }

    public int getNumber() {
        return number;
    }

    public int getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getAnnotation() {
        return annotation;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getDayFrom() {
        return dayFrom;
    }

    public int getDayTo() {
        return dayTo;
    }

    public int getHour() {
        return hour;
    }

    public int getMonthFrom() {
        return monthFrom;
    }

    public int getMonthTo() {
        return monthTo;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDose() {
        return dose;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getTime() {
        return time;
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

    public void setDay(int day) {
        this.day = day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public void setDayFrom(int dayFrom) {
        this.dayFrom = dayFrom;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setDayTo(int dayTo) {
        this.dayTo = dayTo;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setMonthFrom(int monthFrom) {
        this.monthFrom = monthFrom;
    }

    public void setMonthTo(int monthTo) {
        this.monthTo = monthTo;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }
}
