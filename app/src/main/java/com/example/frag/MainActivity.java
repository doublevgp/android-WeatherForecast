package com.example.frag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.frag.data.GloableWeatherId;
import com.example.frag.utility.DataBaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

//todo 片段跳转每次都是new出来的 保存片段历史
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
//    public static String KEY_HOMEFRAGMENT = "homeFragment";
    public static String KEY_CITYLISTFRAGMENT = "cityListFragment";
    public static String KEY_SUBSCRIBEFRAGMENT = "subscribeFragment";
    HomeFragment homeFragment;
    CityListFragment cityListFragment;
    SubscribeFragment subscribeFragment;
    SubscribeFragment.OnListViewItemClick onListViewItemClick;
    public static GloableWeatherId gloableWeatherId = new GloableWeatherId("CN101210701");
    public List<Fragment> fragmentList = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNav);
        DataBaseUtil dataBaseUtil = new DataBaseUtil(this);
        if (dataBaseUtil.isDbExists() == false) {
            try {
                dataBaseUtil.copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (savedInstanceState == null) {
            showHomeFragment();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // menuitem 数量增多之后会出现 item title不显示 点击后才显示
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        showHomeFragment();
                        break;
                    case R.id.navigation_city_list:
                        showCityListFragment();
                        break;
                    case R.id.navigation_subscribe:
                        showSubscribeFragment();
                        break;
                    case R.id.navigation_map:
                        //跳转到地图模块
//                        bottomNavigationView.setSelectedItemId(R.id.navigation_city_list); //这个方法是点击导航之后 显示的内容与setselected的item一致
                        showHomeFragment();
//                        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        homeFragment = null;
                        showHomeFragment();
                        break;
                    case R.id.navigation_city_list:
                        cityListFragment = null;
                        showCityListFragment();
                        break;
                    case R.id.navigation_subscribe:
//                        subscribeFragment = null;
                        showSubscribeFragment();
                        break;
                    case R.id.navigation_map:
                        //跳转到地图模块
//                        bottomNavigationView.setSelectedItemId(R.id.navigation_city_list); //这个方法是点击导航之后 显示的内容与setselected的item一致
//                        showHomeFragment();
                        break;
                }
            }
        });

    }

    public void setAllItemCheckStatus() {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
    }

    public void showHomeFragment() {
        setAllItemCheckStatus();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("weather_id", gloableWeatherId.weatherId);
            homeFragment.setArguments(bundle);
            fragmentList.add(homeFragment);
            transaction.add(R.id.fragmentContainer, homeFragment);
        }
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        hideAllFragments(transaction);
        transaction.show(homeFragment);
        transaction.commit();
    }

    public void showHomeFragmentFromSub() {
        setAllItemCheckStatus();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentList.contains(homeFragment)) {
            fragmentList.remove(homeFragment);
        }
        homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weather_id", gloableWeatherId.weatherId);
        homeFragment.setArguments(bundle);
        fragmentList.add(homeFragment);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        transaction.add(R.id.fragmentContainer, homeFragment);
        hideAllFragments(transaction);
        transaction.show(homeFragment);
        transaction.commit();
    }

    private void showCityListFragment() {
        setAllItemCheckStatus();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (cityListFragment == null) {
            cityListFragment = new CityListFragment();
            fragmentList.add(cityListFragment);
            transaction.add(R.id.fragmentContainer, cityListFragment);
        }
        hideAllFragments(transaction);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        transaction.show(cityListFragment);
        transaction.commit();
    }

    private void showSubscribeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (subscribeFragment == null) {
            subscribeFragment = new SubscribeFragment();
            fragmentList.add(subscribeFragment);
            transaction.add(R.id.fragmentContainer, subscribeFragment);
        } else {
            fragmentList.remove(subscribeFragment);
            subscribeFragment = new SubscribeFragment();
            fragmentList.add(subscribeFragment);
            transaction.add(R.id.fragmentContainer, subscribeFragment);
        }
        hideAllFragments(transaction);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        transaction.show(subscribeFragment);
        subscribeFragment.setOnListViewItemClick(new SubscribeFragment.OnListViewItemClick() {
            @Override
            public void onClickCall(String weather_id) {
                gloableWeatherId.weatherId = weather_id;
                showHomeFragmentFromSub();
            }
        });
        transaction.commit();
    }

    private void hideAllFragments(FragmentTransaction transaction) {
        for (Fragment frag : fragmentList) {
            if (frag != null) {
                transaction.hide(frag);
            }
        }
    }

}