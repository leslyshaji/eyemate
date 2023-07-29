package com.stjosephscpllege.visionai_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class customreview extends BaseAdapter {
    String[] name1,review1,email1,sdate1,photo1;
    private Context context;
    ImageView img;
//    private Object ImageView;

    public customreview(Context applicationContext, String[] name, String[] review, String[] email, String[] sdate, String[] photo) {
    this.context=applicationContext;
    this.name1=name;
    this.review1 = review;
    this.email1 = email;
    this.sdate1 = sdate;
    this.photo1 = photo;

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



        LayoutInflater inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if(view==null)
        {
            gridView=new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView=inflator.inflate(R.layout.customreview,null);//same class name

        }
        else
        {
            gridView=(View)view;

        }
        TextView tv1=(TextView)gridView.findViewById(R.id.textView7);
        TextView tv2=(TextView)gridView.findViewById(R.id.textView10);
        TextView tv3=(TextView)gridView.findViewById(R.id.textView12);

         img = (ImageView)gridView.findViewById(R.id.imageView2);

        tv1.setTextColor(Color.RED);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);

        tv1.setText(name1[i]);
        tv2.setText(email1[i]);
        tv3.setText(review1[i]);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = sh.getString("url", "");
        String url = ip  + photo1[i];
        Picasso.with(context).load(url).transform(new CircleTransform()).into(img);//circle


        return gridView;

    }
}
