package com.journaldev.androidcameraxopencv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText et1, et2;
    Button b1;
    String ip_address, blind_id;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et1 = findViewById(R.id.blind_id);
        et2 = findViewById(R.id.ip_field);
        b1 = findViewById(R.id.button1);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        et2.setText(sp.getString("ip", ""));
        et1.setText(sp.getString("Bphone", ""));

        b1.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int flg=0;
        ip_address = et2.getText().toString().trim();
        final String phone = et1.getText().toString().trim();
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("Bphone",et1.getText().toString().trim());
        editor.commit();
        if(ip_address.equalsIgnoreCase(""))
        {
            flg++;
            et2.setError("*");

        }
if(phone.equalsIgnoreCase(""))
        {
            flg++;
            et1.setError("*");

        }
if(!(phone.length() ==10))
        {
            flg++;
            et1.setError("*");

        }


        if(flg == 0){
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("ip", ip_address);
            ed.putString("url", "http://" + sp.getString("ip", "") + ":4000");
            ed.putString("bid", phone);
            ed.putString("id", phone);
            ed.commit();

            String apiURL = sp.getString("url", "") + "/verify_blind";

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, apiURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                            // response
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                                    Toast.makeText(getApplicationContext(), "ok.....", Toast.LENGTH_SHORT).show();


                                    SharedPreferences.Editor ed = sp.edit();
                                    ed.putString("bid", jsonObj.getString("bid"));
                                    ed.putString("phone", jsonObj.getString("cphone"));
                                    ed.commit();
//                                    Toast.makeText(Login.this, ""+LocationService.logi, Toast.LENGTH_SHORT).show();
                                    startService(new Intent(getApplicationContext(), gpstracker.class));
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);


                                } else
                                    Toast.makeText(getApplicationContext(), "invalid.....", Toast.LENGTH_SHORT).show();


                                // }
                                ///
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
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("u", phone);

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

    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(Intent.ACTION_MAIN);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.addCategory(Intent.CATEGORY_HOME);
        startActivity(in);
    }
}