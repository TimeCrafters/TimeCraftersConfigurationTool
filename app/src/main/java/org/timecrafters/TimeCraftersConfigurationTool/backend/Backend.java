package org.timecrafters.TimeCraftersConfigurationTool.backend;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import org.timecrafters.TimeCraftersConfigurationTool.serializers.SettingsDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.SettingsSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Backend {
    private static final String TAG = "Backend";
    static private Backend instance;
    private TACNET tacnet;
    private Config config;
    private Settings settings;
    private boolean configChanged, settingsChanged;

    public Backend() {
        instance = this;

        loadSettings();
        if (!settings.config.isEmpty()) {
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

    public boolean haveSettingsChanged() {
        return settingsChanged;
    }

    public void loadSettings() {
        File settingsFile = new File(TAC.SETTINGS_PATH);

        if (!settingsFile.exists()) {
            Log.i(TAG, "Writing default settings.json");
            writeDefaultSettings();
        }

        try {
            settings = gsonForSettings().fromJson(new FileReader(settingsFile), Settings.class);
        } catch (FileNotFoundException e) {
            // TODO
            Log.e(TAG, "Unable to load settings.json");
        }
    }

    public void saveSettings() {
        Log.i(TAG, "Settings: " + gsonForSettings().toJson(settings));
        writeToFile(TAC.SETTINGS_PATH, gsonForSettings().toJson(settings));
    }

    public void writeDefaultSettings() {
        settings = new Settings(TACNET.DEFAULT_HOSTNAME, TACNET.DEFAULT_PORT, "");
        saveSettings();
    }

    private Gson gsonForSettings() {
        return new GsonBuilder()
                .registerTypeAdapter(Settings.class, new SettingsSerializer())
                .registerTypeAdapter(Settings.class, new SettingsDeserializer())
                .create();
    }

    private String readFromFile(String path) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader( new FileReader(path) );
            String line;

            while((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }

            br.close();
        } catch (IOException e) {
            // TODO
        }

        return text.toString();
    }

    protected boolean writeToFile(String filePath, String content) {
        try {
            if (filePath.startsWith(TAC.ROOT_PATH)) {
                createFolders(filePath);

                FileWriter writer = new FileWriter(filePath);
                writer.write(content);
                writer.close();

                return true;
            } else {
                Log.e(TAG, "writeToFile disallowed path: " + filePath);
                return false;
            }

        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    private void createFolders(String filePath) throws IOException {
        File rootPath = new File(TAC.ROOT_PATH);
        File configsPath = new File(TAC.CONFIGS_PATH);

        if (!rootPath.exists()) {
            rootPath.mkdir();
        }

        if (!configsPath.exists()) {
            configsPath.mkdir();
        }
    }
}
