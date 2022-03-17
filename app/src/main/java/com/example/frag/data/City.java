package com.example.frag.data;

import com.example.frag.view.SlideView;

public class City {
    private int id;
    private int parentId=1;
    private String enName="";
    private String initialName="";
    private String name;
    private int level=0;
    private String weather_id = "";
    private int isFav = 0;


    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", enName='" + enName + '\'' +
                ", initialName='" + initialName + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", weather_id='" + weather_id + '\'' +
                ", isFav=" + isFav +
                '}';
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public City(int id, String name, int parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getInitialName() {
        return initialName;
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }
}
