package net.reactorfailure.platypusclient.modules.L_core;

import java.util.*;

public class ModuleManager {
    private static final Map<Class<? extends Module>, Module> MODULES = new LinkedHashMap<>();

    public static <T extends Module> void register(T module) {
        MODULES.put(module.getClass(), module);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> clazz) {
        return (T) MODULES.get(clazz);
    }

    public static boolean anyEnabled() {
        for (Module module : MODULES.values()) {
            if (module.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public static Collection<Module> all() {
        return MODULES.values();
    }

    public static Map<ModuleCategory, List<Module>> getByCategory() {
        Map<ModuleCategory, List<Module>> categorized = new LinkedHashMap<>();

        for (ModuleCategory category : ModuleCategory.values()) {
            categorized.put(category, new ArrayList<>());
        }

        for (Module module : MODULES.values()) {
            categorized.get(module.getCategoryName()).add(module);
        }

        return categorized;
    }

    public static void tickAll() {
        for (Module module : MODULES.values()) {
            if (module.isEnabled()) {
                module.tick();
            }
        }
    }

    public static void disableAll() {
        for (Module module : MODULES.values()) {
            if (module.isEnabled()) {
                module.setEnabled(false);
                module.onDisable();
            }
        }
    }
}
