package net.reactorfailure.platypusclient.modules.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModuleManager {
    private static final Map<Class<? extends net.reactorfailure.platypusclient.modules.core.Module>, net.reactorfailure.platypusclient.modules.core.Module> MODULES = new LinkedHashMap<>();

    public static <T extends net.reactorfailure.platypusclient.modules.core.Module> void register(T module) {
        MODULES.put(module.getClass(), module);
    }

    @SuppressWarnings("unchecked")
    public static <T extends net.reactorfailure.platypusclient.modules.core.Module> T get(Class<T> clazz) {
        return (T) MODULES.get(clazz);
    }

    public static boolean anyEnabled() {
        for (net.reactorfailure.platypusclient.modules.core.Module module : MODULES.values()) {
            if (module.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public static Collection<net.reactorfailure.platypusclient.modules.core.Module> all() {
        return MODULES.values();
    }

    public static void tickAll() {
        for (net.reactorfailure.platypusclient.modules.core.Module module : MODULES.values()) {
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
