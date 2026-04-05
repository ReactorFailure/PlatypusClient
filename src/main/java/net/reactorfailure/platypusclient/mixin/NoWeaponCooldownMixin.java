package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.reactorfailure.platypusclient.modules.m_combat.NoWeaponCooldownModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class NoWeaponCooldownMixin {
    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void noCooldown(float baseTime, CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        if (!NoWeaponCooldownModule.isActive()) return;
        cir.setReturnValue(1.0f);
    }
}
