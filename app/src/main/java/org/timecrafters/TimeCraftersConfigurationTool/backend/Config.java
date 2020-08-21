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

    // DELETE ME
    public Config() {
        this.name = "DEBUG ONLY";
        this.configuration = new Configuration(new Date(), new Date(), 0, 32);
        groups = new ArrayList<>();
        presets = new ArrayList<>();

        ArrayList<Action> actions = new ArrayList<>();
        ArrayList<Variable> variables = new ArrayList<>();
        variables.add(new Variable("VarName", "Dx90.45"));
        variables.add(new Variable("VarName2", "Fx90.45"));
        variables.add(new Variable("VarName3", "Dx90.45"));
        variables.add(new Variable("distance", "Ix90"));
        variables.add(new Variable("variable", "Dx90.45"));
        variables.add(new Variable("tea_time", "SxThe Tea Party was quite enjoyable."));
        variables.add(new Variable("tea_time", "SxThe x Tea x Party was x quite x enjoyable."));

        actions.add(new Action("DriverOne", "This is a comment", true, variables));
        actions.add(new Action("DriverTwo", "", true, variables));
        actions.add(new Action("DriverAlt", "This is also is a comment", true, variables));
        actions.add(new Action("DriverOne", "This is a comment", true, variables));
        actions.add(new Action("DriverTwo", "", true, variables));
        actions.add(new Action("DriverAlt", "This is also is a comment", true, variables));
        actions.add(new Action("DriverOne", "This is a comment", true, variables));
        actions.add(new Action("DriverTwo", "", true, variables));
        actions.add(new Action("DriverAlt", "This is also is a comment", true, variables));
        actions.add(new Action("DriverOne", "This is a comment", true, variables));
        actions.add(new Action("DriverTwo", "", true, variables));
        actions.add(new Action("DriverAlt", "This is also is a comment", true, variables));

        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
        groups.add(new Group("TeleOp", actions));
        groups.add(new Group("BlueLeftA", actions));
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
