package net.reactorfailure.platypusclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.Module;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_core.ModuleManager;
import net.reactorfailure.platypusclient.modules.L_utils.ModuleOptions;
import net.reactorfailure.platypusclient.settings.core.AbstractSettings;
import net.reactorfailure.platypusclient.settings.core.Settings;
import net.reactorfailure.platypusclient.settings.core.SettingsManager;

import java.io.*;
import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final File FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("platypusclient.json")
            .toFile();

    public static void save() {
        save(null);
    }

    public static void save(Map<ModuleCategory, Boolean> categoryStates) {
        try {
            JsonObject root = loadOrCreateRoot();

            saveModules(root);
            saveSettings(root);

            if (categoryStates != null) {
                saveCategoryStates(root, categoryStates);
            }

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
            if (root == null) return;

            loadModules(root);
            loadSettings(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Category dropdown states
    public static Map<ModuleCategory, Boolean> loadCategoryStates() {
        if (!FILE.exists()) return null;

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("categoryStates")) return null;

            Map<ModuleCategory, Boolean> map = new EnumMap<>(ModuleCategory.class);
            JsonObject categories = root.getAsJsonObject("categoryStates");

            for (ModuleCategory category : ModuleCategory.values()) {
                if (categories.has(category.name())) {
                    map.put(category, categories.get(category.name()).getAsBoolean());
                }
            }

            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveCategoryStates(JsonObject root, Map<ModuleCategory, Boolean> states) {
        JsonObject categories = new JsonObject();
        for (Map.Entry<ModuleCategory, Boolean> entry : states.entrySet()) {
            categories.addProperty(entry.getKey().name(), entry.getValue());
        }
        root.add("categoryStates", categories);
    }

    //Modules
    private static void saveModules(JsonObject root) {
        JsonObject modules = new JsonObject();

        for (Module module : ModuleManager.all()) {
            if (!(module instanceof AbstractModule abs)) continue;

            JsonObject obj = new JsonObject();
            obj.addProperty("enabled", module.isEnabled());

            Object data = abs.saveToConfig();
            if (data != null) {
                obj.add("data", GSON.toJsonTree(data));
            }

            // Save all registered options
            if (!abs.getOptions().isEmpty()) {
                JsonObject optionsJson = new JsonObject();
                for (ModuleOptions<?> option : abs.getOptions()) {
                    optionsJson.add(option.getId(), GSON.toJsonTree(option.saveToConfig()));
                }
                obj.add("options", optionsJson);
            }

            modules.add(module.getId(), obj);
        }

        root.add("modules", modules);
    }

    private static void loadModules(JsonObject root) {
        if (!root.has("modules")) return;

        JsonObject modules = root.getAsJsonObject("modules");

        for (Module module : ModuleManager.all()) {
            if (!modules.has(module.getId())) continue;

            JsonObject obj = modules.getAsJsonObject(module.getId());

            if (obj.has("enabled")) {
                module.setEnabled(obj.get("enabled").getAsBoolean());
            }

            if (module instanceof AbstractModule abs) {
                if (obj.has("data")) {
                    abs.loadFromConfig(GSON.fromJson(obj.get("data"), Object.class));
                }

                // Load registered options
                if (obj.has("options")) {
                    JsonObject optionsJson = obj.getAsJsonObject("options");
                    for (ModuleOptions<?> option : abs.getOptions()) {
                        if (optionsJson.has(option.getId())) {
                            option.loadFromConfig(
                                    GSON.fromJson(optionsJson.get(option.getId()), Object.class)
                            );
                        }
                    }
                }
            }
        }
    }

    //Settings
    private static void saveSettings(JsonObject root) {
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

    private static void loadSettings(JsonObject root) {
        if (!root.has("settings")) return;

        JsonObject settings = root.getAsJsonObject("settings");

        for (Settings setting : SettingsManager.all()) {
            if (setting instanceof AbstractSettings abs && settings.has(setting.getId())) {
                abs.loadFromConfig(GSON.fromJson(settings.get(setting.getId()), Object.class));
            }
        }
    }

    private static JsonObject loadOrCreateRoot() {
        if (!FILE.exists()) return new JsonObject();

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            return root != null ? root : new JsonObject();
        } catch (Exception e) {
            return new JsonObject();
        }
    }
}