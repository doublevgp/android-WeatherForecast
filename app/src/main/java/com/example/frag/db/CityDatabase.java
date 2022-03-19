package com.example.frag.db;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.frag.data.City;
import com.example.frag.data.FavCity;
import com.example.frag.utility.WeatherApiUtil;

import java.util.ArrayList;
import java.util.List;
//Todo 添加喜欢的城市数据表 点击我的关注之后获取目前喜欢的城市List
public class CityDatabase {
    public static final String KEY_ID = "mid";
    public static final String KEY_PID = "pid";
    public static final String KEY_NAME = "name";
    public static final String KEY_WEATHER_ID = "weather_id";
    public static final String KEY_EN_NAME = "en_name";
    public static final String KEY_INI_NAME = "ini_name";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_LOOK_UP = "key_look_up";
    private static final String DB_NAME = "citydb.db";
    public static final String KEY_FAV = "isfav";
    public static final String CITY_TABLE = "city";
    public static final String FAV_CITY_TABLE = "fav_city";

    private int version = 1;
    Activity activity;
    private SQLiteDatabase db;
    DatabaseHelper databaseHelper;
    public CityDatabase(Activity activity){
        this.activity = activity;
    }
    public void open() {
        if (db == null || !db.isOpen()) {
            databaseHelper = new DatabaseHelper();
            db = databaseHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private ContentValues enCodeCotentValues(City city) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_EN_NAME, city.getEnName());
        cv.put(KEY_ID, city.getId());
        cv.put(KEY_INI_NAME, city.getInitialName());
        cv.put(KEY_LEVEL, city.getLevel());
        cv.put(KEY_LOOK_UP, generateLookup(city));
        cv.put(KEY_PID, city.getParentId());
        cv.put(KEY_WEATHER_ID, city.getWeather_id());
        cv.put(KEY_NAME, city.getName());
        cv.put(KEY_FAV, city.getIsFav());
        return cv;
    }
    public City queryCityById(int id, int level) {
        String sql = String.format("select * from %s where %s=%d and %s=%d", CITY_TABLE, KEY_ID, id, KEY_LEVEL, level);
        List<City> list = getCityListBySql(sql, null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    private String generateLookup(City city) {
        String name = city.getName();
        String enName = city.getEnName();
        String initialName = city.getInitialName();
        String[] enNameArray = enName.split("\\s");
        StringBuilder sb = new StringBuilder();
        sb.append(name + " ");
        sb.append(enName + " ");
        sb.append(initialName + " ");
        sb.append(enName.replaceAll("\\s", "") + " ");
        for (int i = 1; i < enNameArray.length; i++) {
            sb.append(initialName.substring(0, i));
            for (int j = i; j < enNameArray.length; j++) {
                sb.append(enNameArray[j]);
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public List<City> fuzzyQueryCityList(String match) {
        if (TextUtils.isEmpty(match)) {
            return queryAllProvinces();
        }
        String sql = String.format("select * from %s where %s like ?", CITY_TABLE, KEY_LOOK_UP);
        String[] args = new String[]{"%" + match + "%"};
        return getCityListBySql(sql, args);
    }
    public void UpdateFavTable() {
        List<City> list = QueryCityFav();
        // 简单粗暴的方法 直接删表重建
        databaseHelper.createFavTable(db);
        for (City city : list) {
            insertDataIntoFavTable(city);
        }
        // todo 数据量大的时候可能要用其他方法
    }
    public long insertDataIntoFavTable(City city) {
        ContentValues cv = enCodeCotentValues(city);
        return db.insert(FAV_CITY_TABLE, KEY_WEATHER_ID, cv);
    }
    public List<City> QueryCityFav() {
        String sql = String.format("select * from %s where %s=1", CITY_TABLE, KEY_FAV);
        return getCityListBySql(sql, null);
    }
    private City getCityFromCursor(Cursor c) {
        @SuppressLint("Range") String name = c.getString(c.getColumnIndex(KEY_NAME));
        @SuppressLint("Range") String enName = c.getString(c.getColumnIndex(KEY_EN_NAME));
        @SuppressLint("Range") String iniName = c.getString(c.getColumnIndex(KEY_INI_NAME));
        @SuppressLint("Range") String weather_id = c.getString(c.getColumnIndex(KEY_WEATHER_ID));
        @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(KEY_ID));
        @SuppressLint("Range") int pid = c.getInt(c.getColumnIndex(KEY_PID));
        @SuppressLint("Range") int level = c.getInt(c.getColumnIndex(KEY_LEVEL));
        @SuppressLint("Range") int isFav = c.getInt(c.getColumnIndex(KEY_FAV));
        City city = new City(id, name, pid);
        city.setEnName(enName);
        city.setInitialName(iniName);
        city.setLevel(level);
        city.setWeather_id(weather_id);
        city.setIsFav(isFav);
        return city;
    }

    public long insertData(City city) {
        ContentValues cv = enCodeCotentValues(city);
        return db.insert(CITY_TABLE, KEY_WEATHER_ID, cv);
    }

    public int insertList(List<City> list) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            City city = list.get(i);
            System.out.println("hhhhhh " + city.getIsFav());
            if (insertData(city) > 0) {
                count += 1;
            }
        }
        return count;
    }

    public void clearDatabase() {
        if (db != null && db.isOpen()) {
            databaseHelper.resetData(db);
        }
    }

    public List<City> getCityListFromCursor(Cursor c) {
        List<City> list = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            City city = getCityFromCursor(c);
            list.add(city);
        }
        return list;
    }
    public City getCityByWeatherId(String weatherId) {
        String sql = String.format("select * from %s where %s='%s'", CITY_TABLE, KEY_WEATHER_ID, weatherId);
        City city = getCityListBySql(sql, null).get(0);
        return city;
    }
    public List<City> queryAllProvinces() {
        String sql = String.format("select * from %s where %s=0", CITY_TABLE, KEY_LEVEL);
        return getCityListBySql(sql, null);
    }

    private List<City> getCityListBySql(String sql, String[] args) {
        Cursor c = db.rawQuery(sql, args);
        List<City> list = getCityListFromCursor(c);
        c.close();
        return list;
    }

    public List<City> queryCityListByParentId(int parentId, int level) {
        if (level == 0) {
            return queryAllProvinces();
        }
        String sql = String.format("select * from %s where %s=%d and %s=%d", CITY_TABLE, KEY_PID, parentId, KEY_LEVEL, level);
        return getCityListBySql(sql, null);
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper() {
            super(activity, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table if not exists %s " +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT,%s int,%s int,%s text,%s text,%s text,%s int,%s text,%s text,%s int)",
                    CITY_TABLE, KEY_ID, KEY_PID, KEY_NAME, KEY_EN_NAME, KEY_INI_NAME, KEY_LEVEL, KEY_LOOK_UP, KEY_WEATHER_ID, KEY_FAV);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            resetData(db);
        }
        public void resetData(SQLiteDatabase db) {
            String sql = String.format("drop table if exists %s", CITY_TABLE);
            db.execSQL(sql);
            sql = String.format("drop table if exists %s", FAV_CITY_TABLE);
            db.execSQL(sql);
            onCreate(db);
        }

        public void delFavTable(SQLiteDatabase db) {
            String sql = String.format("drop table if exists %s", FAV_CITY_TABLE);
            db.execSQL(sql);
        }

        public void createFavTable(SQLiteDatabase db) {
            delFavTable(db);
            String sql = String.format("create table if not exists %s " +
                            "(_id INTEGER PRIMARY KEY AUTOINCREMENT,%s int,%s int,%s text,%s text,%s text,%s int,%s text,%s text)",
                    FAV_CITY_TABLE, KEY_ID, KEY_PID, KEY_NAME, KEY_EN_NAME, KEY_INI_NAME, KEY_LEVEL, KEY_LOOK_UP, KEY_WEATHER_ID);
            db.execSQL(sql);
        }
    }
    public void updateCityStatus(City city) {
        db.update(CITY_TABLE, enCodeCotentValues(city), String.format("%s='%s' and %s=2", KEY_NAME, city.getName(), KEY_LEVEL), null);
    }
    public void updateCityStatus(int id) {
        City city = queryCityById(id, 2);
        city.setIsFav(0);
        db.update(CITY_TABLE, enCodeCotentValues(city), String.format("%s='%s' and %s=2", KEY_NAME, city.getName(), KEY_LEVEL), null);
    }
    public interface OnQueryFinished {
        public void onFinished(List<City> list);
    }
    interface LoaderWork {
        List<City> queryWork();
    }
    private void asyncLoader(final OnQueryFinished listener, final LoaderWork work) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<City> list = work.queryWork();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinished(list);
                    }
                });
            }
        }).start();
    }

    public void queryAllProvincesAsync(final OnQueryFinished listener) {
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return queryAllProvinces();
            }
        });
    }

    public void queryCityListByParentIdAsync(final int parentId, final int level, final OnQueryFinished listener) {
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return queryCityListByParentId(parentId, level);
            }
        });
    }

    public void fuzzyQueryCityListAsync(final String match, final OnQueryFinished listener) {
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return fuzzyQueryCityList(match);
            }
        });
    }
}
