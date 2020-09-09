package org.timecrafters.TimeCraftersConfigurationTool.backend;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Configuration;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Presets;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.ActionDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.ActionSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.ConfigDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.ConfigSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.ConfigurationDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.ConfigurationSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.GroupDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.GroupSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.PresetsDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.PresetsSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.SettingsDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.SettingsSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.VariableDeserializer;
import org.timecrafters.TimeCraftersConfigurationTool.serializers.VariableSerializer;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.PacketHandler;
import org.timecrafters.TimeCraftersConfigurationTool.tacnet.Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class Backend {
    private static final String TAG = "Backend";
    static private HashMap<String, Object> storage = new HashMap<>();
    static private Backend instance;
    public Context applicationContext;
    private TACNET tacnet;
    private Server server;
    private Exception lastServerError;
    private Config config;
    private Settings settings;
    private boolean configChanged, settingsChanged;
    private MediaPlayer mediaPlayer;

    public static HashMap<String, Object> getStorage() {
        return storage;
    }

    public Backend() {
        if (Backend.instance() != null) {
            throw(new RuntimeException("Backend instance already exists!"));
        } else {
            instance = this;
        }

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

    public Server getServer() {
        return server;
    }

    public void startServer() {
        try {
            server = new Server(settings.port);
            server.start();
        } catch (IOException error) {
            lastServerError = error;
        }
    }

    public void stopServer() {
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (IOException error) {
                lastServerError = error;
            }
        }
    }

    public Exception getLastServerError() {
        return lastServerError;
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

        saveConfig();

        /* Automatically upload whole config to server
        *  TODO: Implement a more atomic remote config updating
        *  */
        if (config != null && tacnet.isConnected()) {
            String json = gsonForConfig().toJson(config);
            tacnet.puts(PacketHandler.packetUploadConfig(config.getName(), json).toString());
        }
    }

    public boolean hasConfigChanged() { return configChanged; }

    public String configPath(String name) {
        return TAC.CONFIGS_PATH + File.separator + name + ".json";
    }

    public void loadConfig(String name) {
        if (name.equals("")) {
            config = null;
            return;
        }

        String path = configPath(name);
        File file = new File(path);

        if (file.exists() && file.isFile()) {
            config = gsonForConfig().fromJson(readFromFile(path), Config.class);
            config.setName(name);
        }
    }

    public Config loadConfigWithoutMutatingBackend(String name) {
        if (name.equals("")) {
            return null;
        }

        String path = configPath(name);
        File file = new File(path);

        if (file.exists() && file.isFile()) {
            Config config = gsonForConfig().fromJson(readFromFile(path), Config.class);
            config.setName(name);

            return config;
        }

        return null;
    }

    public boolean isConfigValid(String json) {
        try {
            gsonForConfig().fromJson(json, Config.class);

            return true;
        } catch (JsonSyntaxException ignored) {
            return false;
        }
    }

    public boolean saveConfig() {
        if (config == null) { return false; }

        final String path = configPath(getConfig().getName());
        configChanged = false;

        return writeToFile(path, gsonForConfig().toJson(config));
    }

    public boolean moveConfig(String oldName, String newName) {
        final String oldPath = configPath(oldName);
        final String newPath = configPath(newName);

        final File oldFile = new File(oldPath);
        final File newFile = new File(newPath);

        if (!oldFile.exists() || !oldFile.isFile()) {
            Log.e(TAG, "moveConfig: Can not move config file \"" + oldPath + "\" does not exists!");
            return false;
        }

        if (newFile.exists() && newFile.isFile()) {
            Log.e(TAG, "moveConfig: Config file \"" + newPath + "\" already exists!");
            return false;
        }

        return oldFile.renameTo(newFile);
    }

    public boolean deleteConfig(String name) {
        File file = new File(configPath(name));

        return file.delete();
    }

    public void writeNewConfig(String name) {
        String path = configPath(name);
        File file = new File(path);

        Config config = new Config(name);

        Gson gson = new Gson();

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gsonForConfig().toJson(config));
            fileWriter.close();
        } catch (IOException error) {
            /* TODO */
            Log.d(TAG, "writeNewConfig: IO Error: " + error.toString());
        }
    }

    public ArrayList<String> configsList() {
        ArrayList<String> list = new ArrayList<>();

        File directory = new File(TAC.CONFIGS_PATH);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        };
        File fileList[] = directory.listFiles(filter);
        for (File file : fileList) {
            list.add(file.getName().replace(".json", ""));
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        return list;
    }

    public Gson gsonForConfig() {
        return new GsonBuilder()
                .registerTypeAdapter(Config.class, new ConfigSerializer())
                .registerTypeAdapter(Config.class, new ConfigDeserializer())

                .registerTypeAdapter(Configuration.class, new ConfigurationSerializer())
                .registerTypeAdapter(Configuration.class, new ConfigurationDeserializer())

                .registerTypeAdapter(Group.class, new GroupSerializer())
                .registerTypeAdapter(Group.class, new GroupDeserializer())

                .registerTypeAdapter(Action.class, new ActionSerializer())
                .registerTypeAdapter(Action.class, new ActionDeserializer())

                .registerTypeAdapter(Variable.class, new VariableSerializer())
                .registerTypeAdapter(Variable.class, new VariableDeserializer())

                .registerTypeAdapter(Presets.class, new PresetsSerializer())
                .registerTypeAdapter(Presets.class, new PresetsDeserializer())
                .create();
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

    public Gson gsonForSettings() {
        return new GsonBuilder()
                .registerTypeAdapter(Settings.class, new SettingsSerializer())
                .registerTypeAdapter(Settings.class, new SettingsDeserializer())
                .create();
    }

    public String readFromFile(String path) {
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

    public boolean writeToFile(String filePath, String content) {
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

    public void startErrorSound(Context context) {
        if (isPlayingErrorSound()) {
            return;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.error_alarm));

            if (Build.VERSION.SDK_INT >= 21) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            }

            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayingErrorSound() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void stopErrorSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
