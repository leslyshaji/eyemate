package com.stjosephscpllege.visionai_android;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class editKnownPerson extends AppCompatActivity  implements View.OnClickListener {
    EditText name,phone;
    Button b1;
    ImageView img;
    Bitmap bitmap;
    String apiURL,apiURL2,ip;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_known_person);

        name = findViewById(R.id.known_person_name);
        phone = findViewById(R.id.editTextPhoneKP);
        b1 = findViewById(R.id.button7);
        t=findViewById(R.id.textView60);
        if(bitmap==null){
            t.setVisibility(View.VISIBLE);
        }

        img = findViewById(R.id.KnownPersonImage);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sp.getString("url", "");
        apiURL = ip + "/profile_kp";
        apiURL2 =ip + "/update_kp";

        b1.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, apiURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("s").equalsIgnoreCase("1")) {

                                name.setText(jsonObj.getString("kpname"));
                                phone.setText(jsonObj.getString("pphone"));

                                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                String ip = sh.getString("url", "");
                                String url =ip + jsonObj.getString("pimage");
                                Picasso.with(getApplicationContext()).load(url).into(img);//circle
                                t.setVisibility(View.INVISIBLE);

                            } else {
                                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
//                        Toast.makeText(getApplicationContext(), "eeeee" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            //                value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sh1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String, String> params = new HashMap<String, String>();
                String lid = sh1.getString("k_id", "");
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

    @Override
    public void onClick(View v) {


        int flag = 0;
        if (name.getText().toString().equals("")) {
            name.setError("enter name");
            flag++;
        }
        if (!Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
            phone.setError("enter valid phone number");
            flag++;
        }
        if(bitmap==null && flag==0){
            ProgressDialog pd = new ProgressDialog(editKnownPerson.this);
            pd.setMessage("Uploading....");
            pd.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL2,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {

                                pd.dismiss();

                                JSONObject obj = new JSONObject(new String(response.data));
                                if(obj.getString("message").equalsIgnoreCase("succes")) {
                                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), KnownPerson.class);
                                    startActivity(i);
                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "Can not identify face", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                pd.dismiss();
//                                Toast.makeText(editKnownPerson.this, ""+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    SharedPreferences o = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    params.put("name", name.getText().toString());//passing to python
                    params.put("phone", phone.getText().toString());//passing to python
                    params.put("cid", o.getString("k_id", ""));
                    return params;
                }



            };

            int MY_SOCKET_TIMEOUT_MS = 100000;

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }
        if (flag == 0) {
            uploadBitmap();
        }
    }

    private void uploadBitmap() {


        ProgressDialog pd = new ProgressDialog(editKnownPerson.this);
        pd.setMessage("Uploading....");
        pd.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL2,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            pd.dismiss();
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.getString("message").equalsIgnoreCase("succes")) {

                                Toast.makeText(getApplicationContext(), obj.getString("Updated successfully"), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), KnownPerson.class);
                                startActivity(i);
                            }
                            else {
                                Toast.makeText(editKnownPerson.this, "Can not identify face", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            pd.dismiss();
//                            Toast.makeText(editKnownPerson.this, "Can not identify face", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        pd.dismiss();
//                        Toast.makeText(getApplicationContext(),"Can not identify face", Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences o = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("name", name.getText().toString());//passing to python
                params.put("phone", phone.getText().toString());//passing to python
                params.put("cid", o.getString("k_id", ""));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long image_name = System.currentTimeMillis();
                params.put("pic", new DataPart(image_name + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        int MY_SOCKET_TIMEOUT_MS = 100000;

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                img.setImageBitmap(bitmap);
                t.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //converting to bitarray
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}