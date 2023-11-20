package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.timecrafters.TimeCraftersConfigurationTool.MainActivity;
import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;

public class TACNETConnectionService extends Service {
    private static final String CHANNEL_ID = "TACNET_CONNECTION_SERVICE";
    private static final String TAG = "TACNETConnectionService";
    private static final int ID = 8962_1;

    public TACNETConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        foregroundify();
        connect();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        disconnect();
        stopForeground(true);
    }

    private void connect() {
        if (!Backend.instance().tacnet().isConnected()) {
            final String hostname = Backend.instance().getSettings().hostname;
            final int port = Backend.instance().getSettings().port;

            Backend.instance().tacnet().connect(hostname, port);
        }
    }

    private void disconnect() {
        Backend.instance().tacnet().close();
    }

    private void foregroundify() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("navigate_to_tacnet", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TACNET Connection")
                .setContentText("Connected to: " + Backend.instance().getSettings().hostname + ":" + Backend.instance().getSettings().port)
                .setSmallIcon(R.drawable.tacnet)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();

        startForeground(ID, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TACNET Connection Service";
            String description = "TACNET Connection Service Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}