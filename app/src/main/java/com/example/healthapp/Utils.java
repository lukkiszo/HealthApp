package com.example.healthapp;

import java.util.Calendar;
import java.util.Locale;

public class Utils {
    public static int getIntDayOfWeek(String day){
        switch (day){
            case "Poniedziałek":
                return Calendar.MONDAY;

            case "Wtorek":
                return Calendar.TUESDAY;

            case "Środa":
                return Calendar.WEDNESDAY;

            case "Czwartek":
                return Calendar.THURSDAY;

            case "Piątek":
                return Calendar.FRIDAY;

            case "Sobota":
                return Calendar.SATURDAY;

            case "Niedziela":
                return Calendar.SUNDAY;
        }
        return 0;
    }

    public static String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public static String getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);
        return String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute);
    }

    public static String makeDateString(int day, int month, int year){
        return day + " " + getMonthFormat(month) + " " + year;
    }

    public static String getMonthFormat(int month){
        switch (month) {
            case 1:
                return "Styczeń";

            case 2:
                return "Luty";

            case 3:
                return "Marzec";

            case 4:
                return "Kwiecień";

            case 5:
                return "Maj";

            case 6:
                return "Czerwiec";

            case 7:
                return "Lipiec";

            case 8:
                return "Sierpień";

            case 9:
                return "Wrzesień";

            case 10:
                return "Październik";

            case 11:
                return "Listopad";

            case 12:
                return "Grudzień";
        }
        return "";
    }

    public static int getMonthInt(String month){
        switch (month) {
            case "Styczeń":
                return 1;

            case "Luty":
                return 2;

            case "Marzec":
                return 3;

            case "Kwiecień":
                return 4;

            case "Maj":
                return 5;

            case "Czerwiec":
                return 6;

            case "Lipiec":
                return 7;

            case "Sierpień":
                return 8;

            case "Wrzesień":
                return 9;

            case "Październik":
                return 10;

            case "Listopad":
                return 11;

            case "Grudzień":
                return 12;
        }
        return 1;
    }

    public static String getDayString(int numberOfDay){
        switch (numberOfDay) {
            case 1:
                return "Poniedziałek";

            case 2:
                return "Wtorek";

            case 3:
                return "Środa";

            case 4:
                return "Czwartek";

            case 5:
                return "Piątek";

            case 6:
                return "Sobota";

            case 0:
                return "Niedziela";

        }
        return "";
    }

}
