package net.reactorfailure.platypusclient.dashboard;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

public class DashboardButtonWidget extends ButtonWidget {
    private static final Identifier ICON_TEXTURE =
            Identifier.of("platypusclient", "icon.png");

    public DashboardButtonWidget(int x, int y, int width, int height, PressAction onPress) {
        super(x, y, width, height, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float delta) {
        int backgroundColor = this.isHovered() ? 0x80FFFFFF : 0x80000000;
        context.fill(
                this.getX(),
                this.getY(),
                this.getX() + this.width,
                this.getY() + this.height,
                backgroundColor
        );

        // Border
        int borderColor = this.isHovered() ? 0xFFFFFFFF : 0xFF888888;
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, borderColor);
        context.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, borderColor);
        context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, borderColor);
        context.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, borderColor);

        // Icon
        int padding = 2;
        int iconSize = this.width - padding * 2;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                ICON_TEXTURE,
                this.getX() + padding,
                this.getY() + padding,
                0.0f,
                0.0f,
                iconSize,
                iconSize,
                iconSize,
                iconSize
        );
    }
}
