package xyz.rrtt217.HDRMod.neoforge.mixin;

import net.neoforged.fml.earlydisplay.render.EarlyFramebuffer;
import net.neoforged.fml.earlydisplay.render.LoadingScreenRenderer;
import net.neoforged.fml.earlydisplay.render.MaterializedTheme;
import net.neoforged.fml.earlydisplay.render.SimpleBufferBuilder;
import net.neoforged.fml.earlydisplay.render.elements.RenderElement;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LoadingScreenRenderer.class)
public class NeoForgeMixinLoadingScreenRenderer {
    @Shadow
    private final long glfwWindow;
    @Shadow
    private final MaterializedTheme theme;
    @Shadow
    private final EarlyFramebuffer framebuffer;
    @Shadow
    private final SimpleBufferBuilder buffer;
    @Shadow
    private final List<RenderElement> elements;


    public NeoForgeMixinLoadingScreenRenderer(long glfwWindow, MaterializedTheme theme, EarlyFramebuffer framebuffer, SimpleBufferBuilder buffer, List<RenderElement> elements) {
        this.glfwWindow = glfwWindow;
        this.theme = theme;
        this.framebuffer = framebuffer;
        this.buffer = buffer;
        this.elements = elements;
    }

    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetCurrentContext()J"))
    private long test() {
        return glfwWindow;
    }
    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL;getCapabilities()Lorg/lwjgl/opengl/GLCapabilities;"))
    private @Nullable GLCapabilities getCapabilities() {
        return null;
    }
}
