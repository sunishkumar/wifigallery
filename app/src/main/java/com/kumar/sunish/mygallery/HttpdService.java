package com.kumar.sunish.mygallery;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by sunish on 11/8/15.
 */
public class HttpdService  extends Service {

    private NotificationManager mNM;
    private int NOTIFICATION = 222;

    private PowerManager.WakeLock wakeLock=null;

    private MyHTTPD server;



    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startHttpServer();
        this.startForeground(NOTIFICATION,createNotification());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        AppData.getInstance().setServerRunning(false);
        if (server != null) {
            server.stop();

        }
        try{
            wakeLock.release();
        }catch (Exception x){}
        super.onDestroy();
    }

    public void startHttpServer() {

        try {
            if (server != null) {

                return;
            }
            server = new MyHTTPD(getApplicationContext());
            server.start();
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();
            AppData.getInstance().setServerRunning(true);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    private Notification createNotification() {
        String message = "";
        String title = "";
        final String address = ServerUtil.getIpAddress();
        if ( address == null|| address.equals("")) {
            title = "Server unconnectable";
            message = "No IP address available. Check your WIFI";
        }
        else {
            title = "Server running";
            message = String.format("%s", address+":"+server.getListeningPort());
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setOngoing(true);

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        return mBuilder.build();
    }


}