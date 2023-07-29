package com.stjosephscpllege.visionai_android;

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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class changePassword extends AppCompatActivity {
    EditText t1, t2, t3;
    String url, ip;
    Button b1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        t1 = findViewById(R.id.current_pwd);
        t2 = findViewById(R.id.change_pwd);
        t3 = findViewById(R.id.confirm_pwd);

        b1 = findViewById(R.id.button4);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flg=0;
                final String cpass = t1.getText().toString();
                final String npass = t2.getText().toString();
                final String cnpass = t3.getText().toString();

                if(cpass.equalsIgnoreCase("")){
                    t1.setError("*");
                    flg++;
                }

                if(npass.equalsIgnoreCase("")){
                    t2.setError("*");
                    flg++;
                }

                if(cnpass.equalsIgnoreCase("")){
                    t3.setError("*");
                    flg++;
                }

                if (!npass.equals(cnpass)) {
                    t3.setError("password not matching");
                    flg++;
                }

                if(flg==0) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    ip = sp.getString("url", "");
                    url = ip + "/change_pwd";

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObj = new JSONObject(response);
                                        if (jsonObj.getString("s").equalsIgnoreCase("1")) {

                                            Toast.makeText(changePassword.this, "success", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(i);

                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "incorrect current password", Toast.LENGTH_LONG).show();
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
                            String lid = sh1.getString("Log_in", "");
                            params.put("lid", lid);
                            params.put("cpass", cpass);
                            params.put("npass", cnpass);
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
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}