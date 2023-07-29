package com.stjosephscpllege.visionai_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class custom_location extends BaseAdapter {
    String[] name1, location1, sdate1;
    private Context context;

    public custom_location(Context applicationContext, String[] name, String[] location, String[] sdate) {
        this.context = applicationContext;
        this.name1 = name;
        this.location1 = location;
        this.sdate1 = sdate;
    }


    @Override
    public int getCount() {
        return name1.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if (view == null) {
            gridView = new View(context);
            //gridView=inflator.inflate(R.layout.activity_custom_emergency, null);
            gridView = inflator.inflate(R.layout.activity_custom_location, null);//same class name

        } else {
            gridView = (View) view;

        }
        TextView tv1 = gridView.findViewById(R.id.textView19);
        TextView tv2 = gridView.findViewById(R.id.textView20);
        TextView tv3 = gridView.findViewById(R.id.textView21);

        tv2.setTextColor(Color.RED);//color setting
        tv1.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);

        tv1.setText(name1[i]);
        tv2.setText(location1[i]);
        tv3.setText(sdate1[i]);

        return gridView;

    }
}