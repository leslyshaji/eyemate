package com.stjosephscpllege.visionai_android;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class editUser extends AppCompatActivity implements View.OnClickListener {
    EditText name, dob, place, post, pin, email, phone;
    String gender = "other";
    RadioButton radioButton, radioButton2;
    Button b1;
    ImageView img;
    Bitmap bitmap;
    String apiURL, ip, url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = findViewById(R.id.editTextCaretakerName);
        dob = findViewById(R.id.editTextDate);
        place = findViewById(R.id.editTextCaretakerPlace);
        post = findViewById(R.id.editTextCaretakerPost);
        pin = findViewById(R.id.editTextCaretakerPin);
        email = findViewById(R.id.editTextCaratakerEmail);
        phone = findViewById(R.id.editTextPhone);
        radioButton = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);

        img = findViewById(R.id.imageButton2);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sp.getString("url", "");
        apiURL = ip + "/update_caretaker";
        url2 = ip + "/profile_caretaker";

        b1 = findViewById(R.id.button9);
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
        StringRequest postRequest = new StringRequest(Request.Method.POST, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(editUser.this, response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("s").equalsIgnoreCase("1")) {

                                name.setText(jsonObj.getString("c_name"));
                                dob.setText(jsonObj.getString("dob"));
                                place.setText(jsonObj.getString("place"));
                                post.setText(jsonObj.getString("post"));
                                pin.setText(jsonObj.getString("pin"));
                                email.setText(jsonObj.getString("email"));
                                phone.setText(jsonObj.getString("phone_number"));
                                if (jsonObj.getString("gender").equalsIgnoreCase("male")) {
                                    radioButton.setChecked(true);
                                } else {
                                    radioButton2.setChecked(true);
                                }
                                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                String ip = sh.getString("url", "");
                                String url =ip+ jsonObj.getString("photo");
                                Picasso.with(getApplicationContext()).load(url).into(img);//circle

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
                String lid = sh1.getString("Log_in", "");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                img.setImageBitmap(bitmap);
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


    private void uploadBitmap() {


        if (radioButton.isChecked()) {
            gender = "male";
        } else if (radioButton2.isChecked()) {
            gender = "female";
        }
        ProgressDialog pd = new ProgressDialog(editUser.this);
        pd.setMessage("Uploading....");
        pd.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            pd.dismiss();
                            Intent i = new Intent(getApplicationContext(), editUser.class);
                            startActivity(i);
                            JSONObject obj = new JSONObject(new String(response.data));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences o = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("name", name.getText().toString());//passing to python
                params.put("phone", phone.getText().toString());//passing to python
                params.put("email", email.getText().toString());
                params.put("place", place.getText().toString());
                params.put("post", post.getText().toString());
                params.put("pin", pin.getText().toString());
                params.put("gender", gender);
                params.put("dob", dob.getText().toString());
                String lid = o.getString("Log_in", "");
                params.put("lid", lid);
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

        if (phone.getText().length() != 10) {
            phone.setError("enter valid number");
            flag++;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("enter valid email");
            flag++;
        }
        if (place.getText().toString().equals("")) {
            place.setError("enter place");
            flag++;
        }
        if (post.getText().toString().equals("")) {
            post.setError("enter post");
            flag++;
        }
        if (pin.getText().length() != 6) {
            pin.setError("enter valid pin");
            flag++;
        }
        if (dob.getText().toString().equals("")) {
            dob.setError("Enter date of birth");
            flag++;
        }
        if(bitmap==null && flag==0){
            if (radioButton.isChecked()) {
                gender = "male";
            } else if (radioButton2.isChecked()) {
                gender = "female";
            }
            ProgressDialog pd = new ProgressDialog(editUser.this);
            pd.setMessage("Uploading....");
            pd.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                pd.dismiss();
                                Intent i = new Intent(getApplicationContext(), editUser.class);
                                startActivity(i);
                                JSONObject obj = new JSONObject(new String(response.data));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    SharedPreferences o = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    params.put("name", name.getText().toString());//passing to python
                    params.put("phone", phone.getText().toString());//passing to python
                    params.put("email", email.getText().toString());
                    params.put("place", place.getText().toString());
                    params.put("post", post.getText().toString());
                    params.put("pin", pin.getText().toString());
                    params.put("gender", gender);
                    params.put("dob", dob.getText().toString());
                    String lid = o.getString("Log_in", "");
                    params.put("lid", lid);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), Caretaker_Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
