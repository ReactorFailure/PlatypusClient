package net.reactorfailure.platypusclient.modules;

public final class ModuleBootstrap {
    public static void init() {
        ModuleManager.register(new NightVisionModule());
        ModuleManager.register(new PersistentSneakModule());
    }
}
