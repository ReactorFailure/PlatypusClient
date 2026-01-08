package net.reactorfailure.platypusclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;
import net.reactorfailure.platypusclient.modules.core.Module;
import net.reactorfailure.platypusclient.modules.core.ModuleManager;
import net.reactorfailure.platypusclient.settings.core.AbstractSettings;
import net.reactorfailure.platypusclient.settings.core.Settings;
import net.reactorfailure.platypusclient.settings.core.SettingsManager;

import java.io.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("platypusclient/config.json")
            .toFile();

    public static void saveModules(JsonObject root) {
        JsonObject modules = new JsonObject();

        for (Module module : ModuleManager.all()) {
            if (module instanceof AbstractModule abs) {
                JsonObject obj = new JsonObject();
                obj.addProperty("enabled", module.isEnabled());

                Object data = abs.saveToConfig();
                if (data != null) {
                    obj.add("data", GSON.toJsonTree(data));
                }

                modules.add(module.getId(), obj);
            }
        }

        root.add("modules", modules);
    }

    public static void loadModules(JsonObject root) {
        if (!root.has("modules")) return;

        JsonObject modules = root.getAsJsonObject("modules");

        for (Module module : ModuleManager.all()) {
            if (!modules.has(module.getId())) continue;

            JsonObject obj = modules.getAsJsonObject(module.getId());

            if (obj.has("enabled")) {
                module.setEnabled(obj.get("enabled").getAsBoolean());
            }

            if (module instanceof AbstractModule abs && obj.has("data")) {
                abs.loadFromConfig(
                        GSON.fromJson(obj.get("data"), Object.class)
                );
            }
        }
    }

    public static void saveSettings(JsonObject root) {
        JsonObject settings = new JsonObject();

        for (Settings setting : SettingsManager.all()) {
            if (setting instanceof AbstractSettings abs) {
                Object value = abs.saveToConfig();
                if (value != null) {
                    settings.add(setting.getId(), GSON.toJsonTree(value));
                }
            }
        }

        root.add("settings", settings);
    }

    public static void loadSettings(JsonObject root) {
        if (!root.has("settings")) return;

        JsonObject settings = root.getAsJsonObject("settings");

        for (Settings setting : SettingsManager.all()) {
            if (setting instanceof AbstractSettings abs) {
                if (settings.has(setting.getId())) {
                    abs.loadFromConfig(
                            GSON.fromJson(settings.get(setting.getId()), Object.class)
                    );
                }
            }
        }
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            saveModules(root);
            saveSettings(root);

            FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(FILE)) {
                GSON.toJson(root, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!FILE.exists()) return;

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);

            loadModules(root);
            loadSettings(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}