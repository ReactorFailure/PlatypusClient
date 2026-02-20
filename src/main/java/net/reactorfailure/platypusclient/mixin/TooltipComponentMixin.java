package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import net.reactorfailure.platypusclient.qol.ShulkerBoxTooltip.ShulkerBoxComponent;
import net.reactorfailure.platypusclient.qol.ShulkerBoxTooltip.ShulkerBoxData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void onOf(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof ShulkerBoxData shulkerData) {
            cir.setReturnValue(new ShulkerBoxComponent(shulkerData.getItems()));
        }
    }
}
