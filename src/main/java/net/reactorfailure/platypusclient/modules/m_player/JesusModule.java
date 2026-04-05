package net.reactorfailure.platypusclient.modules.m_player;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.ClientSide;
import net.reactorfailure.platypusclient.modules.L_core.AbstractModule;
import net.reactorfailure.platypusclient.modules.L_core.ModuleCategory;

public class JesusModule extends AbstractModule {
//    private static JesusModule INSTANCE;
//
//    public JesusModule() {
//        super("mod_jesus", "Become Jesus", "Walk on water", ModuleCategory.PLAYER);
//        INSTANCE = this;
//    }
//
//    public static JesusModule get() {
//        return INSTANCE;
//    }
//
//    @Override
//    public void onEnable() {
//        ClientSide.LOGGER.info("Jesus mode enabled");
//    }
//
//    @Override
//    public void onDisable() {
//        ClientSide.LOGGER.info("Jesus mode disabled");
//    }
//
//    @Override
//    public void tick() {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (!isEnabled() || client.player == null || client.world == null) return;
//
//        if (client.player.isTouchingWater() && !client.player.isSubmergedInWater()) {
//
//            BlockPos below = client.player.getBlockPos().down();
//
//            if (client.world.getBlockState(below).isOf(Blocks.WATER)) {
//
//                Vec3d velocity = client.player.getVelocity();
//
//                client.player.setOnGround(true);
//
//                if (velocity.y < 0.0) {
//                    velocity = new Vec3d(velocity.x, 0.0, velocity.z);
//                }
//
//                if (client.player.input != null &&
//                        client.player.input.playerInput != null &&
//                        client.player.input.playerInput.forward() &&
//                        client.options.sprintKey.isPressed()) {
//
//                    client.player.setSprinting(true);
//                }
//
//                double speedMultiplier = 1.12;
//                velocity = new Vec3d(
//                        velocity.x * speedMultiplier,
//                        velocity.y,
//                        velocity.z * speedMultiplier
//                );
//
//                client.player.setVelocity(velocity);
//            }
//        }
//    }
    private static JesusModule INSTANCE;

    public JesusModule() {
        super("mod_jesus", "Become Jesus", "Walk on water", ModuleCategory.PLAYER);
        INSTANCE = this;
    }

    public static JesusModule get() { return INSTANCE; }
    public static boolean isActive() { return INSTANCE != null && INSTANCE.isEnabled(); }

    @Override
    public void onEnable() { ClientSide.LOGGER.info("Jesus mode enabled"); }

    @Override
    public void onDisable() { ClientSide.LOGGER.info("Jesus mode disabled"); }
}
