package net.reactorfailure.platypusclient.modules.L_core;


import net.reactorfailure.platypusclient.modules.m_persistent.*;
import net.reactorfailure.platypusclient.modules.m_combat.*;
import net.reactorfailure.platypusclient.modules.m_player.*;
import net.reactorfailure.platypusclient.modules.m_misc.*;

public final class ModuleBootstrap {
    public static void init() {
        registerModules(
                new NightVisionModule(),
                new JesusModule(),
                new FlyModule(),
                new SpiderModule(),
                new NoSlowModule(),
                new PersistentSneakModule(),
                new PersistentRunModule(),
                new PersistentHitModule(),
                new PersistentPlaceModule(),
                new AutoEquipTotemModule(),
                new InstantRespawnModule(),
                new StinkAuraModule(),
                new TogglePortalSoundsModule(),
                new FreeCamModule()
        );
    }

    private static void registerModules(Module ... modules) {
        for (Module module : modules) {
            ModuleManager.register(module);
        }
    }
}
