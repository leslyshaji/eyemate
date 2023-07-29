//package com.example.angeleyes;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//
//public class EmegencyVideoAlert extends Service {
//    public EmegencyVideoAlert() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//}




package com.stjosephscpllege.visionai_android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

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

public class EmegencyAlert extends Service {
    String url="";
    SharedPreferences sp;
    private Handler handler = new Handler();
    public static boolean isService = true;

    @Override
    public void onCreate() {
        super.onCreate();

        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        url = sp.getString("url", "") + "/view_emergency_alert";
        isService = true;
    }

    final String TAG = "Alert";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "Start services", Toast.LENGTH_SHORT).show();

        handler.post(AlertFinder);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(AlertFinder);
        handler = null;
        Toast.makeText(this, "Service Stopped..!!", Toast.LENGTH_SHORT).show();
        isService = false;
    }

    public Runnable AlertFinder = new Runnable() {
        public void run()
        {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                if (jsonObj.getString("status").equalsIgnoreCase("ok")) {

                                    SharedPreferences.Editor ed=sp.edit();
                                    ed.putString("ls_id",jsonObj.getString("eid"));
                                    ed.commit();
                                    String txt=jsonObj.getString("msg")+" from "+jsonObj.getString("bname");
                                    issueNotification(txt);
                                }
//                                    else
//                                    {
////										Toast.makeText(VSend_Complaint.this, "failed", Toast.LENGTH_SHORT).show();
//                                    }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                    Toast.makeText(getApplicationContext(), "No Response" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("cid", sp.getString("Log_in", ""));
                    params.put("lastid",sp.getString("ls_id","0"));
                    return params;
                }
            };

            int MY_SOCKET_TIMEOUT_MS = 120000;
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(postRequest);

            handler.postDelayed(AlertFinder, 10000);// register again to start after 35 seconds...
        }
    };



    void issueNotification(String msg)
    {

        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        }
        // the check ensures that the channel will only be made
        // if the device is running Android 8+

        Intent resultIntent=new Intent(this,Viewemerreq.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        // the second parameter is the channel id.
        // it should be the same as passed to the makeNotificationChannel() method

        notification
                .setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
                .setContentTitle("Emergency Notification!")
                .setContentText(msg)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setNumber(3); // this shows a number in the notification dots

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;

        notificationManager.notify(1, notification.build());
        // it is better to not use 0 as notification id, so used 1.
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
