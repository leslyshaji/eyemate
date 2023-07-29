package com.stjosephscpllege.visionai_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KnownPerson extends AppCompatActivity{


    ListView ls;
    SharedPreferences sh;
    String ip,url,sid;
    String[] name,phone,photo,b,kid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_known_person);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ls=(ListView)findViewById(R.id.m);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddKnownPerson.class);
                startActivity(i);
            }
        });

        sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip=sh.getString("url","");
        url=ip+"/view_kp";

//        ls.setOnItemClickListener(this);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("s").equalsIgnoreCase("1")) {
                                JSONArray js= jsonObj.getJSONArray("data");//from python
                                name=new String[js.length()];
                                phone=new String[js.length()];
                                photo=new String[js.length()];
                                b=new String[js.length()];
                                kid=new String[js.length()];

                                for(int i=0;i<js.length();i++)
                                {
                                    JSONObject u=js.getJSONObject(i);
                                    name[i]=u.getString("kpname");//dbcolumn name
                                    photo[i]=u.getString("pimage");
                                    phone[i]=u.getString("pphone");
                                    b[i]=u.getString("b_name");
                                    kid[i]=u.getString("kp_id");


                                }
                                ls.setAdapter(new customKP(getApplicationContext(),name,phone,photo,b,kid));//custom_view_service.xml and li is the listview object


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
                        Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            //                value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();
                String lid=sh.getString("b_id","");
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
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String bid1=kid[position];
//        SharedPreferences.Editor ed1 = sh.edit();
//        ed1.putString("k_id", bid1);
//        ed1.commit();
//        Intent i = new Intent(getApplicationContext(), editKnownPerson.class);
//        startActivity(i);
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Caretaker_Home.class);
        startActivity(i);
    }

}