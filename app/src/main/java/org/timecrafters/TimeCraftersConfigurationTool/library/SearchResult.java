package org.timecrafters.TimeCraftersConfigurationTool.library;

import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;

public class SearchResult {
    public Group group;
    public Action action;
    public Variable variable;

    public boolean isGroup, isAction, isVariable;
    public boolean isFromName, isFromValue, isFromComment;
    public boolean isPreset;

    public String query;

    public SearchResult(Group group, String query, boolean isPreset) {
        this.group = group;
        this.isGroup = true;
        this.isFromName = true;
        this.query = query;

        this.isPreset = isPreset;
    }

    public SearchResult(Group group, Action action, String query, boolean isFromComment, boolean isPreset) {
        this.group = group;
        this.action = action;
        this.isAction = true;
        this.query = query;

        if (isFromComment) {
            this.isFromComment = true;
        } else {
            this.isFromName = true;
        }

        this.isPreset = isPreset;
    }

    public SearchResult(Group group, Action action, Variable variable, String query, boolean isFromValue, boolean isPreset) {
        this.group = group;
        this.action = action;
        this.variable = variable;
        this.isVariable = true;
        this.query = query;

        if (isFromValue) {
            this.isFromValue = true;
        } else {
            this.isFromName = true;
        }

        this.isPreset = isPreset;
    }
}
