package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class ColorTransformUBO implements AutoCloseable {
    private final GpuBuffer buffer;
    private final int memSize = new Std140SizeCalculator().putFloat().putFloat().putInt().putInt().get();
    public float lastUIBrightness = -1.0f;
    public float lastEotfEmulate =  -1.0f;
    public int lastPrimaries = -1;
    public int lastTransferFunction = -1;
    public ColorTransformUBO(String string) {
        GpuDevice gpudevice = RenderSystem.getDevice();
        this.buffer = gpudevice.createBuffer(() -> "Color Transform UBO" + string, 136, memSize);
    }
    public GpuBuffer update(float UIBrightness, float EotfEmulate, int Primaries, int TransferFunction) {
        if(UIBrightness != lastUIBrightness || EotfEmulate != lastEotfEmulate || Primaries != lastPrimaries || TransferFunction != lastTransferFunction) {
            try (MemoryStack memorystack = MemoryStack.stackPush()) {
                Std140Builder builder = Std140Builder.onStack(memorystack, (int) memSize);
                builder = builder.putFloat(UIBrightness).putFloat(EotfEmulate).putInt(Primaries).putInt(TransferFunction);
                ByteBuffer bytebuffer = builder.get();
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), bytebuffer);
            }
            lastUIBrightness = UIBrightness;
            lastEotfEmulate = EotfEmulate;
            lastPrimaries = Primaries;
            lastTransferFunction = TransferFunction;
        }
        return buffer;
    }
    public void close() {
        this.buffer.close();
    }
}
