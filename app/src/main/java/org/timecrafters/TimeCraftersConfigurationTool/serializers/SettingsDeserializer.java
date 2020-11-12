package org.timecrafters.TimeCraftersConfigurationTool.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.timecrafters.TimeCraftersConfigurationTool.backend.Settings;

import java.lang.reflect.Type;

public class SettingsDeserializer implements JsonDeserializer<Settings> {
    @Override
    public Settings deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject data = jsonObject.get("data").getAsJsonObject();

        /* Upgrade Settings */
        if (data.get("mobile_show_navigation_labels") == null) {
            data.addProperty("mobile_show_navigation_labels", false);
        }
        if (data.get("mobile_disable_launcher_delay") == null) {
            data.addProperty("mobile_disable_launcher_delay", false);
        }
        if (data.get("mobile_start_server_at_boot") == null) {
            data.addProperty("mobile_start_server_at_boot", false);
        }

        return new Settings(
                    data.get("hostname").getAsString(),
                    data.get("port").getAsInt(),
                    data.get("config").getAsString(),

                    data.get("mobile_show_navigation_labels").getAsBoolean(),
                    data.get("mobile_disable_launcher_delay").getAsBoolean(),
                    data.get("mobile_start_server_at_boot").getAsBoolean()
                );
    }
}
