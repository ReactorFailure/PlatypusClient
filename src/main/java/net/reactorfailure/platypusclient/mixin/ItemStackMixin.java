package net.reactorfailure.platypusclient.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.reactorfailure.platypusclient.qol.ShulkerBoxTooltip.ShulkerBoxToolTip;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipData>> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        Optional<TooltipData> shulkerData = ShulkerBoxToolTip.getShulkerBoxTooltipData(stack);
        if (shulkerData.isPresent()) {
            cir.setReturnValue(shulkerData);
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void onGetTooltip(
            Item.TooltipContext context,
            @Nullable PlayerEntity player,
            TooltipType type,
            CallbackInfoReturnable<List<Text>> cir
    ) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof ShulkerBoxBlock) {
                List<Text> tooltip = cir.getReturnValue();

                if (tooltip.size() > 1) {
                    // Remove everything after the item name
                    tooltip.subList(1, tooltip.size()).clear();
                }
            }
        }
    }
}
