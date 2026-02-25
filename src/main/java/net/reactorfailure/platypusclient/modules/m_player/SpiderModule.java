package net.reactorfailure.platypusclient.modules.m_player;

import net.minecraft.client.MinecraftClient;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_utils.SliderOption;

public class SpiderModule extends AbstractModule {
    private static SpiderModule INSTANCE;

    private double climbSpeed = 0.2;

    private final SliderOption optSpeed = new SliderOption(
            "climbSpeed", "Climb Speed",
            0.1, 0.6, 0.2,
            0.05,
            "Slow", "Fast",
            v -> this.climbSpeed = v
    );

    public SpiderModule() {
        super("mod_spider", "Spider", "Lets you climb any surface", ModuleCategory.PLAYER);
        INSTANCE = this;
        addOption(optSpeed);
    }

    public static SpiderModule get() { return INSTANCE; }

    public static boolean isActive() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

    public static double getClimbSpeed() {
        return INSTANCE != null ? INSTANCE.climbSpeed : 0.2;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Spider enabled – climb speed: {}", climbSpeed);
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Spider disabled");

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.setVelocity(
                    client.player.getVelocity().x,
                    0.0,
                    client.player.getVelocity().z
            );
        }
    }
}
