package xyz.rrtt217.HDRMod.neoforge.mixin;

import net.neoforged.fml.earlydisplay.render.EarlyFramebuffer;
import net.neoforged.fml.earlydisplay.render.LoadingScreenRenderer;
import net.neoforged.fml.earlydisplay.render.MaterializedTheme;
import net.neoforged.fml.earlydisplay.render.SimpleBufferBuilder;
import net.neoforged.fml.earlydisplay.render.elements.RenderElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
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

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void close(){
        theme.close();
        for (var element : elements) {
            element.close();
        }
        framebuffer.close();
        buffer.close();
        SimpleBufferBuilder.destroy();
    }
}
