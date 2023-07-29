package com.stjosephscpllege.visionai_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class Caretaker_Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    TextView t1, t2, t3, t4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caretaker__home);

        t1 = findViewById(R.id.home_register);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterBlind.class);
                startActivity(i);
            }
        });

        t2 = findViewById(R.id.home_emerency);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Viewemerreq.class);
                startActivity(i);
            }
        });

        t3 = findViewById(R.id.home_review);
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SendingReview.class);
                startActivity(i);
            }
        });
        t4 = findViewById(R.id.home_kp);
        t4.setVisibility(View.GONE);
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddKnownPerson.class);
                startActivity(i);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = sh.getString("cname", "");
        String photo = sh.getString("cphoto", "");
        String ip = sh.getString("url", "");
        String url =  ip + photo;

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.user_name);
        navUsername.setText(name);
        ImageView img = (ImageView) headerView.findViewById(R.id.imageView);

        Picasso.with(Caretaker_Home.this).load(url).transform(new CircleTransform()).into(img);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.caretaker__home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.more_info) {
            Toast.makeText(this, "Thank you \u2665", Toast.LENGTH_LONG).show();
//            Intent i = new Intent(getApplicationContext(), AddBlind.class);
//            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.blind) {
            Intent i = new Intent(getApplicationContext(), AddBlind.class);
            startActivity(i);
        } else if (id == R.id.view_profile) {
            Intent i = new Intent(getApplicationContext(), editUser.class);
            startActivity(i);
        } else if (id == R.id.e_help) {
            Intent i = new Intent(getApplicationContext(), Viewemerreq.class);
            startActivity(i);
        }
        else if (id == R.id.reviews) {
            Intent i = new Intent(getApplicationContext(), Sendreview.class);
            startActivity(i);
        } else if (id == R.id.change_password) {
            Intent i = new Intent(getApplicationContext(), changePassword.class);
            startActivity(i);
        } else if (id == R.id.logout) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }else if (id == R.id.h) {
            Intent i = new Intent(getApplicationContext(), Send_review.class);
            startActivity(i);
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        showExitDialogue();
        //super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), Sendreview.class);
        startActivity(i);
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