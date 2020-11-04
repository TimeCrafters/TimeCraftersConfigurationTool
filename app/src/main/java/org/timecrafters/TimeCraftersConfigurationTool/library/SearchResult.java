package org.timecrafters.TimeCraftersConfigurationTool.library;

import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;

import java.util.ArrayList;

public class SearchResult {
    public ArrayList<Group> groups = new ArrayList<>();
    public ArrayList<Action> actions = new ArrayList<>();
    public ArrayList<Variable> variables = new ArrayList<>();

    public ArrayList<Group> groupPresets = new ArrayList<>();
    public ArrayList<Action> actionPresets = new ArrayList<>();
    public ArrayList<Variable> variablesFromPresets = new ArrayList<>();
}
