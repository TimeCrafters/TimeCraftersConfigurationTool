package org.timecrafters.TimeCraftersConfigurationTool.backend;

import com.google.gson.Gson;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        variables.add(new Variable("VarName2", "FxFx90.45"));
        variables.add(new Variable("VarName3", "Dx90.45"));
        variables.add(new Variable("distance", "Ix90"));
        variables.add(new Variable("variable", "Dx90.45"));
        variables.add(new Variable("tea_time", "SxThe Tea Party was quite enjoyable."));

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

    public class Configuration {
        public Date createdAt, updatedAt;
        private int specVersion;
        public int revision;

        public Configuration(Date createdAt, Date updatedAt, int specVersion, int revision) {
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.specVersion = specVersion;
            this.revision = revision;
        }

        public int getSpecVersion() { return specVersion; }
    }

    public class Preset {
        private ArrayList<Group> groups;
        private ArrayList<Action> actions;

        public Preset(ArrayList<Group> groups, ArrayList<Action> actions) {
            this.groups = groups;
            this.actions = actions;
        }
    }

    public class Group {
        public String name;
        private ArrayList<Action> actions;

        public Group(String name, ArrayList<Action> actions) {
            this.name = name;
            this.actions = actions;
        }

        public ArrayList<Action> getActions() {
            return actions;
        }
    }

    public class Action {
        public String name, comment;
        public boolean enabled;
        private ArrayList<Variable> variables;

        public Action(String name, String comment, boolean enabled, ArrayList<Variable> variables) {
            this.name = name;
            this.comment = comment;
            this.enabled = enabled;
            this.variables = variables;
        }

        public ArrayList<Variable> getVariables() { return variables; }
    }

    public class Variable {
        public String name;
        private String value;

        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public <T> T value() {
            return valueOf();
        }

        @SuppressWarnings("unchecked")
        public <T> T valueOf() {
            String[] split = value.split("x");

            switch (split[0]) {
                case "B": {
                    return (T) Boolean.valueOf(split[(split.length-1)]);
                }
                case "D": {
                    return (T) Double.valueOf(split[(split.length-1)]);
                }
                case "F": {
                    return (T) Float.valueOf(split[(split.length-1)]);
                }
                case "I": {
                    return (T) Integer.valueOf(split[(split.length-1)]);
                }
                case "L": {
                    return (T) Long.valueOf(split[(split.length-1)]);
                }
                case "S": {
                    return (T) String.valueOf(split[(split.length-1)]);
                }
                default: {
                    return null;
                }
            }
        }

        public String typeOf(String value) {
            String[] split = value.split("x");

            switch (split[0]) {
                case "B": {
                    return "Boolean";
                }
                case "D": {
                    return "Double";
                }
                case "F": {
                    return "Float";
                }
                case "I": {
                    return "Integer";
                }
                case "L": {
                    return "Long";
                }
                case "S": {
                    return "String";
                }
                default: {
                    return "=!UNKNOWN!=";
                }
            }
        }
    }
}
