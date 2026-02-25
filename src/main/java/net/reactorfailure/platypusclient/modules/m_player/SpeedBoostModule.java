package net.reactorfailure.platypusclient.modules.m_player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_utils.SliderOption;

public class SpeedBoostModule extends AbstractModule {
    private static SpeedBoostModule INSTANCE;

    private static final Identifier MODIFIER_ID = Identifier.of("platypusclient", "speed_boost");

    private double speedMultiplier = 1.5;
    private boolean modifierApplied = false;

    private final SliderOption optSpeed = new SliderOption(
            "speedMultiplier", "Speed Multiplier",
            1.1, 5.0, 1.5,
            0.1,
            "Slight", "Very Fast",
            v -> {
                this.speedMultiplier = v;

                if (modifierApplied) reapplyModifier();
            }
    );

    public SpeedBoostModule() {
        super("mod_speedBoost", "Speed Boost", "Makes you move faster while sprinting", ModuleCategory.PLAYER);
        INSTANCE = this;
        addOption(optSpeed);
    }

    public static SpeedBoostModule get() { return INSTANCE; }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Speed enabled – {}x", speedMultiplier);
        modifierApplied = false;
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Speed disabled");
        removeModifier();
        modifierApplied = false;
    }

    @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!isEnabled() || client.player == null) return;

        boolean sprinting = client.player.isSprinting();

        if (sprinting && !modifierApplied) {
            applyModifier(client);
        } else if (!sprinting && modifierApplied) {
            removeModifier();
        }
    }

    private void applyModifier(MinecraftClient client) {
        var attr = client.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (attr == null) return;

        if (attr.getModifier(MODIFIER_ID) != null) attr.removeModifier(MODIFIER_ID);

        attr.addTemporaryModifier(new EntityAttributeModifier(
                MODIFIER_ID,
                speedMultiplier - 1.0,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
        modifierApplied = true;
        ClientSide.LOGGER.debug("Speed modifier applied: {}x", speedMultiplier);
    }

    private void reapplyModifier() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        applyModifier(client);
    }

    private void removeModifier() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        var attr = client.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (attr == null) return;

        if (attr.getModifier(MODIFIER_ID) != null) {
            attr.removeModifier(MODIFIER_ID);
        }
        modifierApplied = false;
    }
}
