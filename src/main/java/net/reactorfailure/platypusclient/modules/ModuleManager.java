package net.reactorfailure.platypusclient.modules;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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
}
