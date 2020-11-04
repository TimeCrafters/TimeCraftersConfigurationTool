package org.timecrafters.TimeCraftersConfigurationTool.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.library.SearchResult;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class SearchFragment extends TimeCraftersFragment {
    final static String TAG = "SearchFragment";

    Config config;

    LinearLayout searchResultsContainer;
    EditText searchQuery;
    ImageButton searchButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.config = Backend.instance().getConfig();

        View root = inflater.inflate(R.layout.fragment_search, container, false);
        this.searchQuery = root.findViewById(R.id.search_query);
        this.searchButton = root.findViewById(R.id.search);
        this.searchResultsContainer = root.findViewById(R.id.search_results);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Backend.instance().getConfig() != null) {
                    performSearch();
                }
            }
        });

        return root;
    }

    private void performSearch() {
        final String query = searchQuery.getText().toString().toLowerCase();

        if (query.length() == 0) {
            searchResultsContainer.removeAllViews();
            return;
        }

        SearchResult searchResult = search(query);

        showSearchResults(searchResult);
    }

    private void showSearchResults(SearchResult searchResult) {
        searchResultsContainer.removeAllViews();
        int i = 0;

        // GROUPS
        if (searchResult.groups.size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText(R.string.groups);
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (Group group : searchResult.groups) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(group.name);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // ACTIONS
        if (searchResult.actions.size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText(R.string.actions);
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (Action action : searchResult.actions) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(action.name);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // VARIABLES
        if (searchResult.variables.size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText(R.string.variables);
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (Variable variable : searchResult.variables) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(variable.name + " [" + variable.value().toString() + "]");

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // PRESET GROUPS
        if (searchResult.groupPresets.size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText("Presets - Groups");
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (Group group : searchResult.groupPresets) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(group.name);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // PRESET ACTIONS
        if (searchResult.actions.size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText("Presets - Actions");
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (Action action : searchResult.actionPresets) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(action.name);

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // VARIABLES FROM PRESETS
        if (searchResult.variablesFromPresets.size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText("Presets - Variables");
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (Variable variable : searchResult.variablesFromPresets) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(variable.name + " [" + variable.value().toString() + "]");

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }
    }

    private SearchResult search(String query) {
        SearchResult searchResult = new SearchResult();

        searchGroups(query, searchResult);
        searchActions(query, searchResult);
        searchVariables(query, searchResult);
        searchPresets(query, searchResult);

        return searchResult;
    }

    private void searchGroups(String query, SearchResult searchResult) {
        for (Group group : config.getGroups()) {
            if (group.name.toLowerCase().contains(query)) {
                searchResult.groups.add(group);
            }
        }
    }

    private void searchActions(String query, SearchResult searchResult) {
        for (Group group : config.getGroups()) {
            for (Action action : group.getActions()) {
                if (action.name.toLowerCase().contains(query)) {
                    searchResult.actions.add(action);
                }
            }
        }
    }

    private void searchVariables(String query, SearchResult searchResult) {
        for (Group group : config.getGroups()) {
            for (Action action : group.getActions()) {
                for (Variable variable : action.getVariables()) {
                    if (variable.name.toLowerCase().contains(query)) {
                        searchResult.variables.add(variable);
                    }
                    if (variable.value().toString().toLowerCase().contains(query)) {
                        searchResult.variables.add(variable);
                    }
                }
            }
        }
    }

    private void searchPresets(String query, SearchResult searchResult) {
        for (Group group : config.getPresets().getGroups()) {
            if (group.name.toLowerCase().contains(query)) {
                searchResult.groupPresets.add(group);
            }
        }

        for (Action action : config.getPresets().getActions()) {
            if (action.name.toLowerCase().contains(query)) {
                searchResult.actionPresets.add(action);
            }
        }

        for (Group group : config.getPresets().getGroups()) {
            for (Action action : group.getActions()) {
                for (Variable variable : action.getVariables()) {
                    if (variable.name.toLowerCase().contains(query)) {
                        searchResult.variablesFromPresets.add(variable);
                    }
                    if (variable.value().toString().toLowerCase().contains(query)) {
                        searchResult.variablesFromPresets.add(variable);
                    }
                }
            }
        }

        for (Action action : config.getPresets().getActions()) {
            for (Variable variable : action.getVariables()) {
                if (variable.name.toLowerCase().contains(query)) {
                    searchResult.variablesFromPresets.add(variable);
                }
                if (variable.value().toString().toLowerCase().contains(query)) {
                    searchResult.variablesFromPresets.add(variable);
                }
            }
        }
    }
}