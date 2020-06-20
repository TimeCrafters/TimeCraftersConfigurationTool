package org.timecrafters.TimeCraftersConfigurationTool.backend;

import org.timecrafters.TimeCraftersConfigurationTool.tacnet.PacketHandler;

import java.io.File;
import java.util.Date;

public class Backend {
    static private Backend instance;
    private TACNET tacnet;
    private Config config;
    private Settings settings;
    private boolean configChanged, settingsChanged;

    public Backend() {
        instance = this;

        loadSettings();
        if (settings.config != null) {
            loadConfig(settings.config);
        }
        tacnet = new TACNET();

        configChanged = false;
        settingsChanged = false;
    }

    static public Backend instance() {
        return instance;
    }

    public TACNET tacnet() {
        return tacnet;
    }

    public Config getConfig() {
        return config;
    }

    public Settings getSettings() {
        return settings;
    }

    public void configChanged() {
        config.getConfiguration().updatedAt = new Date();
        config.getConfiguration().revision += 1;
        configChanged = true;
    }

    public boolean isConfigChanged() { return configChanged; }

    public void loadConfig(String name) {
        File file = new File("" + TAC.CONFIGS_PATH + File.separator + name);

        if (file.exists() && file.isFile()) {
            // TODO: Load configuration
        }
    }

    public boolean saveConfig(String name) {
        // TODO: Implement save config
        configChanged = false;
        return  false;
    }

    public void uploadConfig() {
        if (config != null && tacnet.isConnected()) {
            String json = "";
//            tacnet.puts(PacketHandler.packetUploadConfig(json));
        }
    }

    public void downloadConfig() {
        if (config != null && tacnet.isConnected()) {
//            tacnet.puts(PacketHandler.packetDownloadConfig());
        }
    }

    public void writeNewConfig(String name) {
        // TODO: Implement
    }

    public void settingsChanged() {
        settingsChanged = true;
    }

    public boolean isSettingsChanged() {
        return settingsChanged;
    }

    public boolean loadSettings() {
        return false;
    }

    public void saveSettings() {

    }

    public void writeDefaultSettings() {
        /*
        {
            "data":
                {
                    "hostname":TACNET.DEFAULT_HOSTNAME,
                    "port":TACNET.DEFAULT_PORT,
                    "config":null,
                }
        }
         */
    }
}
