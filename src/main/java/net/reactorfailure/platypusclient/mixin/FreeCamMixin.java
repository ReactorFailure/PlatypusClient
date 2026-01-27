package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.reactorfailure.platypusclient.modules.misc.FreeCamModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class FreeCamMixin {
    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("RETURN"))
    private void onUpdate(CallbackInfo ci) {
        FreeCamModule freeCam = FreeCamModule.get();

        if (freeCam != null && freeCam.isEnabled()) {
            this.setPos(freeCam.getCameraPos());
            this.setRotation(freeCam.getCameraYaw(), freeCam.getCameraPitch());
        }
    }
}
