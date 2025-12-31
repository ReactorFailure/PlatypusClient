package net.reactorfailure.platypusclient.modules;

public final class ModuleBootstrap {
    public static void init() {
        NightVisionModule.get();
        PersistentSneakModule.get();
    }
}
