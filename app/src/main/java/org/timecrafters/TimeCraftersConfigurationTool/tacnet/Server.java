package org.timecrafters.TimeCraftersConfigurationTool.tacnet;

import android.util.Log;

import org.timecrafters.TimeCraftersConfigurationTool.backend.TACNET;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class Server {
  private ServerSocket server;
  private int port;
  private Client activeClient;
  private long lastSyncTime = 0;
  private long syncInterval = TACNET.SYNC_INTERVAL;

  private String TAG = "TACNET|Server";

  private int packetsSent, packetsReceived, clientLastPacketsSent, clientLastPacketsReceived = 0;
  private long dataSent, dataReceived, clientLastDataSent, clientLastDataReceived = 0;

  private Runnable handleClientRunner;
  private PacketHandler packetHandler;

  private long lastHeartBeatSent = 0;
  private long heartBeatInterval = TACNET.HEARTBEAT_INTERVAL;

  public Server(int port) throws IOException {
    this.server = new ServerSocket();
    this.port = port;
    this.packetHandler = new PacketHandler(false);
    this.handleClientRunner = new Runnable() {
      @Override
      public void run() {
        handleClient();
      }
    };
  }

  public void start() throws IOException {
    new Thread(new Runnable() {
      @Override
      public void run() {
        int connectionAttempts = 0;

        while(!server.isBound() && connectionAttempts < 10) {
          try {
            server.bind(new InetSocketAddress(port));
            Log.i(TAG, "Server bound and ready!");
          } catch (IOException e) {
            connectionAttempts++;
            Log.e(TAG, "Server failed to bind: " + e.getMessage());
          }
        }

        while (!server.isClosed()) {
          try {
            runServer();
          } catch (IOException e) {
            Log.e(TAG, "Error running server: " + e.getMessage());
          }

        }
      }
    }).start();
  }

  private void runServer() throws IOException {
    while (!isClosed()) {

      final Client client = new Client();
      client.setSyncInterval(syncInterval);
      client.setSocket(this.server.accept());

      if (activeClient != null && !activeClient.isClosed()) {
        Log.i(TAG, "Too many clients, already have one connected!");
        client.close("Too many clients!");

      } else {
        this.activeClient = client;

        activeClient.puts(PacketHandler.packetHandShake( activeClient.uuid() ).toString());
        activeClient.puts(PacketHandler.packetListConfigs().toString());

        Log.i(TAG, "Client connected!");

        new Thread(new Runnable() {
          @Override
          public void run() {
            while(activeClient != null && !activeClient.isClosed()) {
              if (System.currentTimeMillis() > lastSyncTime + syncInterval) {
                lastSyncTime = System.currentTimeMillis();

                activeClient.sync(handleClientRunner);
                updateNetStats();
              }

              try {
                Thread.sleep(syncInterval);
              } catch (InterruptedException e) {
                // Failed to sleep, i guess.
              }
            }

            updateNetStats();
            activeClient = null;

            clientLastPacketsSent = 0;
            clientLastPacketsReceived = 0;
            clientLastDataSent = 0;
            clientLastDataReceived = 0;

//            AppSync.getMainActivity().clientDisconnected();
          }
        }).start();

      }
    }
  }

  private void handleClient() {
    if (activeClient != null && !activeClient.isClosed()) {
      String message = activeClient.gets();

      if (message != null) {
        packetHandler.handle(message);
      }

      if (System.currentTimeMillis() > lastHeartBeatSent + heartBeatInterval) {
        lastHeartBeatSent = System.currentTimeMillis();

        activeClient.puts(PacketHandler.packetHeartBeat().toString());
      }
    }
  }

  public void stop() throws IOException {
    if (this.activeClient != null) {
      this.activeClient.close();
      this.activeClient = null;
    }

    this.server.close();
  }

  public boolean hasActiveClient() {
    return activeClient != null;
  }

  public Client getActiveClient() {
    return activeClient;
  }

  public int getPacketsSent() {
    return packetsSent;
  }

  public int getPacketsReceived() {
    return packetsReceived;
  }

  public long getDataSent() {
    return dataSent;
  }

  public long getDataReceived() {
    return dataReceived;
  }

  private void updateNetStats() {
    if (activeClient != null) {
      // NOTE: In and Out are reversed for Server stats

      packetsSent += activeClient.getPacketsReceived() - clientLastPacketsReceived;
      packetsReceived += activeClient.getPacketsSent() - clientLastPacketsSent;

      dataSent += activeClient.getDataReceived() - clientLastDataReceived;
      dataReceived += activeClient.getDataSent() - clientLastDataSent;

      clientLastPacketsSent = activeClient.getPacketsSent();
      clientLastPacketsReceived = activeClient.getPacketsReceived();
      clientLastDataSent = activeClient.getDataSent();
      clientLastDataReceived = activeClient.getDataReceived();
    }
  }

  public boolean isBound() {
    return this.server.isBound();
  }

  public boolean isClosed() {
    return this.server.isClosed();
  }
}
