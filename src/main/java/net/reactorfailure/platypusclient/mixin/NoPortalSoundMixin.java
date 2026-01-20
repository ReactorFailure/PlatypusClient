package net.reactorfailure.platypusclient.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.reactorfailure.platypusclient.modules.misc.TogglePortalSoundsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class NoPortalSoundMixin {
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void onRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        TogglePortalSoundsModule module = TogglePortalSoundsModule.get();
        if (module != null && module.isEnabled()) {
            ci.cancel();
        }
    }
}
