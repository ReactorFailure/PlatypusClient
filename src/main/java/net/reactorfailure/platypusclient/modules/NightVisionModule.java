package net.reactorfailure.platypusclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class NightVisionModule extends AbstractModule {
    private static NightVisionModule INSTANCE;

    NightVisionModule() {
        super("mod_nv", "Night Vision");
        INSTANCE = this;
    }

    public static NightVisionModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.NIGHT_VISION,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false,
                        false
                )
        );
    }

    @Override
    public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null) return;

        if (!client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            onEnable();
        }
    }
}
