package net.reactorfailure.platypusclient.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.modules.m_player.NoSlowModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class NoSlowMixin {
    @Mixin(Entity.class)
    public static abstract class EntityMixin {

        @Inject(method = "slowMovement", at = @At("HEAD"), cancellable = true)
        private void noSlow_cancelSlowMovement(BlockState state, Vec3d multiplier, CallbackInfo ci) {
            net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
            if (mc.player == null) return;
            if (!((Object) this == mc.player)) return;

            if (NoSlowModule.isActive()) {
                ci.cancel();
            }
        }
    }

    @Mixin(LivingEntity.class)
    public static abstract class LivingEntityMixin {

        @Inject(method = "getVelocityMultiplier", at = @At("RETURN"), cancellable = true)
        private void noSlow_fixVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
            net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
            if (mc.player == null) return;
            if (!((Object) this == mc.player)) return;

            if (NoSlowModule.isActive()) {
                cir.setReturnValue(1.0F);
            }
        }
    }
}
