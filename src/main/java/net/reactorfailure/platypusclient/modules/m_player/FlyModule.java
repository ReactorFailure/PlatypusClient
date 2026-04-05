package net.reactorfailure.platypusclient.modules.m_player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;
import net.reactorfailure.platypusclient.modules.L_utils.SliderOption;

public class FlyModule extends AbstractModule {
    private static FlyModule INSTANCE;

    private double flySpeed = 0.2;

    private boolean spaceReleased = false;
    private boolean wasSpacePressed = false;
    private boolean waitingForSecondTap = false;
    private boolean spaceReleasedAfterT1 = false;
    private int tapWindowTimer = 0;
    private static final int DOUBLE_TAP_WINDOW = 14;

    private final SliderOption optSpeed = new SliderOption(
            "flySpeed", "Fly Speed",
            0.05, 1.0, 0.2,
            0.05,
            "Slow", "Fast",
            v -> this.flySpeed = v
    );

    public FlyModule() {
        super("mod_fly", "Fly", "Fly like a bird", ModuleCategory.PLAYER);
        INSTANCE = this;
        addOption(optSpeed);
    }

    public static FlyModule get() { return INSTANCE; }
    public static boolean isActive() { return INSTANCE != null && INSTANCE.isEnabled(); }
    public static double getSpeed() { return INSTANCE != null ? INSTANCE.flySpeed : 0.2; }

    @Override
    public void onEnable() {
        ClientSide.LOGGER.info("Fly enabled – speed: {}", flySpeed);
        spaceReleased = false;
        wasSpacePressed = false;
        waitingForSecondTap = false;
        spaceReleasedAfterT1 = false;
        tapWindowTimer = 0;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.getAbilities().flying = false;
    }

    @Override
    public void onDisable() {
        ClientSide.LOGGER.info("Fly disabled");
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        mc.player.setVelocity(0, 0, 0);

        if (mc.player.getAbilities().allowFlying) {
            mc.player.getAbilities().flying = false;
        }
    }

    @Override
    public void tick() {
        if (!isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        boolean spaceNow = mc.player.input.playerInput.jump();
        boolean justPressed  = spaceNow && !wasSpacePressed;
        boolean justReleased = !spaceNow && wasSpacePressed;

        if (!spaceReleased) {
            if (justReleased) spaceReleased = true;
            wasSpacePressed = spaceNow;
            return;
        }

        if (waitingForSecondTap) {
            if (tapWindowTimer > 0) tapWindowTimer--;

            if (justReleased) spaceReleasedAfterT1 = true;

            if (justPressed && spaceReleasedAfterT1) {
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x, 0.0, vel.z);
                mc.player.getAbilities().flying = false;
                waitingForSecondTap = false;
                spaceReleasedAfterT1 = false;
                wasSpacePressed = spaceNow;
                return;
            }

            if (tapWindowTimer == 0) {
                waitingForSecondTap  = false;
                spaceReleasedAfterT1 = false;
            }
        } else {
            if (justPressed) {
                waitingForSecondTap  = true;
                spaceReleasedAfterT1 = false;
                tapWindowTimer       = DOUBLE_TAP_WINDOW;
            }
        }

        wasSpacePressed = spaceNow;
    }
}
