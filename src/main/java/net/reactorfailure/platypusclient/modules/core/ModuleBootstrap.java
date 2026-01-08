package net.reactorfailure.platypusclient.modules.core;

import net.reactorfailure.platypusclient.modules.NightVisionModule;
import net.reactorfailure.platypusclient.modules.PersistentSneakModule;

public final class ModuleBootstrap {
    public static void init() {
        ModuleManager.register(new NightVisionModule());
        ModuleManager.register(new PersistentSneakModule());
    }
}
