package com.example.weatherapp;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
    private String temperature,icon,weatherType,cityName,countryName;
    private double longitude,latitude;
    private int condition;

    public String getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public double getLongitude() {return longitude;}

    public double getLatitude() {return latitude;}



    public static Weather fromJson(JSONObject jObject){

        try{
            Weather weather = new Weather();
            weather.cityName = jObject.getString("name");
            weather.countryName = jObject.getJSONObject("sys").getString("country");
            weather.condition = jObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weather.weatherType = jObject.getJSONArray("weather").getJSONObject(0).getString("main");

            weather.longitude = jObject.getJSONObject("coord").getDouble("lon");
            weather.latitude= jObject.getJSONObject("coord").getDouble("lat");

            weather.icon = jObject.getJSONArray("weather").getJSONObject(0).getString("icon"); //updateWeatherIcon(weather.condition);

            weather.temperature = Integer.toString(
                    (int) Math.rint(
                            jObject.getJSONObject("main").getDouble("temp") //-273.15 to switch from F to C
                    ));

            return weather;
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Weather fromJson(JsonObject jObject) {
        try{
            Weather weather = new Weather();
            weather.cityName = jObject.get("name").getAsString();
            weather.countryName = jObject.get("sys").getAsJsonObject().get("country").getAsString();
            weather.condition = jObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
            weather.weatherType = jObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();

            weather.longitude = jObject.get("coord").getAsJsonObject().get("lon").getAsDouble();
            weather.latitude= jObject.get("coord").getAsJsonObject().get("lat").getAsDouble();

            weather.icon = jObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString(); //updateWeatherIcon(weather.condition);

            weather.temperature = Integer.toString(
                    (int) Math.rint(
                            jObject.get("main").getAsJsonObject().get("temp").getAsDouble() //-273.15 to switch from F to C
                    ));

            return weather;
        }catch(JsonIOException e){
            e.printStackTrace();
            return null;
        }
    }


    /*
    private static String updateWeatherIcon(int condition){

        if(condition == 800){
            return "clear";
        }else if(condition>=0 && condition < 300){
            return "thunderstorm";
        }else if(condition>=300 && condition < 500){
            return "drizzle";
        }else if(condition>=500 && condition < 600){
            return "rain";
        }else if(condition>=600 && condition < 700){
            return "snow";
        }else if(condition>=701 && condition < 801){
            return "atmosphere";
        }else if(condition>=801 && condition < 804){
            return "clouds";
        }else if(condition>=900 && condition < 902){
            return "thunderstorm";
        }else if(condition==903){
            return "snow";
        }
        else if(condition==904){
            return "sunny";
        }
        else if(condition>=905 && condition < 1000){
            return "thunderstorm";
        }
        return"unknown";
    }
    */

}


/*
     -- JSON DATA --

      "coord": {
        "lon": -122.08,
        "lat": 37.39
      },

      "weather": [
        {
          "id": 800,
          "main": "Clear",
          "description": "clear sky",
          "icon": "01d"
        }
      ],

      "base": "stations",
      "main": {
        "temp": 282.55,
        "feels_like": 281.86,
        "temp_min": 280.37,
        "temp_max": 284.26,
        "pressure": 1023,
        "humidity": 100
      },

      "visibility": 16093,

      "wind": {
        "speed": 1.5,
        "deg": 350
      },

      "clouds": {
        "all": 1
      },

      "dt": 1560350645,

      "sys": {
        "type": 1,
        "id": 5122,
        "message": 0.0139,
        "country": "US",
        "sunrise": 1560343627,
        "sunset": 1560396563
      },

      "timezone": -25200,

      "id": 420006353,

      "name": "Mountain View",

      "cod": 200

*/