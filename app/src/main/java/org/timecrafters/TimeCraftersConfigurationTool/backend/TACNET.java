package org.timecrafters.TimeCraftersConfigurationTool.backend;

import android.os.SystemClock;
import android.util.Log;

import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Client;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Connection;

import java.io.IOException;

public class TACNET {
    private final static String TAG = "TACNET|TACNET";
    public static final String DEFAULT_HOSTNAME = "192.168.49.1";
    public static final int DEFAULT_PORT = 8962;

    public static final int SYNC_INTERVAL = 250; // ms
    public static final int HEARTBEAT_INTERVAL = 1_500; // ms

    public enum Status {
        CONNECTED,
        CONNECTING,
        CONNECTION_ERROR,
        NOT_CONNECTED,
    }

    private Connection connection;

    public void connect(String hostname, int port) {
        if (connection != null && connection.isConnected()) {
            return;
        }

        connection = new Connection(hostname, port);

        connection.connect(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + connection.lastSocketError());
                Backend.instance().startErrorSound(Backend.instance().applicationContext);
            }
        });
    }

    public Status status() {
        if (isConnecting()) {
            return Status.CONNECTING;
        } else if (isConnectionError()) {
            return Status.CONNECTION_ERROR;
        } else if (isConnected()) {
            return Status.CONNECTED;
        } else {
            return Status.NOT_CONNECTED;
        }
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean isConnecting() {
        return connection != null && !connection.isConnected() && !connection.socketError();
    }

    public boolean isConnectionError() {
        return connection != null && connection.socketError();
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {}

            connection = null;
        }
    }

    public Client getClient() {
        if (isConnected()) {
            return connection.getClient();
        } else {
            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void puts(String message) {
        if (isConnected()) {
            connection.puts(message);
        }
    }

    public String gets() {
        if (isConnected()) {
            return connection.gets();
        } else {
            return null;
        }
    }

    public static long milliseconds() {
        return SystemClock.elapsedRealtime();
    }
}
