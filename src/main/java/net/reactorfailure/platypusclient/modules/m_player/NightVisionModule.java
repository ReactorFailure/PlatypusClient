package net.reactorfailure.platypusclient.modules.m_player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;
import net.reactorfailure.platypusclient.modules.core.ModuleCategory;

public class NightVisionModule extends AbstractModule {
    private static NightVisionModule INSTANCE;

    public NightVisionModule() {
        super("mod_nightVision", "Night Vision", "Makes you see in dark places", ModuleCategory.PLAYER);
        INSTANCE = this;
    }

    public static NightVisionModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Night Vision enabled");

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
        ClientSide.LOGGER.info("Night Vision disabled");

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
