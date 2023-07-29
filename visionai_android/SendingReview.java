package com.stjosephscpllege.visionai_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SendingReview extends AppCompatActivity implements View.OnClickListener {
    EditText Review;
    Button b1;
    SharedPreferences sp;
    String ip;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_review);

        Review = findViewById(R.id.ReviewText);
        b1 = findViewById(R.id.button3);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sp.getString("url", "");
        url = ip + "/sendingreview";

        b1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String rv = Review.getText().toString();
        if (rv.equals("")) {
            Review.setError("enter review");
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                if (jsonObj.getString("s").equalsIgnoreCase("1")) {
                                    Toast.makeText(getApplicationContext(), "Review send ", Toast.LENGTH_LONG).show();

                                    Intent i =new Intent(getApplicationContext(),Sendreview.class);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(getApplicationContext(), " User not found", Toast.LENGTH_LONG).show();

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

                    Map<String, String> params = new HashMap<String, String>();
                    String lid = sp.getString("Log_in", "");

                    params.put("Review", rv);//passing to python
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
        }
    }

}