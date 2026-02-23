package net.reactorfailure.platypusclient.modules.m_persistent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class PersistentHitModule extends AbstractModule {
    private static PersistentHitModule INSTANCE;

    // Click delay in ticks (20 ticks = 1 second)
    // Default: 4 ticks = 5 clicks per second (CPS)
    private int clickDelay = 4;
    private int tickCounter = 0;

    public PersistentHitModule() {
        super("mod_persistHit", "Persistent Hit", "Basically an autoclicker", ModuleCategory.PERSISTENT);
        INSTANCE = this;
    }

    public static PersistentHitModule get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Persistent Hit enabled with {} CPS", getCPS());
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Persistent Hit disabled");
        tickCounter = 0;
    }

    @Override
    public void tick() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!isEnabled() || client.player == null) return;

            tickCounter++;

            // Only click when delay has passed
            if (tickCounter >= clickDelay) {
                tickCounter = 0;
                performClick(client);
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error in Persistent Hit tick: ", e);
            setEnabled(false); // Disable module on error to prevent crash loop
        }
    }

    private void performClick(MinecraftClient client) {
        try {
            if (client.player != null && client.interactionManager != null) {

                client.player.swingHand(Hand.MAIN_HAND);

                if (client.targetedEntity != null) {
                    client.interactionManager.attackEntity(client.player, client.targetedEntity);
                    ClientSide.LOGGER.debug("Attacked entity: {}", client.targetedEntity.getName().getString());
                }
                else if (client.crosshairTarget != null) {
                    switch (client.crosshairTarget.getType()) {
                        case BLOCK:

                            client.interactionManager.attackBlock(
                                    ((net.minecraft.util.hit.BlockHitResult) client.crosshairTarget).getBlockPos(),
                                    ((net.minecraft.util.hit.BlockHitResult) client.crosshairTarget).getSide()
                            );
                            ClientSide.LOGGER.debug("Attacking block");
                            break;
                        default:
                            ClientSide.LOGGER.debug("Swinging in air");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error performing click: ", e);
        }
    }

    public int getClickDelay() {
        return clickDelay;
    }

    public void setClickDelay(int delay) {
        this.clickDelay = Math.max(1, Math.min(20, delay));
    }

    public int getCPS() {
        return 20 / clickDelay;
    }

    @Override
    public Object saveToConfig() {
        return clickDelay;
    }

    @Override
    public void loadFromConfig(Object data) {
        if (data instanceof Number) {
            setClickDelay(((Number) data).intValue());
        }
    }
}
