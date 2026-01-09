package net.reactorfailure.platypusclient.settings.core;

import java.util.*;

public class SettingsManager {
    private static final Map<String, Settings> SETTINGS = new LinkedHashMap<>();

    public static void register(Settings setting) {
        SETTINGS.put(setting.getId(), setting);
    }

    public static List<Settings> all() {
        return new ArrayList<>(SETTINGS.values());
    }
}
