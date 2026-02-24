package net.reactorfailure.platypusclient.modules.m_persistent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_utils.SliderOption;

public class PersistentPlaceModule extends AbstractModule {
    private static PersistentPlaceModule INSTANCE;

    private int placeDelay  = 4;
    private int tickCounter = 0;

    private final SliderOption optSpeed = new SliderOption(
            "placeDelay", "Place Speed",
            1, 10, 4,
            1,
            "Very Fast", "Normal",
            v -> this.placeDelay = v.intValue()
    );

    public PersistentPlaceModule() {
        super("mod_persistPlace", "Persistent Place", "Automatically places blocks continuously", ModuleCategory.PERSISTENT);
        INSTANCE = this;
        addOption(optSpeed);
    }

    public static PersistentPlaceModule get() { return INSTANCE; }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Persistent Place enabled – {} placements/s", getPlacementsPerSecond());
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Persistent Place disabled");
        tickCounter = 0;
    }

    @Override
    public void tick() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!isEnabled() || client.player == null || client.interactionManager == null) return;

            tickCounter++;
            if (tickCounter >= placeDelay) {
                tickCounter = 0;
                performPlace(client);
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error in Persistent Place tick: ", e);
            setEnabled(false);
        }
    }

    private void performPlace(MinecraftClient client) {
        try {
            if (client.crosshairTarget == null) return;
            if (client.crosshairTarget.getType() != HitResult.Type.BLOCK) return;
            if (client.player.getMainHandStack().isEmpty()) return;

            BlockHitResult blockHit = (BlockHitResult) client.crosshairTarget;
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
            client.player.swingHand(Hand.MAIN_HAND);
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error performing place: ", e);
        }
    }

    public int  getPlaceDelay() { return placeDelay; }
    public void setPlaceDelay(int d) { optSpeed.setValue((double) Math.max(1, Math.min(10, d))); }
    public int getPlacementsPerSecond() { return Math.max(1, 20 / placeDelay); }
}
