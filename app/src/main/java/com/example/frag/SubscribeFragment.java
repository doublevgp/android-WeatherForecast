package com.example.frag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.frag.data.City;
import com.example.frag.data.FavCity;
import com.example.frag.data.WeatherNow;
import com.example.frag.db.CityDatabase;
import com.example.frag.utility.WeatherApiUtil;
import com.example.frag.view.CityAdapter;
import com.example.frag.view.FavCityAdapter;
import com.example.frag.view.ListViewCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//TODO 取消选中 左滑删除
public class SubscribeFragment extends Fragment {
    ArrayList<FavCity> list = new ArrayList<FavCity>();
    ArrayList<FavCity> mlist = new ArrayList<FavCity>();
    AtomicInteger count = new AtomicInteger(0);
    private SwipeMenuListView lv;
    CityDatabase cityDatabase;
    FavCityAdapter adapter;

    public void setOnListViewItemClick(OnListViewItemClick onListViewItemClick) {
        this.onListViewItemClick = onListViewItemClick;
    }

    private OnListViewItemClick onListViewItemClick;
    private static final String KEY_WEATHER_ID="weather_id";


    public static String getWeatherIdByIntent(Intent intent) {
        String weather_id = intent.getStringExtra(KEY_WEATHER_ID);
        return weather_id;
    }

    public interface OnListViewItemClick {
        public void onClickCall(String weather_id);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscribe_fragment, container, false);
        cityDatabase = new CityDatabase(getActivity());
        cityDatabase.open();
        lv =  view.findViewById(R.id.swip_list_view);
        List<City> lst = cityDatabase.QueryCityFav();
        System.out.println("City list size == " + lst.size());
        count = new AtomicInteger(0);
        for (City city : lst) {
            WeatherApiUtil.getWeatherNow(getActivity(), city.getWeather_id(), new WeatherApiUtil.OnWeatherNowFinished() {
                @Override
                public void onFinished(WeatherNow data) {
                    if (data != null) {
                        FavCity favCity = new FavCity(city.getId(), city.getName(), city.getIsFav(), city.getWeather_id());
                        favCity.cond_code = (data.now.cond_code != null ? data.now.cond_code : null);
                        favCity.cond_txt = (data.now.cond_txt != null ? data.now.cond_txt : null);
                        favCity.tmp = (data.now.tmp != null ? data.now.tmp : null);
                        list.add(favCity);
                        System.out.println("FavCity list size == " + list.size());
                    }
                    if (count.incrementAndGet() == lst.size()) {
                        System.out.println("load done");
                        mlist = new ArrayList<FavCity>();
                        for (int i = 0; i < list.size(); i++) {
                            mlist.add(list.get(i));
                        }
                        System.out.println(mlist.size());
                        for (int i = 0; i < mlist.size(); i++) {
                            System.out.println(mlist.get(i).toString());
                        }
                        adapter = new FavCityAdapter(getContext(), mlist);
                        lv.setAdapter(adapter);
                        lv.setMenuCreator(menuCreator);
                        lv.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
                        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                Toast.makeText(getContext(), "已取消对" + ((FavCity) adapter.getItem(position)).getName() + "的关注", Toast.LENGTH_SHORT).show();
                                FavCity city1 = (FavCity) adapter.getItem(position);
                                city1.setIsFav(0);
                                cityDatabase.updateCityStatus(city1.getId());
                                mlist.remove(position);
                                adapter.notifyDataSetChanged();
                                lv.setAdapter(adapter);
//                                lv.notifyAll();
//                                lv.deferNotifyDataSetChanged();
//                                lv.invalidate();
                                return true;
                            }
                        });
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                FavCity city = (FavCity) adapter.getItem(position);
                                System.out.println(city.getName());
                                MainActivity.gloableWeatherId.weatherId = city.getWeather_id();
                                if (onListViewItemClick != null) {
                                    onListViewItemClick.onClickCall(city.getWeather_id());
                                }
                            }
                        });
                    }
                }
            });
        }
        return view;
    }
    private SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem removeItem = new SwipeMenuItem(getActivity());
            removeItem.setBackground(new ColorDrawable(Color.parseColor("#D85757")));
            removeItem.setTitle("取消关注");
            removeItem.setTitleSize(20);
            removeItem.setTitleColor(Color.WHITE);
            removeItem.setWidth(300);

            menu.addMenuItem(removeItem);
            //...
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
