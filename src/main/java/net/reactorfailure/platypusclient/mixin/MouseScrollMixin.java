package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.Mouse;
import net.reactorfailure.platypusclient.qol.TooltipScroll.TooltipScrollHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Mouse.class)
public class MouseScrollMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        TooltipScrollHandler.onScroll(vertical);
    }
}