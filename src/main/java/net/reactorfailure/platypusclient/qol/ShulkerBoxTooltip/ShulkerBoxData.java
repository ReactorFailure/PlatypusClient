package net.reactorfailure.platypusclient.qol.ShulkerBoxTooltip;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

import java.util.List;

public class ShulkerBoxData implements TooltipData {
    private final List<ItemStack> items;

    public ShulkerBoxData(List<ItemStack> items) {
        this.items = items;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
