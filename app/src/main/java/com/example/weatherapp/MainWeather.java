package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import cz.msebera.android.httpclient.Header;

public class MainWeather extends AppCompatActivity {
    //https://api.openweathermap.org/data/2.5/onecall?lat=28.9884&lon=10.0527&exclude=minutely,current,alerts&&units=metric&appid=b90258d4ab2232ea5bd8a73aa223d107
    final String API_KEY = "b90258d4ab2232ea5bd8a73aa223d107";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    Weather weather;
    String Location_Provider = LocationManager.GPS_PROVIDER;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    FirebaseAuth FireAuth;
    Animation animation_fadeIn, animation_fadeOut;

    LinearLayout today_layout, this_week_layout,search_overbox,search_dialog,menu_dialog;
    ListView this_week_list_view;
    TextView current_temperature,current_weather_type,current_city,current_country,today,this_week,search_bar,morning_degree,afternoon_degree,evening_degree;
    ImageView current_weather_icon , morning_icon , afternoon_icon, evening_icon;
    Button position_finder_button,search_btn,search_float_btn,menu_float_btn,signout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_weather);

        Utils.blackIconStatusBar(MainWeather.this, R.color.mostly_clear_Color);

        //FireBase Auth :
        FireAuth = FirebaseAuth.getInstance();

        //Animations :
        animation_fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animation_fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        //Layouts :
        today_layout = findViewById(R.id.today_layout);
        this_week_layout = findViewById(R.id.this_week_layout);
        search_overbox = findViewById(R.id.search_overbox);
        search_dialog = findViewById(R.id.search_dialog);
        menu_dialog = findViewById(R.id.menu_dialog);
        //ListView :
        this_week_list_view = findViewById(R.id.this_week_list_view);
        //EditText :
        search_bar = findViewById(R.id.search_bar);
        //TextViews :
        current_temperature = findViewById(R.id.current_temperature);
        current_weather_type = findViewById(R.id.current_weather_type);
        current_city = findViewById(R.id.current_city);
        current_country = findViewById(R.id.current_country);

        today = findViewById(R.id.today_text);
        this_week = findViewById(R.id.this_week_text);

        morning_degree = findViewById(R.id.morning_degree);
        afternoon_degree = findViewById(R.id.afternoon_degree);
        evening_degree = findViewById(R.id.evening_degree);
        //ImageViews :
        current_weather_icon = findViewById(R.id.current_weather_icon);

        morning_icon = findViewById(R.id.morning_icon);
        afternoon_icon = findViewById(R.id.afternoon_icon);
        evening_icon = findViewById(R.id.evening_icon);
        //Buttons :
        position_finder_button = findViewById(R.id.fin_position_button);
        search_btn = findViewById(R.id.search_button);
        search_float_btn = findViewById(R.id.search_float_button);
        menu_float_btn = findViewById(R.id.menu_float_button);
        signout_button = findViewById(R.id.signout_button);

        menu_float_btn.setOnClickListener(v -> new Handler().postDelayed(()->{
                search_overbox.setVisibility(View.VISIBLE);
                search_overbox.setAnimation(animation_fadeIn);
                menu_dialog.setVisibility(View.VISIBLE);
                menu_dialog.setAnimation(animation_fadeIn);
            },400));

        signout_button.setOnClickListener( v -> new Handler().postDelayed(()->{
                menu_dialog.setAnimation(animation_fadeOut);
                search_overbox.setAnimation(animation_fadeOut);
                menu_dialog.setVisibility(View.INVISIBLE);
                search_overbox.setVisibility(View.INVISIBLE);
                FireAuth.signOut();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            },400)
        );

        search_float_btn.setOnClickListener(v->{
            new Handler().postDelayed(()->{
                search_overbox.setVisibility(View.VISIBLE);
                search_overbox.setAnimation(animation_fadeIn);
                search_dialog.setVisibility(View.VISIBLE);
                search_dialog.setAnimation(animation_fadeIn);
            },400);
        });

        search_btn.setOnClickListener(v->{
            getWeatherForNewLocation();
            /*RequestParams params = new RequestParams();
            String city_name = search_bar.getText().toString().trim();
            params.put("q",city_name);
            params.put("appid",API_KEY);
            params.put("units","metric");
            CallApi(params);*/
            new Handler().postDelayed(()->{
                //today.setEnabled(false);
                //this_week.setEnabled(false);
                //this_week.setTextColor(getResources().getColor(R.color.hint_text_color));
                //today.setTextColor(getResources().getColor(R.color.hint_text_color));
                search_dialog.setAnimation(animation_fadeOut);
                search_overbox.setAnimation(animation_fadeOut);
                search_dialog.setVisibility(View.INVISIBLE);
                search_overbox.setVisibility(View.INVISIBLE);
                //hide details from previous result
                //today_layout.setVisibility(View.GONE);
                //this_week_layout.setVisibility(View.GONE);
            },300);
        });

        today.setOnClickListener(v -> {
            today.setTextColor(getResources().getColor(R.color.selected_text));
            this_week.setTextColor(getResources().getColor(R.color.hint_text_color));
            this_week_layout.setVisibility(View.GONE);
            this_week_layout.setAnimation(animation_fadeOut);
            new Handler().postDelayed(() -> {
                today_layout.setVisibility(View.VISIBLE);
                today_layout.setAnimation(animation_fadeIn);
            }, 400);

        });

        this_week.setOnClickListener(v -> {
            this_week.setTextColor(getResources().getColor(R.color.selected_text));
            today.setTextColor(getResources().getColor(R.color.hint_text_color));
            today_layout.setVisibility(View.GONE);
            today_layout.setAnimation(animation_fadeOut);
            new Handler().postDelayed(() -> {
                this_week_layout.setVisibility(View.VISIBLE);
                this_week_layout.setAnimation(animation_fadeIn);
            }, 400);

        });

        position_finder_button.setOnClickListener(v -> {
            getWeatherForCurrentLocation();
            today.setEnabled(true);
            this_week.setEnabled(true);
            today.setTextColor(getResources().getColor(R.color.selected_text));
            this_week.setTextColor(getResources().getColor(R.color.hint_text_color));
            this_week_layout.setVisibility(View.GONE);
            this_week_layout.setAnimation(animation_fadeOut);
            new Handler().postDelayed(() -> {
                today_layout.setVisibility(View.VISIBLE);
                today_layout.setAnimation(animation_fadeIn);
            }, 400);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(search_bar.getText().toString().isEmpty())
            getWeatherForCurrentLocation();
        else
            getWeatherForNewLocation();
    }

    private void getWeatherForNewLocation() {
        String city = search_bar.getText().toString().trim();

        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",API_KEY);
        params.put("units","metric");
        CallApi(params);

        //hide details from previous result
        //today_layout.setVisibility(View.GONE);
        //this_week_layout.setVisibility(View.GONE);

        //Todo : Daily & Hourly forecast for a new location

        Ion.with(this)
            .load(WEATHER_URL+"?q="+city+"&&units=metric&appid="+API_KEY)
            .asJsonObject()
            .setCallback((e, result) -> {
                    if(e != null){
                        e.printStackTrace();
                        Toast.makeText(MainWeather.this, "Unable to fetch data from server", Toast.LENGTH_SHORT).show();
                    }else{
                        weather = Weather.fromJson(result);
                        assert weather != null;
                        updateUI(weather);
                        colorSetup(Integer.parseInt(weather.getTemperature()));

                        double lat = result.get("coord").getAsJsonObject().get("lat").getAsDouble();
                        double lon = result.get("coord").getAsJsonObject().get("lon").getAsDouble();

                        getDailyHourlyForecast(lat,lon);
                        Toast.makeText(MainWeather.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();
                    }
        });

    }
    @SuppressLint("SetTextI18n")
    private void getDailyHourlyForecast(double lat, double lon){
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=minutely,current,alerts&&units=metric&appid=" + API_KEY;
        Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback((e, result) -> {

                    if(e != null){
                        e.printStackTrace();
                        Toast.makeText(MainWeather.this, "Unable to fetch Hourly Forecast from server", Toast.LENGTH_SHORT).show();
                    }else{
                        String timeZone = result.get("timezone").getAsString();
                        // Parse Json for Today Layout
                        DateFormat df = new SimpleDateFormat("HH", Locale.ENGLISH);
                        df.setTimeZone(TimeZone.getTimeZone(timeZone));

                        // Parse Json for Today Layout
                        double hr_temp;
                        String hr_icon;
                        JsonArray hourly = result.get("hourly").getAsJsonArray();
                        for(int i=0;i<hourly.size();i++){

                            long hr_date = hourly.get(i).getAsJsonObject().get("dt").getAsLong()*1000;
                            double hr = Double.parseDouble(df. format(hr_date));

                            Log.d("time",hr+"");
                            if( hr == 8 ){
                                hr_temp = hourly.get(i).getAsJsonObject().get("temp").getAsDouble();
                                hr_icon = hourly.get(i).getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                                morning_degree.setText((int) Math.rint(hr_temp) +"°C");
                                Ion.with(this).load("http://openweathermap.org/img/wn/"+hr_icon+".png").intoImageView(morning_icon);
                            } else if( hr == 13 ){
                                hr_temp = hourly.get(i).getAsJsonObject().get("temp").getAsDouble();
                                hr_icon = hourly.get(i).getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                                afternoon_degree.setText((int) Math.rint(hr_temp) +"°C");
                                Ion.with(this).load("http://openweathermap.org/img/wn/"+hr_icon+".png").intoImageView(afternoon_icon);
                            }else if( hr == 22 ){
                                hr_temp = hourly.get(i).getAsJsonObject().get("temp").getAsDouble();
                                hr_icon = hourly.get(i).getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                                evening_degree.setText((int) Math.rint(hr_temp) +"°C");
                                Ion.with(this).load("http://openweathermap.org/img/wn/"+hr_icon+".png").intoImageView(evening_icon);
                            }
                        }
                        // Parse Json for This week Layout
                        List<DailyWeather> weatherList = new ArrayList<>();
                        JsonArray daily = result.get("daily").getAsJsonArray();
                        for (int i =0;i < daily.size();i++) {
                            long dl_date = daily.get(i).getAsJsonObject().get("dt").getAsLong();
                            double dl_temp = daily.get(i).getAsJsonObject().get("temp").getAsJsonObject().get("day").getAsDouble();
                            String dl_icon = daily.get(i).getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                            weatherList.add(new DailyWeather(dl_temp, dl_icon,timeZone,dl_date));

                            //attach adapter to listview
                            DailyForecastAdapter dfa = new DailyForecastAdapter(getApplicationContext(),weatherList);
                            this_week_list_view.setAdapter(dfa);

                        }
                        Log.d("result",result.toString());
                    }
                });
    }
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat",Latitude);
                params.put("lon",Longitude);
                params.put("appid",API_KEY);
                params.put("units","metric");
                CallApi(params);
                getDailyHourlyForecast(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(MainWeather.this, "Unable to get location!", Toast.LENGTH_SHORT).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }

        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    private void CallApi(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                weather = Weather.fromJson(response);
                assert weather != null;
                updateUI(weather);
                colorSetup(Integer.parseInt(weather.getTemperature()));
                Toast.makeText(MainWeather.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainWeather.this, "Unable to fetch data from server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "FBI: we are heading to your address", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }else{
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(Weather w){

        current_temperature.setText(w.getTemperature());
        current_weather_type.setText(w.getWeatherType());
        current_city.setText(w.getCityName());
        current_country.setText(w.getCountryName());
        //current_weather_icon.setImageResource(getResources().getIdentifier(w.getIcon(),"drawable",getPackageName()));
        Ion.with(current_weather_icon).load("http://openweathermap.org/img/wn/"+w.getIcon()+".png");
        Log.d("icon",w.getIcon());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    //Todo : Activity main color change with weather degree
    public void colorSetup(int temp){
        if(temp<20)
            Utils.blackIconStatusBar(MainWeather.this,R.color.mostly_clear_Color);
    }
}

