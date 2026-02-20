package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

public class MainTargetBlitShader {
    public static ShaderInstance blitShader;
    public static void preloadMaintargetBlitShader(ResourceProvider resourceProvider) {
        try{
            blitShader = new ShaderInstance(resourceProvider, "main_target_blit_screen", DefaultVertexFormat.BLIT_SCREEN);
        }
        catch (Exception ignored){
        }
    }
}
