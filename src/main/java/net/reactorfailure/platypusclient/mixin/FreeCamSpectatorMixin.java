package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.reactorfailure.platypusclient.modules.m_misc.FreeCamModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class FreeCamSpectatorMixin {
    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
        FreeCamModule freeCam = FreeCamModule.get();

        if (freeCam != null && freeCam.isEnabled()) {
            cir.setReturnValue(false);
        }
    }
}
