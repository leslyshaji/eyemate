package com.stjosephscpllege.visionai_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Send_review extends AppCompatActivity {
    ListView ls;
    SharedPreferences sh;
    String ip, url, lid, sid;
    String[] name, review, email, sdate, photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ls = findViewById(R.id.lv);
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sh.getString("url", "");
        lid = sh.getString("logid", "");
        url = ip + "/viewcom";


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
//                                photo = new String[js.length()];

                                for (int i = 0; i < js.length(); i++) {
                                    JSONObject u = js.getJSONObject(i);
                                    name[i] = u.getString("complaint");//dbcolumn name
                                    review[i] = u.getString("date");
                                    email[i] = u.getString("reply");
                                    sdate[i] = u.getString("r_date");
//                                    photo[i] = u.getString("photo");


                                }
                                ls.setAdapter(new custom_com(getApplicationContext(), name, review, email, sdate));//custom_view_service.xml and li is the listview object


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
                        Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
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
                Intent ij = new Intent(getApplicationContext(),Send_com.class);
                startActivity(ij);
            }
        });
    }

}
