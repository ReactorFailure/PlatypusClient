package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.reactorfailure.platypusclient.qol.TooltipTextWrap.TooltipTextWrap;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class TooltipTextWrapMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void wrapTooltipText(
            Item.TooltipContext context,
            @Nullable PlayerEntity player,
            TooltipType type,
            CallbackInfoReturnable<List<Text>> cir
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer == null) return;

        List<Text> originalLines = cir.getReturnValue();
        if (originalLines == null || originalLines.isEmpty()) return;

        // Wrap the tooltip lines
        List<Text> wrappedLines = TooltipTextWrap.wrapTooltipLines(originalLines, client.textRenderer);

        // Replace the original lines with wrapped lines
        originalLines.clear();
        originalLines.addAll(wrappedLines);
    }
}
