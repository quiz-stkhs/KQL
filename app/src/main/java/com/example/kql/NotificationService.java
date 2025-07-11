package com.example.kql;

import static android.app.Service.START_STICKY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class NotificationService extends Service {

    private static final int EVENT_NOTIF = 0;
    private static final String CHANNEL_ID = "daily_channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            nm.createNotificationChannel(channel);
        }

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Hello")
                    .setContentText("This is your 12:22 PM daily reminder.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your icon
                    .build();
        }
        else {
            notification = new Notification.Builder(this)
                    .setContentTitle("Hello")
                    .setContentText("This is your 12:22 PM daily reminder.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your icon
                    .build();
        }

        nm.notify(EVENT_NOTIF, notification);
        Toast.makeText(this, "Notification Sent", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }
}
