package com.example.healthapp;

import java.util.Calendar;

public class MedicalVisitScheduleItem {
    private String date;
    private String time;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;
    private String doctorName;
    private String visitName;
    private String address;
    private String status;
    private int numberId;
    private int number;

    public MedicalVisitScheduleItem(String date, String time, String name, String address, String doctor, String status){
        this.date = date;
        this.time = time;
        this.year = Integer.parseInt(date.split(" ")[2]);
        this.month = SugarResult.getMonthInt(date.split(" ")[1]);
        this.day = Integer.parseInt(date.split(" ")[0]);
        this.hour = Integer.parseInt(time.split(":")[0]);
        this.minutes = Integer.parseInt(time.split(":")[1]);
        this.doctorName = doctor;
        this.visitName = name;
        this.address = address;
        this.status = status;
        this.number = this.hour * 60 + this.minutes;
        this.numberId = (year-2020) * 365 * 1440 * 60 + month * 31 * 1440 * 60 + day * 1440 * 60 + hour * 60 * 60 + minutes * 60 + (int) (Calendar.getInstance().getTimeInMillis() % 60);
    }

    public String getTime() {
        return time;
    }

    public int getNumberId() {
        return numberId;
    }

    public void setNumberId(int numberId) {
        this.numberId = numberId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
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

    public String getAddress() {
        return address;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getStatus() {
        return status;
    }

    public String getVisitName() {
        return visitName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setHour(int hour) {
        this.hour = hour;
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

    public void setYear(int year) {
        this.year = year;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVisitName(String visitName) {
        this.visitName = visitName;
    }
}
