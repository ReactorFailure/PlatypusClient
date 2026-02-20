package net.reactorfailure.platypusclient.qol.ShulkerBoxTooltip;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShulkerBoxToolTip {
    public static Optional<TooltipData> getShulkerBoxTooltipData(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return Optional.empty();
        }

        if (!(blockItem.getBlock() instanceof ShulkerBoxBlock)) {
            return Optional.empty();
        }

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);

        if (container == null || !container.iterateNonEmpty().iterator().hasNext()) {
            return Optional.empty();
        }

        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : container.iterateNonEmpty()) {
            if (!item.isEmpty()) {
                items.add(item);
            }
        }

        if (items.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ShulkerBoxData(items));
    }
}
