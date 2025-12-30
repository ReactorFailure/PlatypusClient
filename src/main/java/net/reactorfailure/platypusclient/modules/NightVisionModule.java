package net.reactorfailure.platypusclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class NightVisionModule implements Module {
    private static final NightVisionModule INSTANCE = new NightVisionModule();
    private static boolean enabled;

    private NightVisionModule() {}

    public static NightVisionModule get() {
        return INSTANCE;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean value) {
        enabled = value;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (enabled) {
            client.player.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.NIGHT_VISION,
                            20 * 60 * 60,
                            0,
                            false,
                            false,
                            false
                    )
            );
        } else {
            client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
