package com.example.frag.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.frag.R;
import com.example.frag.data.City;
import com.example.frag.data.FavCity;
import com.example.frag.data.WeatherNow;
import com.example.frag.utility.WeatherApiUtil;

import java.util.ArrayList;
import java.util.List;

public class FavCityAdapter extends BaseAdapter implements SlideView.OnSlideListener {
    private ArrayList<FavCity> list;
    private Context context;
    private LayoutInflater layoutInflater;

    private SlideView LastSlideViewWithStatusOn;
    private com.example.frag.view.CityAdapter.FavIvClick favIvClick;
    public FavCityAdapter(@NonNull Context context, ArrayList<FavCity> list) {
        this.list = list;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list == null) {
            list = new ArrayList<FavCity>();
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemHolder itemHolder;
        View v = layoutInflater.inflate(R.layout.fav_row_view, null);
        SlideView slideView = new SlideView(context);
        slideView.setContentView(v);
        itemHolder = new ItemHolder(slideView);
        slideView.setOnSlideListener(this);
        slideView.setTag(itemHolder);

        FavCity city = list.get(position);
        city.slideView = slideView;
        city.slideView.shrink();
        if (city.cond_code != null) {
            String url = String.format("https://cdn.heweather.com/cond_icon/%s.png", city.cond_code);
            Glide.with(itemHolder.iv_cond).load(Uri.parse(url)).into(itemHolder.iv_cond);
        } else {
            itemHolder.iv_cond.setVisibility(View.INVISIBLE);
        }
        if (city.cond_txt != null) {
            itemHolder.cond_text.setText(city.cond_txt);
        } else {
            itemHolder.cond_text.setText("NaN");
        }
        if (city.tmp != null) {
            int tmp = Integer.parseInt(city.tmp);
            if (tmp >= 30) {
                itemHolder.tmp.setTextColor(Color.parseColor("#DA4141"));
            } else if (tmp >= 20) {
                itemHolder.tmp.setTextColor(Color.parseColor("#FF9800"));
            } else if (tmp >= 10) {
                itemHolder.tmp.setTextColor(Color.parseColor("#00BCD4"));
            } else {
                itemHolder.tmp.setTextColor(Color.parseColor("#99CEF8"));
            }
            itemHolder.tmp.setText(city.tmp+" ℃");
        } else {
            itemHolder.tmp.setText("NaN");
        }
        itemHolder.cityName.setText(city.getName());
        itemHolder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        return slideView;
    }

    @Override
    public void onSlide(View view, int status) {
        if (LastSlideViewWithStatusOn != null&& LastSlideViewWithStatusOn != view) {
            LastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            LastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    private class ItemHolder {
        public TextView cityName;
        public TextView tmp;
        public TextView cond_text;
        public ViewGroup deleteHolder;
        public ImageView iv_cond;
        ItemHolder(View view) {
            iv_cond = view.findViewById(R.id.fav_row_view_cond_iv);
            tmp = view.findViewById(R.id.fav_row_view_tmp);
            cond_text = view.findViewById(R.id.fav_row_view_cond_txt);
            cityName = (TextView) view.findViewById(R.id.fav_row_view_cityName);
            deleteHolder = (ViewGroup) view.findViewById(R.id.delete_merge_holder);
        }
    }
}
