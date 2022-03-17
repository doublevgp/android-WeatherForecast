package com.example.frag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.frag.data.City;
import com.example.frag.db.CityDatabase;
import com.example.frag.db.GenerateDatabaseTask;
import com.example.frag.utility.HttpUtil;
import com.example.frag.utility.JsonUtil;
import com.example.frag.view.CityAdapter;

import java.util.List;
// TODO ListView的背景颜色 可能和Fragment的背景颜色有关系
public class CityListFragment extends Fragment implements CityAdapter.FavIvClick {
    ListView lv;
    String baseUrl = "http://guolin.tech/api/china";
    int level_0_id;
    CityDatabase cityDatabase;
    CityAdapter adapter;
    androidx.appcompat.widget.Toolbar toolbar;

    private static final String KEY_WEATHER_ID="weather_id";
    public static String getWeatherIdByIntent(Intent intent) {
        String weather_id = intent.getStringExtra(KEY_WEATHER_ID);
        return weather_id;
    }

    private void back() {
        if (adapter.getCount() > 0) {
            City city = adapter.getItem(0);
            int level = city.getLevel();
            if (level == 2) {
                City city1 = cityDatabase.queryCityById(city.getParentId(), 1);
                level_0_id = city1.getParentId();
                String url = String.format("%s/%d", baseUrl, level_0_id);
                getAndUpdateCityList(url, level_0_id, level - 1);
            }
            if (level == 1) {
                getAndUpdateCityList(baseUrl, -1, level - 1);
            }
        }
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opt_back:
                back();
                break;
            case R.id.opt_generate_db:
                new GenerateDatabaseTask(getActivity(),cityDatabase).execute();
                break; }
        return super.onOptionsItemSelected(item);
    }
    private void getAndUpdateCityList(final String url, final int parentId, final int level) {
        cityDatabase.queryCityListByParentIdAsync(parentId, level, new CityDatabase.OnQueryFinished() {
            @Override
            public void onFinished(List<City> list) {
                if (list == null || list.size() == 0) {
                    HttpUtil.getOkHttpAsync(getActivity(), url, new HttpUtil.SimpleAsyncCall() {
                        @Override
                        public void onFailure(String e) {
                            showToast(e);
                        }

                        @Override
                        public void onResponse(String s) {
                            List<City> list = JsonUtil.getCityListFromJson(s, parentId, level);
                            cityDatabase.insertList(list);
                            showDbList(parentId, level);
                        }
                    });
                } else {
                    updateListView(list);
                }
                if (level == 0) {
                    toolbar.setTitle("China");
                } else {
                    City city = cityDatabase.queryCityById(parentId, level - 1);
                    toolbar.setTitle(city.getName());
                }
            }
        });
    }

    private void showDbList(int parentId, int level) {
        cityDatabase.queryCityListByParentIdAsync(parentId, level, new CityDatabase.OnQueryFinished() {
            @Override
            public void onFinished(List<City> list) {
                updateListView(list);
            }
        });
    }

    private void showToast(String info) {
        Toast.makeText(getContext(), String.format("%s", info), Toast.LENGTH_SHORT).show();
    }

    private void updateListView(List<City> list) {
        adapter = new CityAdapter(getContext(), list, this::onFavIvClick);
        lv.setAdapter(adapter);
    }

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.citylist_fragment, container, false);
        lv = view.findViewById(R.id.listview);
        view.setBackgroundColor(R.color.white);
        setHasOptionsMenu(true);
        toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.toolbar);
        cityDatabase = new CityDatabase(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cityDatabase.open();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("China");
        getAndUpdateCityList(baseUrl, -1, 0);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = adapter.getItem(position);
                int level = city.getLevel();
                int cityId = city.getId();
                int parentId = city.getParentId();
                String url = "";
                switch (level) {
                    case 0:
                        level_0_id = cityId;
                        url = String.format("%s/%d", baseUrl, cityId);
                        getAndUpdateCityList(url, cityId, level + 1);
                        break;
                    case 1:
                        url = String.format("%s/%d/%d", baseUrl, parentId, cityId);
                        getAndUpdateCityList(url, cityId, level + 1);
                        break;
                    case 2:
                        Intent i = getActivity().getIntent();
                        i.putExtra(KEY_WEATHER_ID,city.getWeather_id());
                        getActivity().setResult(Activity.RESULT_OK,i);
                        getActivity().finish();
                        break;
                }

            }
        });

}

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.opt_menu, menu);
        MenuItem item = menu.findItem(R.id.opt_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cityDatabase.fuzzyQueryCityListAsync(newText, new CityDatabase.OnQueryFinished() {
                    @Override
                    public void onFinished(List<City> list) {
                        updateListView(list);
                        if (TextUtils.isEmpty(newText)) {
                            toolbar.setTitle("China");
                        }
                    }
                });
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cityDatabase.close();
    }

    @Override
    public void onFavIvClick(View v) {
        int pos = (Integer)v.getTag();
        City city = adapter.getItem(pos);
        System.out.println("I am here " + city.getName());
        city.setIsFav(city.getIsFav() == 1 ? 0 : 1);
        cityDatabase.updateCityStatus(city);
        ImageView iv = v.findViewById(R.id.city_row_view_fav_iv);
        if (city.getIsFav() == 1) {
            iv.setImageResource(R.drawable.ic_baseline_is_favorite_24);
        } else {
            iv.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    }
}
