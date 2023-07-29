package com.stjosephscpllege.visionai_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Activity_IP extends AppCompatActivity implements View.OnClickListener {
    EditText e1;
    Button b1;
    String ip_address;
    SharedPreferences sp;
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__i_p);

        b1 = findViewById(R.id.button1);
        e1 = findViewById(R.id.ip_address);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e1.setText(sp.getString("ip",""));
        b1.setOnClickListener(this);


        if (android.os.Build.VERSION.SDK_INT > 23) {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (checkPermissions()) {
                //  permissions  granted.
            }
        }



    }


    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),0 );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case 0:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;

                        }

                    }
                    // Show permissionsDenied
//                    updateViews();
                }
                return;
            }
        }
    }



    @Override
    public void onClick(View v) {

        int flg=0;
        ip_address = e1.getText().toString().trim();

        SharedPreferences.Editor ed = sp.edit();
        ed.putString("ip", ip_address);
        ed.putString("url",  "http://" + ip_address + ":4000");
        ed.apply();

        if(ip_address.equalsIgnoreCase("")){
         flg++;
         e1.setError("*");
        }
        if(flg==0) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        showExitDialogue();
    }

    public void showExitDialogue(){
        AlertDialog.Builder myExitDialogue = new AlertDialog.Builder(this);
        myExitDialogue.setTitle("Exit app?");
        myExitDialogue.setMessage("Do you want to exit the app?");

        myExitDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in=new Intent(Intent.ACTION_MAIN);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.addCategory(Intent.CATEGORY_HOME);
                startActivity(in);
            }
        });

        myExitDialogue.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        myExitDialogue.create();
        myExitDialogue.show();
    }
}