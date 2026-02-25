package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.modules.m_player.SpiderModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class SpiderClientMixin {
    @Inject(method = "travel", at = @At("HEAD"))
    private void spiderClimb(Vec3d movementInput, CallbackInfo ci) {
        if (!(((Object) this) instanceof ClientPlayerEntity player)) return;
        if (!SpiderModule.isActive()) return;

        if (player.isOnGround()) return;
        if (player.isTouchingWater() || player.isInLava() || player.isSwimming()) return;

        if (!player.horizontalCollision) return;

        Vec3d vel = player.getVelocity();

        boolean movingForward = player.input != null
                && player.input.playerInput != null
                && (player.input.playerInput.forward()
                ||  player.input.playerInput.backward()
                ||  player.input.playerInput.left()
                ||  player.input.playerInput.right());

        boolean sneaking = player.input != null
                && player.input.playerInput != null
                && player.input.playerInput.sneak();

        double newY;
        if (movingForward) {
            newY = SpiderModule.getClimbSpeed();
        } else if (sneaking) {
            newY = -SpiderModule.getClimbSpeed() * 0.5;
        } else {
            newY = 0.0;
        }

        player.setVelocity(vel.x, newY, vel.z);
    }
}
