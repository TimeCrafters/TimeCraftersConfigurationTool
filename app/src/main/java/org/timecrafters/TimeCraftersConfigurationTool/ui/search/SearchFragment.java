package org.timecrafters.TimeCraftersConfigurationTool.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import org.timecrafters.TimeCraftersConfigurationTool.R;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Backend;
import org.timecrafters.TimeCraftersConfigurationTool.backend.Config;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Action;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Group;
import org.timecrafters.TimeCraftersConfigurationTool.backend.config.Variable;
import org.timecrafters.TimeCraftersConfigurationTool.library.SearchResult;
import org.timecrafters.TimeCraftersConfigurationTool.library.SearchResults;
import org.timecrafters.TimeCraftersConfigurationTool.library.TimeCraftersFragment;

public class SearchFragment extends TimeCraftersFragment {
    final static String TAG = "SearchFragment";

    Config config;

    LinearLayout searchResultsContainer;
    EditText searchQuery;
    ImageButton searchButton;
    String query;

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
                    getArguments().putString("query", query);
                }
            }
        });

        if (getArguments() == null) {
            setArguments(new Bundle());
        }

        // Restore search results when returning from navigating from a search result
        if (getArguments().getString("query", null) != null) {
            searchQuery.setText(getArguments().getString("query"));
            searchButton.callOnClick();
        }

        return root;
    }

    private void performSearch() {
        query = searchQuery.getText().toString().toLowerCase();

        if (query.length() == 0) {
            searchResultsContainer.removeAllViews();
            return;
        }

        SearchResults searchResults = search(query);

        showSearchResults(searchResults);
    }

    private void showSearchResults(SearchResults searchResults) {
        searchResultsContainer.removeAllViews();
        int i = 0;

        // GROUPS
        if (searchResults.groups().size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText(R.string.groups);
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (SearchResult result : searchResults.groups()) {
            final Group group = result.group;
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(group.name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", config.getGroups().indexOf(group));
                    bundle.putBoolean("is_search", true);
                    Navigation.findNavController(v).navigate(R.id.navigation_editor, bundle);
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // ACTIONS
        if (searchResults.actions().size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText(R.string.actions);
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (final SearchResult result : searchResults.actions()) {
            final Action action = result.action;
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(action.name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", config.getGroups().indexOf(result.group));
                    bundle.putInt("action_index", result.group.getActions().indexOf(result.action));
                    bundle.putBoolean("is_search", true);
                    Navigation.findNavController(v).navigate(R.id.variables_fragment, bundle);
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // VARIABLES
        if (searchResults.variables().size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText(R.string.variables);
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (final SearchResult result : searchResults.variables()) {
            final Variable variable = result.variable;
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(variable.name + " [" + variable.value().toString() + "]");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", config.getGroups().indexOf(result.group));
                    bundle.putInt("action_index", result.group.getActions().indexOf(result.action));
                    bundle.putInt("variable_index", result.action.getVariables().indexOf(result.variable));
                    bundle.putBoolean("is_search", true);
                    Navigation.findNavController(v).navigate(R.id.variables_fragment, bundle);
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // PRESET GROUPS
        if (searchResults.groupPresets().size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText("Presets - Groups");
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (final SearchResult result : searchResults.groupPresets()) {
            final Group group = result.group;
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(group.name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("group_index", config.getPresets().getGroups().indexOf(group));
                    bundle.putBoolean("group_is_preset", true);
                    bundle.putBoolean("is_search", true);
                    Navigation.findNavController(v).navigate(R.id.actions_fragment, bundle);
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // PRESET ACTIONS
        if (searchResults.actionPresets().size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText("Presets - Actions");
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (final SearchResult result : searchResults.actionPresets()) {
            final Action action = result.action;
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(action.name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (result.group != null) {
                        bundle.putInt("group_index", config.getPresets().getGroups().indexOf(result.group));
                        bundle.putInt("action_index", result.group.getActions().indexOf(action));
                        bundle.putBoolean("group_is_preset", true);
                    } else {
                        bundle.putInt("action_index", config.getPresets().getActions().indexOf(action));
                        bundle.putBoolean("action_is_preset", true);
                    }
                    bundle.putBoolean("is_search", true);
                    Navigation.findNavController(v).navigate(R.id.variables_fragment, bundle);
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }

        // VARIABLES FROM PRESETS
        if (searchResults.variablesFromPresets().size() > 0) {
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result_header, null);
            final TextView section = view.findViewById(R.id.section);

            section.setText("Presets - Variables");
            searchResultsContainer.addView(view);
        }

        i = 0;
        for (final SearchResult result : searchResults.variablesFromPresets()) {
            final Variable variable = result.variable;
            final View view = View.inflate(getContext(), R.layout.fragment_part_search_result, null);
            final Button button = view.findViewById(R.id.name);
            button.setText(variable.name + " [" + variable.value().toString() + "]");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (result.group != null) {
                        bundle.putInt("group_index", config.getPresets().getGroups().indexOf(result.group));
                        bundle.putInt("action_index", result.group.getActions().indexOf(result.action));
                        bundle.putInt("variable_index", result.action.getVariables().indexOf(variable));
                        bundle.putBoolean("group_is_preset", true);
                    } else {
                        bundle.putInt("action_index", config.getPresets().getActions().indexOf(result.action));
                        bundle.putInt("variable_index", result.action.getVariables().indexOf(variable));
                        bundle.putBoolean("action_is_preset", true);
                    }
                    bundle.putBoolean("is_search", true);
                    Navigation.findNavController(v).navigate(R.id.variables_fragment, bundle);
                }
            });

            if (i % 2 == 0) { // even
                view.setBackgroundColor(getResources().getColor(R.color.list_even));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.list_odd));
            }

            searchResultsContainer.addView(view);
            i++;
        }
    }

    private SearchResults search(String query) {
        SearchResults searchResults = new SearchResults();

        searchGroups(query, searchResults);
        searchActions(query, searchResults);
        searchVariables(query, searchResults);
        searchPresets(query, searchResults);

        return searchResults;
    }

    private void searchGroups(String query, SearchResults searchResults) {
        for (Group group : config.getGroups()) {
            if (group.name.toLowerCase().contains(query)) {
                SearchResult result = new SearchResult(group, query, false);
                searchResults.results.add(result);
            }
        }
    }

    private void searchActions(String query, SearchResults searchResults) {
        for (Group group : config.getGroups()) {
            for (Action action : group.getActions()) {
                if (action.name.toLowerCase().contains(query)) {
                    SearchResult result = new SearchResult(group, action, query, false, false);
                    searchResults.results.add(result);
                }
            }
        }
    }

    private void searchVariables(String query, SearchResults searchResults) {
        for (Group group : config.getGroups()) {
            for (Action action : group.getActions()) {
                for (Variable variable : action.getVariables()) {
                    if (variable.name.toLowerCase().contains(query)) {
                        SearchResult result = new SearchResult(group, action, variable, query, false, false);
                        searchResults.results.add(result);
                    }
                    if (variable.value().toString().toLowerCase().contains(query)) {
                        SearchResult result = new SearchResult(group, action, variable, query, true, false);
                        searchResults.results.add(result);
                    }
                }
            }
        }
    }

    private void searchPresets(String query, SearchResults searchResults) {
        for (Group group : config.getPresets().getGroups()) {
            if (group.name.toLowerCase().contains(query)) {
                SearchResult result = new SearchResult(group, query, true);
                searchResults.results.add(result);
            }

            for (Action action : group.getActions()) {
                if (action.name.toLowerCase().contains(query)) {
                    SearchResult result = new SearchResult(group, action, query, false, true);
                    searchResults.results.add(result);
                }
            }
        }

        for (Action action : config.getPresets().getActions()) {
            if (action.name.toLowerCase().contains(query)) {
                SearchResult result = new SearchResult(null, action, query, false, true);
                searchResults.results.add(result);
            }
        }

        for (Group group : config.getPresets().getGroups()) {
            for (Action action : group.getActions()) {
                for (Variable variable : action.getVariables()) {
                    if (variable.name.toLowerCase().contains(query)) {
                        SearchResult result = new SearchResult(group, action, variable, query, false, true);
                        searchResults.results.add(result);
                    }
                    if (variable.value().toString().toLowerCase().contains(query)) {
                        SearchResult result = new SearchResult(group, action, variable, query, true, true);
                        searchResults.results.add(result);
                    }
                }
            }
        }

        for (Action action : config.getPresets().getActions()) {
            for (Variable variable : action.getVariables()) {
                if (variable.name.toLowerCase().contains(query)) {
                    SearchResult result = new SearchResult(null, action, variable, query, false, true);
                    searchResults.results.add(result);
                }
                if (variable.value().toString().toLowerCase().contains(query)) {
                    SearchResult result = new SearchResult(null, action, variable, query, true, true);
                    searchResults.results.add(result);
                }
            }
        }
    }
}