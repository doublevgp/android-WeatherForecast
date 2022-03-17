package com.example.frag.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frag.R;
import com.example.frag.data.City;

import java.util.List;

public class CityAdapter extends ArrayAdapter<City> implements View.OnClickListener {
    private List<City> list;
    private Context context;
    private FavIvClick favIvClick;
    public CityAdapter(@NonNull Context context, List<City> list, FavIvClick favIvClick) {
        super(context, android.R.layout.simple_list_item_1, list);
        this.list = list;
        this.context = context;
        this.favIvClick = favIvClick;
    }

    @Override
    public void onClick(View v) {
        favIvClick.onFavIvClick(v);
    }

    public interface FavIvClick {
        public void onFavIvClick(View v);
    }
    public void setOnclick(FavIvClick favIvClick) {
        this.favIvClick = favIvClick;
    }
    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.city_row_view, null, false);
        } else {
            v = convertView;
        }
        TextView tv = v.findViewById(R.id.city_row_view_tv);
        TextView tv0 = v.findViewById(R.id.city_row_view_province_tv);
        TextView tv1 = v.findViewById(R.id.city_row_view_city_tv);
        TextView tv2 = v.findViewById(R.id.city_row_view_country_tv);
        ImageView fav_iv = v.findViewById(R.id.city_row_view_fav_iv);
        City city =list.get(position);
        final int isFav = city.getIsFav();
        tv.setText(city.getName());
        v.setBackgroundColor(R.color.colorWhite);
        fav_iv.setOnClickListener(this);
        fav_iv.setTag(position);
        switch (city.getLevel()) {
            case 0:
                tv0.setVisibility(View.VISIBLE);
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                fav_iv.setVisibility(View.INVISIBLE);
                break;
            case 1:
                tv0.setVisibility(View.GONE);
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                fav_iv.setVisibility(View.INVISIBLE);
                break;
            case 2:
                tv0.setVisibility(View.GONE);
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.VISIBLE);
                fav_iv.setVisibility(View.VISIBLE);
                if (isFav == 1) {
                    fav_iv.setImageResource(R.drawable.ic_baseline_is_favorite_24);
                } else {
                    fav_iv.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                break;
        }
        return v;
    }
}
