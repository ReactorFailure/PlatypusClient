package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.reactorfailure.platypusclient.qol.TooltipTextWrap.TooltipTextWrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DrawContext.class)
public class TooltipTextWrapMixin {
    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wrapTooltipLines(
            net.minecraft.client.font.TextRenderer textRenderer,
            List<Text> text,
            int x,
            int y,
            CallbackInfo ci
    ) {
        if (text == null || text.isEmpty()) return;
        if (textRenderer == null) return;

        List<Text> wrapped = TooltipTextWrap.wrapTooltipLines(new ArrayList<>(text), textRenderer);

        if (wrapped.size() == text.size()) return;

        DrawContext ctx = (DrawContext) (Object) this;
        ctx.drawTooltip(textRenderer, wrapped, x, y);
        ci.cancel();
    }
}
