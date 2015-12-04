package com.sen.test.util;

import java.util.Calendar;

/**
 * Created by Administrator on 14-9-24.
 */
public class RecorderDate {

    public static final int DATE_TYPE_TODAY = 0x03;
    public static final int DATE_TYPE_A_MONTH = 0x04;
    public static final int DATE_TYPE_THREE_MONTHS = 0x05;
    public static final int DATE_TYPE_A_HALF_OF_YEAR = 0x06;

    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    int dayOfWeek;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public static RecorderDate newInstance(long millis){

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        return newInstance(c);
    }

    public static RecorderDate newInstance(Calendar c){

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int millis = c.get(Calendar.SECOND);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        RecorderDate d = new RecorderDate();
        d.year = year;
        d.month = month;
        d.day = day;
        d.hour = hour;
        d.minute = minute;
        d.second = millis;
        d.dayOfWeek = dayOfWeek;

        return d;
    }

    public boolean isToday(){
        Calendar c = Calendar.getInstance();
        RecorderDate today = newInstance(c);

        return equals(today);
    }

    public static long setMillisecondAndSecondToNull(long times) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(times);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long getATodayStartTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static long getStartTimeByNumMonths(int num) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, -num);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static long getMonDayOfWeek(long timeMillis) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(timeMillis);
////        c.setFirstDayOfWeek(Calendar.MONDAY);
//        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        c.add(Calendar.DATE, -dayofweek + 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    @Override
    public boolean equals(Object d){
        RecorderDate date = (RecorderDate)d;
        return date.day == day && date.month == month && date.year == year;
    }

    @Override
    public String toString(){
        return year+"-"+
                formatData(month+1)+"-"+formatData(day)+/*"\n"+*/" "+
                formatData(hour)+":"+formatData(minute)+":"+formatData(second);
    }

    public String toSimpleString() {
        return year+"-"+
                formatData(month+1)+"-"+formatData(day)+/*"\n"+*/"  "+
                formatData(hour)+":"+formatData(minute);
    }

    private String formatData(int data){
        return String.format("%02d", data);
    }


}
