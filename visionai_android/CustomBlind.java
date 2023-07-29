package com.stjosephscpllege.visionai_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomBlind extends BaseAdapter {
    String[] name1,email1,age1,photo1, place1, post1, pin1, district1, phone1,b;
    private Context context;
    ImageView img,img1,img2,img3,img4;


    public CustomBlind(Context applicationContext, String[] b,String[] name, String[] email, String[] photo, String[] age, String[] place, String[] post, String[] pin, String[] district, String[] phone) {
        this.context=applicationContext;
        this.name1=name;
        this.email1 = email;
        this.b = b;
        this.photo1 = photo;
        this.age1 = age;
        this.place1 = place;
        this.post1 = post;
        this.pin1 = pin;
        this.district1 = district;
        this.phone1 = phone;

    }


    @Override
    public int getCount() {

        return name1.length;
    }

    @Override
    public Object getItem(int position) { return null;
    }

    @Override
    public long getItemId(int position) { return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {


        LayoutInflater inflator=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        if(view==null)
        {
            gridView = new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView=inflator.inflate(R.layout.custom_blind,null);//same class name

        }
        else
        {
            gridView=(View)view;

        }
        TextView tv1=(TextView)gridView.findViewById(R.id.textView9);
        TextView tv2=(TextView)gridView.findViewById(R.id.textView33);
        TextView tv3=(TextView)gridView.findViewById(R.id.textView50);
        TextView tv4=(TextView)gridView.findViewById(R.id.textView14);
        TextView tv5=(TextView)gridView.findViewById(R.id.textView25);
        TextView tv6=(TextView)gridView.findViewById(R.id.textView27);
        TextView tv7=(TextView)gridView.findViewById(R.id.textView29);
        TextView tv8=(TextView)gridView.findViewById(R.id.textView31);

        img = (ImageView)gridView.findViewById(R.id.imageView3);
        img1 = (ImageView)gridView.findViewById(R.id.imageView10);
        img2 = (ImageView)gridView.findViewById(R.id.imageView11);
        img3 = (ImageView)gridView.findViewById(R.id.imageView12);
        img4 = (ImageView)gridView.findViewById(R.id.imageView13);
        img1.setTag(i);
        img2.setTag(i);
        img3.setTag(i);
        img4.setTag(i);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed1 = sh.edit();
                int k= (int) view.getTag();
                ed1.putString("b_id", b[k]);
                ed1.apply();
                Intent i = new Intent(context, editBlind.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
                String ip = sh.getString("url", "");
                String url2 =ip + "/remove_blind";
                SharedPreferences.Editor ed1 = sh.edit();
                int k= (int) view.getTag();
                ed1.putString("b_id", b[k]);
                ed1.apply();
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObj = new JSONObject(response);
                                        if (jsonObj.getString("s").equalsIgnoreCase("1")) {
                                            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(context, AddBlind.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(i);
                                        } else {
                                            Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Exception e) {
                                        Toast.makeText(context, "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Toast.makeText(context, "No Reponse" + error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {

                        //                value Passing android to python
                        @Override
                        protected Map<String, String> getParams() {
                            SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(context);
                            Map<String, String> params = new HashMap<String, String>();
                            String lid = sh.getString("b_id", "");
                            params.put("b_id", lid);
                            return params;
                        }
                    };


                    int MY_SOCKET_TIMEOUT_MS = 100000;

                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
                            MY_SOCKET_TIMEOUT_MS,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(postRequest);


            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed1 = sh.edit();
                int k= (int) view.getTag();
                ed1.putString("b_id", b[k]);
                ed1.apply();

                Intent i = new Intent(context, KnownPerson.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sh=PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed1 = sh.edit();
                int k= (int) view.getTag();
                ed1.putString("b_id", b[k]);
                ed1.apply();

                Intent i = new Intent(context, blocation.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
            }
        });

        tv1.setTextColor(Color.RED);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);
        tv4.setTextColor(Color.BLACK);
        tv5.setTextColor(Color.BLACK);
        tv6.setTextColor(Color.BLACK);
        tv7.setTextColor(Color.BLACK);
        tv8.setTextColor(Color.BLACK);

        tv1.setText(name1[i]);
        tv2.setText(email1[i]);
        tv3.setText(phone1[i]);
        tv4.setText(age1[i]);
        tv5.setText(place1[i]);
        tv6.setText(post1[i]);
        tv7.setText(pin1[i]);
        tv8.setText(district1[i]);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = sh.getString("url", "");
        String url = ip + photo1[i];
        Picasso.with(context).load(url).into(img);//circle


        return gridView;

    }




}


