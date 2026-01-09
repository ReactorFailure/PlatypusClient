package net.reactorfailure.platypusclient.modules.core;

import net.reactorfailure.platypusclient.modules.*;

public final class ModuleBootstrap {
    public static void init() {
        registerModules(
                new NightVisionModule(),
                new PersistentSneakModule(),
                new PersistentRunModule(),
                new PersistentHitModule(),
                new InstantRespawnModule()
        );
    }

    private static void registerModules(Module ... modules) {
        for (Module module : modules) {
            ModuleManager.register(module);
        }
    }
}
