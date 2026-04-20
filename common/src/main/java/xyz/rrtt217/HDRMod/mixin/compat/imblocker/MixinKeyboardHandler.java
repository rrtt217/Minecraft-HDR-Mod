package xyz.rrtt217.HDRMod.mixin.compat.imblocker;

import com.mojang.blaze3d.platform.Window;
import io.github.reserveword.imblocker.common.gui.UniversalIMEPreeditOverlay;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.rrtt217.HDRMod.config.HDRModConfig;
import xyz.rrtt217.HDRMod.util.ime.GLFWIMEUtils;

import java.nio.IntBuffer;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    // Fix IME on Rebased LWJGL 3.4.1.
    // Still need IMBlocker to properly show preedit.
    @Inject(method = "setup", at = @At(value = "TAIL"))
    private void hdr_mod$setPreeditCallbackForIMBlocker(long l, CallbackInfo ci){
        HDRModConfig config = AutoConfig.getConfigHolder(HDRModConfig.class).getConfig();
        if(config.enableIMBlockerSetPreeditCallbackIntegration) GLFWIMEUtils.glfwSetPreeditCallback(Minecraft.getInstance().getWindow().getWindow(), MixinKeyboardHandler::hdr_mod$preeditCallback);
    }
    // Work like vanilla 26.1.
    @Unique
    private static void hdr_mod$preeditCallback(long window, int preeditSize, long preeditPtr, int blockCount, long blockSizesPtr, int focused_block, int caret){
        if(window == Minecraft.getInstance().getWindow().getWindow()){
            if (preeditSize == 0) {
                UniversalIMEPreeditOverlay.getInstance().preeditContentUpdated(null, 0);
            } else {
                int[] codepoints = hdr_mod$readIntBuffer(preeditSize, preeditPtr);
                int[] blockSizes = hdr_mod$readIntBuffer(blockCount, blockSizesPtr);
                StringBuilder fullText = new StringBuilder();
                Builder<String> blocks = ImmutableList.builder();
                int offset = 0;
                int convertedCaret = 0;

                for (int blockSize : blockSizes) {
                    StringBuilder blockBuilder = new StringBuilder();

                    for (int i = 0; i < blockSize; i++) {
                        int codepoint = codepoints[offset];
                        if (offset == caret) {
                            convertedCaret = fullText.length() + blockBuilder.length();
                        }

                        blockBuilder.appendCodePoint(codepoint);
                        offset++;
                    }

                    String block = blockBuilder.toString();
                    blocks.add(block);
                    fullText.append(block);
                }

                if (offset == caret) {
                    convertedCaret = fullText.length();
                }
                UniversalIMEPreeditOverlay.getInstance().preeditContentUpdated(fullText.toString(), convertedCaret);
            }
        }
    }
    @Unique
    private static int[] hdr_mod$readIntBuffer(final int size, final long ptr) {
        IntBuffer buffer = MemoryUtil.memIntBuffer(ptr, size);
        int[] result = new int[size];
        buffer.get(result);
        return result;
    }
}
