package org.timecrafters.TimeCraftersConfigurationTool.backend;

import com.google.gson.Gson;

import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Configuration;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Preset;

import java.util.ArrayList;
import java.util.Date;

public class Config {
    private String name;
    private Configuration configuration;
    private ArrayList<Group> groups;
    private ArrayList<Preset> presets;

    public Config(String name) {
        this.name = name;
        this.configuration = new Configuration(new Date(), new Date(), TAC.CONFIG_SPEC_VERSION, 32);
        groups = new ArrayList<>();
        presets = new ArrayList<>();
    }


    public Config(String name, String path) {
        this.name = name;
        parse(path);
    }

    public Config(Configuration configuration, ArrayList<Group> groups, ArrayList<Preset> presets) {
        this.configuration = configuration;
        this.groups = groups;
        this.presets = presets;
    }

    public String getName() { return name; }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ArrayList<Preset> getPresets() {
        return presets;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    private void parse(String path) {
        Gson gson = new Gson();
        String json = Backend.instance().readFromFile(path);
        Config config = gson.fromJson(json, Config.class);

        this.configuration = config.configuration;
        this.groups = config.groups;
        this.presets = config.presets;
    }
}
