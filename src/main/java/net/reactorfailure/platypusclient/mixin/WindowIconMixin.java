package net.reactorfailure.platypusclient.mixin;

import net.minecraft.client.util.Icons;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Mixin(Window.class)
public class WindowIconMixin {

    @Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
    private void injectCustomIcon(ResourcePack resourcePack, Icons icons, CallbackInfo ci) {
        ci.cancel();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            java.lang.reflect.Field handleField = Window.class.getDeclaredField("handle");
            handleField.setAccessible(true);
            long handle = (long) handleField.get(this);

            GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1, stack);

            loadIconInto(imageBuffer.get(0), stack);

            GLFW.glfwSetWindowIcon(handle, imageBuffer);

        } catch (Exception e) {
            // Skip icon loading
        }
    }

    @Unique
    private void loadIconInto(GLFWImage image, MemoryStack stack) throws Exception {
        InputStream stream = getClass().getResourceAsStream("/assets/platypusclient/icon.png");
        if (stream == null) throw new Exception("Icon not found: " + "/assets/platypusclient/icon.png");

        byte[] bytes = stream.readAllBytes();
        ByteBuffer buffer = stack.malloc(bytes.length);
        buffer.put(bytes).flip();

        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer channels = stack.mallocInt(1);

        ByteBuffer pixels = STBImage.stbi_load_from_memory(buffer, w, h, channels, 4);
        if (pixels == null) throw new Exception("STBImage failed to decode: " + "/assets/platypusclient/icon.png");

        image.set(w.get(0), h.get(0), pixels);
    }
}
