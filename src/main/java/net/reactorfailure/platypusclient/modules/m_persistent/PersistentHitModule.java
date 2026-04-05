package net.reactorfailure.platypusclient.modules.m_persistent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_utils.BooleanOption;
import net.reactorfailure.platypusclient.modules.L_utils.SliderOption;

public class PersistentHitModule extends AbstractModule {
    private static PersistentHitModule INSTANCE;

    private int clickDelay  = 4;
    private int tickCounter = 0;

    private final SliderOption optSpeed = new SliderOption(
            "clickDelay", "Click Speed",
            1, 10, 4,
            1,
            "Very Fast", "Normal",
            v -> this.clickDelay = v.intValue()
    );

    public PersistentHitModule() {
        super("mod_persistHit", "Persistent Hit", "Basically an autoclicker", ModuleCategory.PERSISTENT);
        INSTANCE = this;
        addOption(optSpeed);
    }

    public static PersistentHitModule get() { return INSTANCE; }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Persistent Hit enabled – {} CPS", getCPS());
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

            if (client.player.getAttackCooldownProgress(0f) < 1.0f) {
                tickCounter = 0;
                return;
            }

            tickCounter++;
            if (tickCounter >= clickDelay) {
                tickCounter = 0;
                performClick(client);
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error in Persistent Hit tick: ", e);
            setEnabled(false);
        }
    }

    private void performClick(MinecraftClient client) {
        try {
            if (client.player == null || client.interactionManager == null) return;

            client.player.swingHand(Hand.MAIN_HAND);

            if (client.targetedEntity != null) {
                client.interactionManager.attackEntity(client.player, client.targetedEntity);
                ClientSide.LOGGER.debug("Attacked entity: {}", client.targetedEntity.getName().getString());
            } else if (client.crosshairTarget != null) {
                if (client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                    client.interactionManager.attackBlock(
                            ((net.minecraft.util.hit.BlockHitResult) client.crosshairTarget).getBlockPos(),
                            ((net.minecraft.util.hit.BlockHitResult) client.crosshairTarget).getSide()
                    );
                }
            }
        } catch (Exception e) {
            ClientSide.LOGGER.error("Error performing click: ", e);
        }
    }

    public int  getClickDelay() { return clickDelay; }
    public void setClickDelay(int d) { optSpeed.setValue((double) Math.clamp(d, 1, 10)); }
    public int  getCPS() { return Math.max(1, 20 / clickDelay); }
}
