package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.modules.m_player.JesusModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class JesusClientMixin {
    @Inject(method = "travel", at = @At("HEAD"))
    private void jesusWalk(Vec3d movementInput, CallbackInfo ci) {
        if (!(((Object) this) instanceof ClientPlayerEntity player)) return;
        if (!JesusModule.isActive()) return;
        if (player.isSneaking()) return;
        if (player.isSubmergedInWater()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;

        BlockPos feetPos = player.getBlockPos();
        boolean feetInWater = mc.world.getFluidState(feetPos).isOf(Fluids.WATER)
                || mc.world.getFluidState(feetPos).isOf(Fluids.FLOWING_WATER);

        if (!feetInWater) return;

        BlockPos below = feetPos.down();
        boolean solidBelow = mc.world.getBlockState(below).isSolidBlock(mc.world, below);

        if (solidBelow) return;

        Vec3d vel = player.getVelocity();

        if (vel.y < 0) {
            player.setVelocity(vel.x, 0.0, vel.z);
        }

        player.setOnGround(true);
        player.fallDistance = 0.0f;
    }
}
