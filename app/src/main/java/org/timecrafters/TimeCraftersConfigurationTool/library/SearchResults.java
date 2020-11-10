package org.timecrafters.TimeCraftersConfigurationTool.library;

import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;

import java.util.ArrayList;

public class SearchResults {
    public ArrayList<SearchResult> results = new ArrayList<>();

    public ArrayList<SearchResult> groups() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for(SearchResult result : results) {
            if (result.isGroup && !result.isPreset) {
                searchResults.add(result);
            }
        }

        return searchResults;
    }

    public ArrayList<SearchResult> actions() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for(SearchResult result : results) {
            if (result.isAction && !result.isPreset) {
                searchResults.add(result);
            }
        }

        return searchResults;
    }

    public ArrayList<SearchResult> variables() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for(SearchResult result : results) {
            if (result.isVariable && !result.isPreset) {
                searchResults.add(result);
            }
        }

        return searchResults;
    }

    public ArrayList<SearchResult> groupPresets() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for(SearchResult result : results) {
            if (result.isGroup && result.isPreset) {
                searchResults.add(result);
            }
        }

        return searchResults;
    }

    public ArrayList<SearchResult> actionPresets() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for(SearchResult result : results) {
            if (result.isAction && result.isPreset) {
                searchResults.add(result);
            }
        }

        return searchResults;
    }

    public ArrayList<SearchResult> variablesFromPresets() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for(SearchResult result : results) {
            if (result.isVariable && result.isPreset) {
                searchResults.add(result);
            }
        }

        return searchResults;
    }
}
