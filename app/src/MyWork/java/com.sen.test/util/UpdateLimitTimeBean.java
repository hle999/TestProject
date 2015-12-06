package com.sen.test.util;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;


/**
 * Editor: sgc
 * Date: 2015/03/02
 */
public class UpdateLimitTimeBean extends JSONObject {

    @SerializedName("responseNo")
    private int responseNo;

    @SerializedName("msg")
    private String msg;

    @SerializedName("morning_start_time")
    private String limitMorningStartTime;

    @SerializedName("morning_end_time")
    private String limitMorningEndTime;

    @SerializedName("afternoon_start_time")
    private String limitAfternoonStartTime;

    @SerializedName("afternoon_end_time")
    private String limitAfternoonEndTime;

    @SerializedName("night_start_time")
    private String limitNightStartTime;

    @SerializedName("night_end_time")
    private String limitNightEndTime;

    @SerializedName("delay_hour")
    private String delayLimitHour;

    @SerializedName("buckets")
    private String buckets;

    public int getResponseNo() {
        return responseNo;
    }

    public void setResponseNo(int responseNo) {
        this.responseNo = responseNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLimitMorningStartTime() {
        return limitMorningStartTime;
    }

    public void setLimitMorningStartTime(String limitMorningStartTime) {
        this.limitMorningStartTime = limitMorningStartTime;
    }

    public String getLimitMorningEndTime() {
        return limitMorningEndTime;
    }

    public void setLimitMorningEndTime(String limitMorningEndTime) {
        this.limitMorningEndTime = limitMorningEndTime;
    }

    public String getLimitAfternoonStartTime() {
        return limitAfternoonStartTime;
    }

    public void setLimitAfternoonStartTime(String limitAfternoonStartTime) {
        this.limitAfternoonStartTime = limitAfternoonStartTime;
    }

    public String getLimitAfternoonEndTime() {
        return limitAfternoonEndTime;
    }

    public void setLimitAfternoonEndTime(String limitAfternoonEndTime) {
        this.limitAfternoonEndTime = limitAfternoonEndTime;
    }

    public String getLimitNightStartTime() {
        return limitNightStartTime;
    }

    public void setLimitNightStartTime(String limitNightStartTime) {
        this.limitNightStartTime = limitNightStartTime;
    }

    public String getLimitNightEndTime() {
        return limitNightEndTime;
    }

    public void setLimitNightEndTime(String limitNightEndTime) {
        this.limitNightEndTime = limitNightEndTime;
    }

    public String getDelayLimitHour() {
        return delayLimitHour;
    }

    public void setDelayLimitHour(String delayLimitHour) {
        this.delayLimitHour = delayLimitHour;
    }

    public String getBuckets() {
        return buckets;
    }

    public void setBuckets(String buckets) {
        this.buckets = buckets;
    }

    @Override
    public String toString() {
        return limitMorningStartTime+" "+limitMorningEndTime+" "+limitAfternoonStartTime+" "+limitAfternoonEndTime+" "
                +limitNightStartTime+" "+limitNightEndTime+" "+delayLimitHour+" "+buckets;
    }
}
