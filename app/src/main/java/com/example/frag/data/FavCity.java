package com.example.frag.data;

import com.example.frag.view.SlideView;

public class FavCity {
    private int id;
    public String name;
    private int isFav = 0;
    public String cond_code;
    public String cond_txt;
    public String tmp;
    public SlideView slideView;

    public String getWeather_id() {
        return weather_id;
    }

    private String weather_id = "";

    public FavCity(int id, String name, int isFav, String weather_id) {
        this.id = id;
        this.name = name;
        this.isFav = isFav;
        this.weather_id = weather_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public String getCond_code() {
        return cond_code;
    }

    public void setCond_code(String cond_code) {
        this.cond_code = cond_code;
    }

    public String getCond_txt() {
        return cond_txt;
    }

    public void setCond_txt(String cond_txt) {
        this.cond_txt = cond_txt;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public SlideView getSlideView() {
        return slideView;
    }

    public void setSlideView(SlideView slideView) {
        this.slideView = slideView;
    }
}
