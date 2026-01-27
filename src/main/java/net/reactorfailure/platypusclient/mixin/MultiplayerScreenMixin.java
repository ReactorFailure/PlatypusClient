package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import net.reactorfailure.platypusclient.dashboard.DashboardButtonWidget;
import net.reactorfailure.platypusclient.dashboard.DashboardUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addDashboardButton(CallbackInfo ci) {
        // Add a small square button with icon in the top-right corner
        int buttonSize = 20;
        int padding = 4;

        this.addDrawableChild(new DashboardButtonWidget(
                this.width - buttonSize - padding,
                padding,
                buttonSize,
                buttonSize,
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new DashboardUI((MultiplayerScreen)(Object)this));
                    }
                }
        ));
    }
}
