package org.timecrafters.TimeCraftersConfigurationTool.backend;

import android.os.SystemClock;

import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Client;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Connection;

import java.io.IOException;

public class TACNET {
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
        connection.connect(null);
    }

    public Status status() {
        if (isConnected()) {
            return Status.CONNECTED;
        } else if (connection != null && !connection.socketError()) {
            return Status.CONNECTING;
        } else if (connection != null && connection.socketError()) {
            return Status.CONNECTION_ERROR;
        } else {
            return Status.NOT_CONNECTED;
        }
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
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
