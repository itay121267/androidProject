package com.example.project;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String content = intent.getStringExtra("data"); // תוכן מintent להתראה

        NotificationCompat.BigTextStyle contentStyle = new NotificationCompat.BigTextStyle();
        contentStyle.bigText(content);
        contentStyle.setBigContentTitle("Take care of your dog!");

        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Take care of your dog!")
                .setStyle(contentStyle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent2 = new Intent(context, HomeActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int notificationId =(int) System.currentTimeMillis();
        // in order to generate separate notifications, each one should have its own unique id (current timestamp)
        notificationManager.notify(notificationId, builder.build());
    }

}
