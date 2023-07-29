package com.stjosephscpllege.visionai_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class blocation extends AppCompatActivity {
    ListView ls;
    SharedPreferences sh;
    String ip,url,lid,sid;
    String[] name,location,sdate,lat,lg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocation);
        ls = findViewById(R.id.bloc);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String strUri = "http://maps.google.com/maps?q=loc:" + lat[i] + "," + lg[i] + " (" + location[i] + ")";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                startActivity(intent);


            }
        });

        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip=sh.getString("url","");
        lid=sh.getString("logid","");
        url=ip+"/viewlocation";


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("s").equalsIgnoreCase("1")) {
                                JSONArray js= jsonObj.getJSONArray("data");//from python
                                Toast.makeText(getApplicationContext(), ""+js, Toast.LENGTH_SHORT).show();
                                name=new String[js.length()];
                                location=new String[js.length()];
                                sdate=new String[js.length()];
                                lat=new String[js.length()];
                                lg=new String[js.length()];

                                for(int i=0;i<js.length();i++)
                                {
                                    JSONObject u=js.getJSONObject(i);
                                    name[i]=u.getString("b_name");//dbcolumn name
                                    location[i]=u.getString("location");
                                    sdate[i]=u.getString("datetime");
                                    lat[i]=u.getString("lattitude");
                                    lg[i]=u.getString("longitude");



                                }
                                ls.setAdapter(new custom_location(getApplicationContext(),name,location,sdate));//custom_view_service.xml and li is the listview object


                            }

                            else {
                                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            }

                        }    catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getApplicationContext(), "No Response" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            //                value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();
                lid=sh.getString("b_id","");
                params.put("lid",lid);
                return params;
            }
        };


        int MY_SOCKET_TIMEOUT_MS=100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Caretaker_Home.class);
        startActivity(i);
    }
}