package org.timecrafters.TimeCraftersConfigurationTool.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import org.timecrafters.TimeCraftersConfigurationTool.R;

public class VariableDialog extends Dialog {
    final String TAG = "VariableDialog";
    Button variableType;
    EditText variableValue;
    Switch variableValueBoolean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(false);

        View v = super.onCreateView(inflater, container, savedInstanceState);

        ((TextView)v.findViewById(R.id.dialogTitle)).setText("Add Variable");
        LinearLayout view = v.findViewById(R.id.dialogContent);
        view.addView(getLayoutInflater().inflate(R.layout.dialog_edit_variable, null));
        variableType = view.findViewById(R.id.variableType);
        variableType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showVariableTypeMenu();
            }
        });
        variableType.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                getActivity().getMenuInflater().inflate(R.menu.variable_type_menu, menu);
            }
        });

        variableValue = v.findViewById(R.id.variableValue);
        variableValueBoolean = v.findViewById(R.id.variableValueBoolean);

        setVariableType("Double");

        Button cancelButton = view.findViewById(R.id.cancel);
        Button mutateButton = view.findViewById(R.id.mutate);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mutateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    private void showVariableTypeMenu() {
        Context context = new ContextThemeWrapper(getActivity(), R.style.PopUpMenu);
        PopupMenu menu = new PopupMenu(context, variableType);
        menu.getMenuInflater().inflate(R.menu.variable_type_menu, menu.getMenu());

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.boolean_type: {
                        setVariableType("boolean");
                        return true;
                    }
                    case R.id.double_type: {
                        setVariableType("double");
                        return true;
                    }
                    case R.id.float_type: {
                        setVariableType("float");
                        return true;
                    }
                    case R.id.integer_type: {
                        setVariableType("integer");
                        return true;
                    }
                    case R.id.long_type: {
                        setVariableType("long");
                        return true;
                    }
                    case R.id.string_type: {
                        setVariableType("string");
                        return true;
                    }
                }
                return false;
            }
        });

        menu.show();
    }

    private void setVariableType(String type) {
        String _type = type.toLowerCase();
        if (_type.equals("boolean")) {
            variableType.setText("Boolean");
            variableValue.setVisibility(View.GONE);
            variableValueBoolean.setVisibility(View.VISIBLE);

        } else if (_type.equals("double")) {
            variableType.setText("Double");
            variableValue.setVisibility(View.VISIBLE);
            variableValueBoolean.setVisibility(View.GONE);

            variableValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        } else if (_type.equals("float")) {
            variableType.setText("Float");
            variableValue.setVisibility(View.VISIBLE);
            variableValueBoolean.setVisibility(View.GONE);

            variableValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        } else if (_type.equals("long")){
            variableType.setText("Long");
            variableValue.setVisibility(View.VISIBLE);
            variableValueBoolean.setVisibility(View.GONE);

            variableValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

        } else if (_type.equals("integer")) {
            variableType.setText("Integer");
            variableValue.setVisibility(View.VISIBLE);
            variableValueBoolean.setVisibility(View.GONE);

            variableValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

        } else if (_type.equals("string")) {
            variableType.setText("String");
            variableValue.setVisibility(View.VISIBLE);
            variableValueBoolean.setVisibility(View.GONE);

            variableValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        } else {
            Log.d(TAG, "setVariableType: unknown type \"" + type + "\"");
        }
    }
}
