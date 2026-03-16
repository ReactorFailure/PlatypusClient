package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.reactorfailure.platypusclient.qol.TooltipScroll.TooltipScroll;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenTooltipMixin {
    @Shadow
    protected abstract Slot getSlotAt(double x, double y);

    @Unique
    private Slot lastHoveredSlot = null;

    @ModifyVariable(
            method = "drawMouseoverTooltip",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private int modifyTooltipY(int y) {
        int offset = TooltipScroll.getInstance().getYOffset();
        return y + offset;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Slot currentSlot = this.getSlotAt(mouseX, mouseY);
        if (currentSlot == null) {
            TooltipScroll.getInstance().reset();
            lastHoveredSlot = null;
        } else if (currentSlot != lastHoveredSlot) {
            TooltipScroll.getInstance().reset();
            lastHoveredSlot = currentSlot;
        }
    }
}