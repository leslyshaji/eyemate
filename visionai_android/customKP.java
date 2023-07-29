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

public class customKP extends BaseAdapter {

    String[] name1,phone1,photo1,bname,kid;
    private Context context;
    ImageView img,img1,img2;

    public customKP(Context applicationContext, String[] name, String[] phone, String[] photo, String[] bname, String[] kid) {
        this.context=applicationContext;
        this.name1=name;
        this.phone1 = phone;
        this.kid=kid;

        this.photo1 = photo;
        this.bname=bname;

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
            new View(context);
            //gridView=inflator.inflate(R.layout.customview, null);
            gridView=inflator.inflate(R.layout.activity_custom_k_p,null);//same class name

        }
        else
        {
            gridView=(View)view;

        }
        TextView tv1=(TextView)gridView.findViewById(R.id.textView18);
        TextView tv2=(TextView)gridView.findViewById(R.id.textView23);
        TextView tv3=(TextView)gridView.findViewById(R.id.textView24);

        img = (ImageView)gridView.findViewById(R.id.imageView4);
        img1 = (ImageView)gridView.findViewById(R.id.imageView5);
        img2 = (ImageView)gridView.findViewById(R.id.imageView7);
        img1.setTag(i);
        img2.setTag(i);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ik= (int) view.getTag();
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed1 = sh.edit();
                ed1.putString("k_id", kid[ik]);
                ed1.apply();
                String ip=sh.getString("url","");
                String url=ip+"/dlkp";
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    if (jsonObj.getString("s").equalsIgnoreCase("1")) {



//                                            Toast.makeText(context, "Welcome, Caretaker", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(context, KnownPerson.class);
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
                                Toast.makeText(context, "No Response" + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {

                    //                value Passing android to python
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();


                        params.put("k", kid[i]);


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
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ik= (int) view.getTag();
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor ed1 = sh.edit();
                ed1.putString("k_id", kid[ik]);
                ed1.apply();
                Intent i = new Intent(context, editKnownPerson.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
        tv1.setTextColor(Color.RED);//color setting
        tv2.setTextColor(Color.BLACK);
        tv3.setTextColor(Color.BLACK);

        tv1.setText(name1[i]);
        tv2.setText(phone1[i]);
//        tv3.setText(bname[i]);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = sh.getString("url", "");
        String url =ip + photo1[i];
        Picasso.with(context).load(url).into(img);//circle


        return gridView;

    }
}


