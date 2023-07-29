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

public class custom_emergency extends BaseAdapter {
    String[] name1, message1, sdate1;
    private Context context;


    public custom_emergency(Context applicationContext, String[] name, String[] message, String[] sdate) {
        this.context = applicationContext;
        this.name1 = name;
        this.message1 = message;
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
            gridView = inflator.inflate(R.layout.activity_custom_emergency, null);//same class name

        } else {
            gridView = (View) view;

        }
        TextView tv1 = gridView.findViewById(R.id.textView11);
        TextView tv2 = gridView.findViewById(R.id.textView15);
        TextView tv3 = gridView.findViewById(R.id.textView5);

        tv2.setTextColor(Color.RED);//color setting
        tv1.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);


        tv1.setText(name1[i]);
        tv2.setText(message1[i]);
        tv3.setText(sdate1[i]);

        return gridView;

    }
}