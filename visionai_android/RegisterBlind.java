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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterBlind extends AppCompatActivity implements View.OnClickListener {
    EditText name, age, place, post, pin, district, email, phone;
    Button b1;
    ImageView img;
    Bitmap bitmap=null;
    String apiURL,ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_blind);

        name = findViewById(R.id.editTextBlindName);
        age = findViewById(R.id.editTextBlindAge);
        place = findViewById(R.id.editTextBlindPlace);
        post = findViewById(R.id.editTextPost);
        pin = findViewById(R.id.editTextBlindPin);
        district = findViewById(R.id.editTextBlindDistrict);
        email = findViewById(R.id.editTextBlindEmail);
        phone = findViewById(R.id.editTextBlindPhone);

        img = findViewById(R.id.imageButton);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ip = sp.getString("url", "");
        apiURL = ip + "/register_blind";

        b1 = findViewById(R.id.BlindButtonSubmit);
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


        ProgressDialog pd=new ProgressDialog(RegisterBlind.this);
        pd.setMessage("Uploading....");
        pd.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, apiURL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            pd.dismiss();
                            Intent i =new Intent(getApplicationContext(),AddBlind.class);
                            startActivity(i);

                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SharedPreferences o= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("name", name.getText().toString());//passing to python
                params.put("phone", phone.getText().toString());//passing to python
                params.put("email", email.getText().toString());
                params.put("place", place.getText().toString());
                params.put("post", post.getText().toString());
                params.put("pin", pin.getText().toString());
                params.put("age", age.getText().toString());
                params.put("district", district.getText().toString());
                params.put("cid", o.getString("Log_in",""));
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

        int flag=0;
        if(name.getText().toString().equals("")){
            name.setError("enter name");
            flag++;
        }
        if(!Patterns.PHONE.matcher(phone.getText().toString()).matches()){
            phone.setError("enter valid phone number");
            flag++;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError("enter valid email");
            flag++;
        }
        if(place.getText().toString().equals("")){
            place.setError("enter place");
            flag++;
        }
        if(age.getText().toString().equals("")){
            age.setError("enter age");
            flag++;
        }
        if(post.getText().toString().equals("")){
            post.setError("enter post");
            flag++;
        }
        if(pin.getText().length()!=6){
            pin.setError("enter valid pin");
            flag++;
        }
        if(district.getText().toString().equals("")){
            district.setError("enter district");
            flag++;
        }
        if(bitmap==null){
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show();
            flag++;
        }

        if(flag==0) {
            uploadBitmap();
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Caretaker_Home.class));
    }
}
