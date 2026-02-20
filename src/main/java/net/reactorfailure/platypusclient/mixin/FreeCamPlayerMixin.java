package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.reactorfailure.platypusclient.modules.m_misc.FreeCamModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class FreeCamPlayerMixin {
    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPackets(CallbackInfo ci) {
        FreeCamModule freeCam = FreeCamModule.get();

        if (freeCam != null && freeCam.isEnabled()) {
            ci.cancel();
        }
    }
}
