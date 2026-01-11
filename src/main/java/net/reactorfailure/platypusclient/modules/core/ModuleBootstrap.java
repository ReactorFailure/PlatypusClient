package net.reactorfailure.platypusclient.modules.core;

import net.reactorfailure.platypusclient.modules.m_combat.InstantRespawnModule;
import net.reactorfailure.platypusclient.modules.m_combat.StinkAuraModule;
import net.reactorfailure.platypusclient.modules.m_persistent.PersistentHitModule;
import net.reactorfailure.platypusclient.modules.m_persistent.PersistentRunModule;
import net.reactorfailure.platypusclient.modules.m_persistent.PersistentSneakModule;
import net.reactorfailure.platypusclient.modules.m_player.NightVisionModule;

public final class ModuleBootstrap {
    public static void init() {
        registerModules(
                new NightVisionModule(),
                new PersistentSneakModule(),
                new PersistentRunModule(),
                new PersistentHitModule(),
                new InstantRespawnModule(),
                new StinkAuraModule()
        );
    }

    private static void registerModules(Module ... modules) {
        for (Module module : modules) {
            ModuleManager.register(module);
        }
    }
}
