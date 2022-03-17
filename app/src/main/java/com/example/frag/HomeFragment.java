package com.example.frag;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.frag.data.AirQualityData;
import com.example.frag.data.DailyForecast;
import com.example.frag.data.WeatherForecast;
import com.example.frag.data.WeatherNow;
import com.example.frag.utility.DataBaseUtil;
import com.example.frag.utility.WeatherApiUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {
    TextView tv_city, tv_update_time, tv_temp, tv_weather_info;
    ImageView iv_cond;
    String default_weather_id = "CN101210701";
    public String weather_id;
    LinearLayout forecastLayout;
    TextView tv_aqi, tv_pm25;
    SwipeRefreshLayout swipeRefreshLayout;
    AtomicInteger requestCount = new AtomicInteger(0);
    public static final int CITY_REQ_CODE=0;
    private static final String KEY_WEATHER_ID="weather_id";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        tv_city = v.findViewById(R.id.title_city_tv);
        tv_update_time = v.findViewById(R.id.title_pub_time_tv);
        tv_temp = v.findViewById(R.id.now_temp_tv);
        tv_weather_info = v.findViewById(R.id.now_cond_tv);
        iv_cond = v.findViewById(R.id.now_cond_iv);
        forecastLayout = v.findViewById(R.id.forecast_layout);
        tv_aqi = v.findViewById(R.id.aqi_text);
        tv_pm25 = v.findViewById(R.id.pm25_text);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
//        weather_id = MainActivity.gloableWeatherId.weatherId;
        if (getArguments() != null) {
            getTransitData();
        } else {
            weather_id = default_weather_id;
        }
        return v;
    }

    private void getTransitData() {
        if (getArguments().getString(KEY_WEATHER_ID) != null) {
            weather_id = getArguments().getString(KEY_WEATHER_ID);
        } else {
            weather_id = default_weather_id;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(getContext(), com.example.frag.SelectCityActivity.class);
//                startActivityForResult(i,CITY_REQ_CODE);
            }
        });
        loadWeatherId();
        updateData();
    }
    private void loadWeatherId() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        weather_id=sharedPreferences.getString(KEY_WEATHER_ID,weather_id);
    }
    private void saveWeatherId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WEATHER_ID,weather_id);
        editor.apply();// async save;
        // editor.commit();// block save;
    }

    private void updateData() {
        requestCount.set(0);
        swipeRefreshLayout.setRefreshing(true);
        updateWeatherNow();
        updateWeatherForecast();
        updateWeatherAqi();
    }

    private void updateWeatherNow() {
        WeatherApiUtil.getWeatherNow(getActivity(), weather_id, new WeatherApiUtil.OnWeatherNowFinished() {
            @Override
            public void onFinished(WeatherNow data) {
                if (data != null) {
                    updateUiInfo(data);
                }
                updateRefreshState();
            }
        });
    }

    private void updateRefreshState() {
        if (requestCount.incrementAndGet() == 3) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateWeatherAqi() {
        WeatherApiUtil.getAirQualityData(getActivity(), weather_id, new WeatherApiUtil.OnAirQualityFinished() {
            @Override
            public void onFinished(AirQualityData data) {
                if (data != null && data.status.equalsIgnoreCase("ok")) {
                    tv_aqi.setText(data.airNowCity.aqi);
                    tv_pm25.setText(data.airNowCity.pm25);
                } else {
                    tv_aqi.setText("--");
                    tv_pm25.setText("--");
                }
                updateRefreshState();
            }
        });
    }

    private void updateUiInfo(WeatherNow data) {
        tv_city.setText(data.basic.location);
        tv_update_time.setText(data.update.loc);
        tv_temp.setText(data.now.tmp + " ℃");
        tv_weather_info.setText(data.now.cond_txt);
        updateWeatherIcon(data.now.cond_code, iv_cond);
    }

    private void updateWeatherIcon(String cond_code, ImageView iv_cond) {
        String url = String.format("https://cdn.heweather.com/cond_icon/%s.png", cond_code);
        System.out.println("cond_code == " + url);
        Glide.with(getContext()).load(Uri.parse(url)).into(iv_cond);
    }

    private void updateWeatherForecast() {
        WeatherApiUtil.getWeatherForecast(getActivity(), weather_id, new WeatherApiUtil.OnWeatherForecastFinished() {
            @Override
            public void onFinished(WeatherForecast data) {
                forecastLayout.removeAllViews();
                if (data != null) {
                    List<DailyForecast> forecastList = data.dailyForecastList;
                    for (int i = 0; i < forecastList.size(); i++) {
                        DailyForecast f = forecastList.get(i);
                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item, null, false);
                        TextView item_date_text = v.findViewById(R.id.item_date_text);
                        TextView item_max_text = v.findViewById(R.id.item_max_text);
                        TextView item_min_text = v.findViewById(R.id.item_min_text);
                        ImageView item_iv_day_con = v.findViewById(R.id.item_iv_day_con);
                        ImageView item_iv_night_con = v.findViewById(R.id.item_iv_night_con);
                        item_date_text.setText(f.date);
                        item_max_text.setText(f.tmp_max + "℃");
                        item_min_text.setText(f.tmp_min + "℃");
                        updateWeatherIcon(f.cond_code_d, item_iv_day_con);
                        updateWeatherIcon(f.cond_code_n, item_iv_night_con);
                        forecastLayout.addView(v);
                    }
                }
                updateRefreshState();
            }
        });
    }
}
