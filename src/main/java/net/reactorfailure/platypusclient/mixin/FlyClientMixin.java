package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.modules.m_player.FlyModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class FlyClientMixin {
    @Inject(method = "travel", at = @At("TAIL"))
    private void fly(Vec3d movementInput, CallbackInfo ci) {
        if (!(((Object) this) instanceof ClientPlayerEntity player)) return;
        if (!FlyModule.isActive()) return;

        double speed = FlyModule.getSpeed();

        boolean forward  = player.input.playerInput.forward();
        boolean backward = player.input.playerInput.backward();
        boolean left = player.input.playerInput.left();
        boolean right = player.input.playerInput.right();
        boolean up = player.input.playerInput.jump();
        boolean down = player.input.playerInput.sneak();

        double yawRad = Math.toRadians(player.getYaw());

        double fwdX    = -Math.sin(yawRad);
        double fwdZ    =  Math.cos(yawRad);
        double strafeX = -Math.cos(yawRad);
        double strafeZ = -Math.sin(yawRad);

        double vx = 0, vy = 0, vz = 0;

        if (forward)  { vx += fwdX;    vz += fwdZ;   }
        if (backward) { vx -= fwdX;    vz -= fwdZ;   }
        if (right)    { vx += strafeX; vz += strafeZ; }
        if (left)     { vx -= strafeX; vz -= strafeZ; }
        if (up)         vy =  1.0;
        if (down)       vy = -1.0;

        double horizLen = Math.sqrt(vx * vx + vz * vz);
        if (horizLen > 0) {
            vx = (vx / horizLen) * speed;
            vz = (vz / horizLen) * speed;
        }
        if (vy != 0) vy = Math.signum(vy) * speed;

        player.setVelocity(vx, vy, vz);

        player.fallDistance = 0.0F;
    }
}
