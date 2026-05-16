package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.system.MemoryUtil;

public class ExtendedNativeImage extends NativeImage {
    private final int bitPerChannel;
    public ExtendedNativeImage(int i, int j, boolean bl) {
        this(Format.RGBA, i, j, bl);
    }
    public ExtendedNativeImage(Format format, int i, int j, boolean bl) {
        this(8, format, i, j, bl);
    }
    public ExtendedNativeImage(int bits, int i, int j, boolean bl) {
        this(bits, Format.RGBA, i, j, bl);
    }
    public ExtendedNativeImage(int bits, Format format, int i, int j, boolean bl) {
        if (i > 0 && j > 0) {
            this.format = format;
            this.width = i;
            this.height = j;
            this.size = (long)i * (long)j * (long)format.components();
            this.useStbFree = false;
            if (bl) {
                this.pixels = MemoryUtil.nmemCalloc(1L, this.size);
            } else {
                this.pixels = MemoryUtil.nmemAlloc(this.size);
            }

            MEMORY_POOL.malloc(this.pixels, (int)this.size);
            if (this.pixels == 0L) {
                throw new IllegalStateException("Unable to allocate texture of size " + i + "x" + j + " (" + format.components() + " channels)");
            }
        } else {
            throw new IllegalArgumentException("Invalid texture size: " + i + "x" + j);
        }
        bitPerChannel = bits;
    }

}
