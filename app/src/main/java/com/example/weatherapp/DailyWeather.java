package com.example.weatherapp;

public class DailyWeather {
    private double temperature;
    private String icon,timeZone;
    private long date;

    public double getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }

    public String getTimeZone() {return timeZone; }

    public long getDate() {return date;}

    public DailyWeather(double temperature, String icon) {
        this.temperature = temperature;
        this.icon = icon;
    }

    public DailyWeather(double temperature, String icon, String timeZone, long date) {
        this.temperature = temperature;
        this.icon = icon;
        this.timeZone = timeZone;
        this.date = date;
    }
}
