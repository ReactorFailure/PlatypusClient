package net.reactorfailure.platypusclient.qol.ShulkerBoxTooltip;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ShulkerBoxComponent implements TooltipComponent {
    private final List<ItemStack> items;
    private static final int ITEMS_PER_ROW = 9;
    private static final int ITEM_SIZE = 18;
    private static final int PADDING = 2;

    public ShulkerBoxComponent(List<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        int rows = (int) Math.ceil((double) items.size() / ITEMS_PER_ROW);
        return rows * ITEM_SIZE + (rows - 1) * PADDING + 4;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int itemCount = Math.min(items.size(), ITEMS_PER_ROW);
        return itemCount * ITEM_SIZE + (itemCount - 1) * PADDING + 4;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        int currentX = x + 2;
        int currentY = y + 2;
        int itemsInRow = 0;

        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                drawSlotBackground(context, currentX, currentY);

                context.drawItem(item, currentX + 1, currentY + 1);

                context.drawStackOverlay(textRenderer, item, currentX + 1, currentY + 1);

                currentX += ITEM_SIZE + PADDING;
                itemsInRow++;

                if (itemsInRow >= ITEMS_PER_ROW) {
                    currentX = x + 2;
                    currentY += ITEM_SIZE + PADDING;
                    itemsInRow = 0;
                }
            }
        }
    }

    private void drawSlotBackground(DrawContext context, int x, int y) {
        context.fill(x, y, x + ITEM_SIZE, y + ITEM_SIZE, 0xFF8B8B8B); // Light gray border
        context.fill(x + 1, y + 1, x + ITEM_SIZE - 1, y + ITEM_SIZE - 1, 0xFF373737); // Dark gray slot
    }
}
