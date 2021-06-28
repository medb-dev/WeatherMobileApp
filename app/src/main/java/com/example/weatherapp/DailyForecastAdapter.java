package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.koushikdutta.ion.Ion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DailyForecastAdapter  extends ArrayAdapter<DailyWeather> {

    private Context context;
    private List<DailyWeather> weatherList;

    public DailyForecastAdapter(@NonNull Context context, @NonNull List<DailyWeather> weatherList) {
        super(context,0, weatherList);
        this.context = context;
        this.weatherList = weatherList;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(
                R.layout.item_forecast_weather,
                parent,
                false);
        TextView date = convertView.findViewById(R.id.item_date);
        TextView temp = convertView.findViewById(R.id.item_temp);
        ImageView icon = convertView.findViewById(R.id.item_icon);

        DailyWeather weather = weatherList.get(position);
        String tmp = Integer.toString((int) Math.rint(weather.getTemperature()));
        temp.setText(tmp+"°C");
        Ion.with(context).load("http://openweathermap.org/img/wn/"+weather.getIcon()+".png").intoImageView(icon);

        Date dt = new Date(weather.getDate()*1000);
        DateFormat df = new SimpleDateFormat("EEEE,MMM", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(weather.getTimeZone()));
        date.setText(df.format(dt));

        return convertView;
    }
}
