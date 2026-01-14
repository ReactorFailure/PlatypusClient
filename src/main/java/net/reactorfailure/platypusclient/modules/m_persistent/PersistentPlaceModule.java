package net.reactorfailure.platypusclient.modules.m_persistent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.core.AbstractModule;
import net.reactorfailure.platypusclient.modules.core.ModuleCategory;

public class PersistentPlaceModule extends AbstractModule {
    private static PersistentPlaceModule INSTANCE;

    // Place delay in ticks (20 ticks = 1 second)
    // Default: 4 ticks = 5 placements per second
    private int placeDelay = 4;
    private int tickCounter = 0;

    public PersistentPlaceModule() {
        super("mod_pp", "Persistent Place", "Automatically places blocks continuously", ModuleCategory.PERSISTENT);
        INSTANCE = this;
    }

    public static PersistentPlaceModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Persistent Place enabled with {} placements per second", getPlacementsPerSecond());
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
            if (!isEnabled() || client.player == null) return;
            if (client.interactionManager == null) return;

            tickCounter++;

            // Only place when delay has passed
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

            BlockHitResult blockHit = (BlockHitResult) client.crosshairTarget;

            // Check if player is holding something placeable
            if (client.player.getMainHandStack().isEmpty()) {
                ClientSide.LOGGER.debug("No item in hand to place");
                return;
            }

            client.interactionManager.interactBlock(
                    client.player,
                    Hand.MAIN_HAND,
                    blockHit
            );

            client.player.swingHand(Hand.MAIN_HAND);

            ClientSide.LOGGER.debug("Placed block at {}", blockHit.getBlockPos());

        } catch (Exception e) {
            ClientSide.LOGGER.error("Error performing place: ", e);
        }
    }

    public int getPlaceDelay() {
        return placeDelay;
    }

    public void setPlaceDelay(int delay) {
        this.placeDelay = Math.max(1, Math.min(20, delay));
    }

    public int getPlacementsPerSecond() {
        return 20 / placeDelay;
    }

    @Override
    public Object saveToConfig() {
        return placeDelay;
    }

    @Override
    public void loadFromConfig(Object data) {
        if (data instanceof Number) {
            setPlaceDelay(((Number) data).intValue());
        }
    }
}
