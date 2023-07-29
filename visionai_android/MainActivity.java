package com.stjosephscpllege.visionai_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btLogin, btSignup;
    EditText etEmail, etPasswd;
    String url, ip;
    TextView txForgotPWD;
    SharedPreferences sp;

    String stringEmail, stringPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btLogin = findViewById(R.id.btLogin);
        btSignup = findViewById(R.id.button_signup);
        etEmail = findViewById(R.id.etEmail);
        etPasswd = findViewById(R.id.etPassword);
        txForgotPWD = findViewById(R.id.txForgotPWD);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sp.getString("url", "");
        url =  ip + "/Login";
        btLogin.setOnClickListener(this);
        txForgotPWD.setOnClickListener(this);
        btSignup.setOnClickListener(this);
        etEmail.setText(sp.getString("u",""));
        etPasswd.setText(sp.getString("p",""));

    }

    @Override
    public void onClick(View v) {
        int flag = 0;
        if (v == txForgotPWD) {
            Intent i = new Intent(this, Forgot_password.class);
            startActivity(i);
        }
        if (v == btSignup) {
            Intent i = new Intent(this, CaretakerRegister.class);
            startActivity(i);
        }
        if (v == btLogin) {
            final String s1 = etEmail.getText().toString();
            final String s2 = etPasswd.getText().toString();
            SharedPreferences.Editor ed=sp.edit();
            ed.putString("u",s1);
            ed.putString("p",s2);
ed.apply();
            if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                etEmail.setError("enter valid email");
                flag++;
            }
            if (etPasswd.getText().toString().equals("")) {
                etPasswd.setError("Enter Password");
                flag++;
//                Intent ij = new Intent(getApplicationContext())
            }
            if(flag==0) {

                Log.d("==========",url);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    if (jsonObj.getString("s").equalsIgnoreCase("1")) {
                                        String l_id = jsonObj.getString("id");
                                        SharedPreferences.Editor ed1 = sp.edit();
                                        ed1.putString("Log_in", l_id);
                                        ed1.putString("cname", jsonObj.getString("name"));
                                        ed1.putString("cphoto", jsonObj.getString("image"));
                                        ed1.commit();

                                        if (jsonObj.getString("type").equalsIgnoreCase("caretaker")) {

                                            startService(new Intent(getApplicationContext(), EmegencyAlert.class));
//                                            Toast.makeText(getApplicationContext(), "Welcome, Caretaker", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), Caretaker_Home.class);
                                            startActivity(i);
                                        }
                                        if (jsonObj.getString("type").equalsIgnoreCase("blind")) {


//                                            Toast.makeText(getApplicationContext(), "Welcome, Caretaker", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), Caretaker_Home.class);
                                            startActivity(i);
                                        }
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
                                Toast.makeText(getApplicationContext(), "No Response" + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {

                    //                value Passing android to python
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("username", s1);//passing to python
                        params.put("password", s2);


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
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Activity_IP.class);
        startActivity(i);
    }
}


