package com.stjosephscpllege.visionai_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class custom_com extends BaseAdapter {
    String[] name1,review1,email1,sdate1;
    private Context context;

    public custom_com(Context applicationContext, String[] name, String[] review, String[] email, String[] sdate) {
        this.context=applicationContext;
        this.name1=name;
        this.review1 = review;
        this.email1 = email;
        this.sdate1 = sdate;
    }

    @Override
    public int getCount() {
        return sdate1.length;
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


        LayoutInflater inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if(view==null)
        {
            gridView=new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView=inflator.inflate(R.layout.activity_custom_com,null);//same class name

        }
        else
        {
            gridView=(View)view;

        }
        TextView tv1=(TextView)gridView.findViewById(R.id.textView7);
        TextView tv2=(TextView)gridView.findViewById(R.id.textView10);
        TextView tv3=(TextView)gridView.findViewById(R.id.textView12);
        TextView tv4=(TextView)gridView.findViewById(R.id.textView47);


        tv1.setTextColor(Color.RED);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);
        tv4.setTextColor(Color.BLACK);

        tv1.setText(name1[i]);
        tv2.setText(review1[i]);
        tv3.setText(email1[i]);
        tv4.setText(sdate1[i]);

        return gridView;


    }
}
