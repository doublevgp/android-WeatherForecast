package com.example.frag.utility;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.frag.data.AirQualityData;
import com.example.frag.data.Now;
import com.example.frag.data.WeatherForecast;
import com.example.frag.data.WeatherNow;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApiUtil {
    // 普通用户每日访问有上限次数
//    public static final String API_KEY = "72c78773924e4140854a6375169d9692";
    public static final String API_KEY = "b41a22f844ea4423903ff78ce0b8b6e5";
//    public static final String API_KEY = "a2fef0f40fc24cc3b277183d555fd7ea";
    public interface OnMyCall {
        public void onFailed(String e);
        public void onResponse(WeatherNow data);
    }
    public interface OnWeatherNowFinished {
        public void onFinished(WeatherNow data);
    }
    public static void getWeatherNowTb(final Activity activity, String weather_id, final OnMyCall l) {
        String url = String.format("https://free-api.heweather.net/s6/weather/now?location=%s&key=%s&lang=en", weather_id, API_KEY);
        HttpUtil.getOkHttp(activity, url, new HttpUtil.mCall() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s = heWeather6.get(0).toString();
                        WeatherNow data = new Gson().fromJson(s, WeatherNow.class);
                        l.onResponse(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        listener.onFinished(null);
                    }
                } else {
//                    listener.onFinished(null);
                }
            }
        });
    }
    public static void getWeatherNow(final Activity activity, String weather_id, final OnWeatherNowFinished listener) {
        String url = String.format("https://free-api.heweather.net/s6/weather/now?location=%s&key=%s&lang=en", weather_id, API_KEY);
        HttpUtil.getOkHttpAsync(activity, url, new HttpUtil.SimpleAsyncCall() {
            @Override
            public void onFailure(String e) {
                showToast(activity, e);
                listener.onFinished(null);
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s = heWeather6.get(0).toString();
                        WeatherNow data = new Gson().fromJson(s, WeatherNow.class);
                        listener.onFinished(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFinished(null);
                    }
                } else {
                    listener.onFinished(null);
                }
            }
        });
    }
    public interface OnWeatherForecastFinished{
        public void onFinished(WeatherForecast data);
    }
    public static void getWeatherForecast(final Activity activity, String weather_id, final OnWeatherForecastFinished listener){
        String url=String.format("https://free-api.heweather.net/s6/weather/forecast?location=%s&key=%s&lang=en",weather_id,API_KEY);
        HttpUtil.getOkHttpAsync(activity, url, new HttpUtil.SimpleAsyncCall() {
            @Override
            public void onFailure(String e) {
                showToast(activity,e);
                listener.onFinished(null);
            }
            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)){
                    try {
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s= heWeather6.get(0).toString();
                        WeatherForecast data = new Gson().fromJson(s, WeatherForecast.class);
                        if(data!=null&&data.status.equalsIgnoreCase("ok")){
                            listener.onFinished(data);
                            return; }
                    } catch (JSONException e) { e.printStackTrace(); }
                }
                listener.onFinished(null);
            }
        });
    }

    public interface OnAirQualityFinished{
        public void onFinished(AirQualityData data);
    }
    public static void getAirQualityData(final Activity activity, String weather_id, final OnAirQualityFinished listener){
        String url=String.format("https://free-api.heweather.net/s6/air/now?location=%s&key=%s&lang=en",weather_id,API_KEY);
        System.out.println("api url");
        System.out.println(url);
        HttpUtil.getOkHttpAsync(activity, url, new HttpUtil.SimpleAsyncCall() {
            @Override
            public void onFailure(String e) {
                showToast(activity,e);
                listener.onFinished(null);
            }
            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)) {
                    try {
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s= heWeather6.get(0).toString();
                        AirQualityData data = new Gson().fromJson(s, AirQualityData.class);
                        if(data!=null&&data.status.equalsIgnoreCase("ok")){
                            listener.onFinished(data);
                            return; }
                    } catch (JSONException e) {e.printStackTrace();}
                }
                listener.onFinished(null);
            }
        });
    }

    private static void showToast(Activity activity, String s) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }
}
