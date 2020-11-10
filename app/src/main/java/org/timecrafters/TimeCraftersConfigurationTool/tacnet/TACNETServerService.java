package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.timecrafters.TimeCraftersConfigurationTool.MainActivity;
import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class TACNETServerService extends Service {
    private static final String CHANNEL_ID = "TACNET_SERVER_SERVICE";
    private static final String TAG = "TACNETServerService";
    private static final int ID = 8962_0;

    public TACNETServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        foregroundify();
        startServer();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping server...");
        stopServer();
        stopForeground(true);
    }

    private void startServer() {
        if (Backend.instance().getServer() == null) {
            Log.i(TAG, "Starting server...");
            Backend.instance().startServer();
        }
    }

    private void stopServer() {
        Backend.instance().stopServer();
    }

    private void foregroundify() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("navigate_to_tacnet", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TACNET server is running")
                .setSmallIcon(R.drawable.tacnet)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();

        startForeground(ID, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TACNET Server Service";
            String description = "TACNET Server Service Description";
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