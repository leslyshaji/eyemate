package com.stjosephscpllege.visionai_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class AddBlind extends AppCompatActivity{
    ListView ls;
    SharedPreferences sh;
    String ip, url, lid, url2;
    String[] name, age, place, post, pin, district, phone, review, email, sdate, photo, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blind);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ls = (ListView) findViewById(R.id.m);
        setSupportActionBar(toolbar);

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sh.getString("url", "");
        lid = sh.getString("logid", "");
        url = ip + "/blind_list";
        url2 = ip + "/remove_blind";

//        ls.setOnItemClickListener(this);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("s").equalsIgnoreCase("1")) {
                                JSONArray js = jsonObj.getJSONArray("data");//from python
                                name = new String[js.length()];
                                review = new String[js.length()];
                                email = new String[js.length()];
                                sdate = new String[js.length()];
                                photo = new String[js.length()];
                                age = new String[js.length()];
                                place = new String[js.length()];
                                post = new String[js.length()];
                                pin = new String[js.length()];
                                district = new String[js.length()];
                                phone = new String[js.length()];
                                b = new String[js.length()];

                                for (int i = 0; i < js.length(); i++) {
                                    JSONObject u = js.getJSONObject(i);
                                    name[i] = u.getString("b_name");//dbcolumn name
                                    email[i] = u.getString("b_email");
                                    photo[i] = u.getString("b_photo");
                                    age[i] = u.getString("b_age");
                                    place[i] = u.getString("b_place");
                                    post[i] = u.getString("b_post");
                                    pin[i] = u.getString("b_pin");
                                    district[i] = u.getString("b_district");
                                    phone[i] = u.getString("b_phone");
                                    b[i] = u.getString("b_id");
//                                    Toast.makeText(AddBlind.this, ""+email[i], Toast.LENGTH_SHORT).show();


                                }
                                ls.setAdapter(new CustomBlind(getApplicationContext(), b,name, email, photo, age, place, post, pin, district, phone));//custom_view_service.xml and li is the listview object


                            } else {
                                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getApplicationContext(), "No Reponse" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            //                value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();
                lid = sh.getString("Log_in", "");
                params.put("lid", lid);
                return params;
            }
        };


        int MY_SOCKET_TIMEOUT_MS = 100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddBlind.this, "Add Blind", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), RegisterBlind.class);
                startActivity(i);
            }
        });
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String bid1 = b[position];
//        SharedPreferences.Editor ed1 = sh.edit();
//        ed1.putString("b_id", bid1);
//        ed1.commit();
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(AddBlind.this);
//        String[] seq = {"Location", "Edit", "Remove", "Known Person","Cancel"};
//        alert.setItems(seq, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                if (which == 0) {
//
//                    Intent i = new Intent(getApplicationContext(), blocation.class);
//                    startActivity(i);
//                }
//                else if (which == 2) {
//
//                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//                    StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//
//                                    try {
//                                        JSONObject jsonObj = new JSONObject(response);
//                                        if (jsonObj.getString("s").equalsIgnoreCase("1")) {
//                                            Toast.makeText(AddBlind.this, "success", Toast.LENGTH_SHORT).show();
//                                            startActivity(new Intent(getApplicationContext(), AddBlind.class));
//                                        } else {
//                                            Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
//                                        }
//
//                                    } catch (Exception e) {
//                                        Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    // error
//                                    Toast.makeText(getApplicationContext(), "No Reponse" + error.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    ) {
//
//                        //                value Passing android to python
//                        @Override
//                        protected Map<String, String> getParams() {
//                            SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                            Map<String, String> params = new HashMap<String, String>();
//                            lid = sh.getString("b_id", "");
//                            params.put("b_id", lid);
//                            return params;
//                        }
//                    };
//
//
//                    int MY_SOCKET_TIMEOUT_MS = 100000;
//
//                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
//                            MY_SOCKET_TIMEOUT_MS,
//                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                    requestQueue.add(postRequest);
//                }
//                else if (which == 1) {
//
//                    startActivity(new Intent(getApplicationContext(), editBlind.class));
//                }
//                else if (which == 3) {
//
//                    startActivity(new Intent(getApplicationContext(), KnownPerson.class));
//                }
//
//            }
//        });
//        AlertDialog al = alert.create();
//        al.show();
//
//    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Caretaker_Home.class));
    }
}