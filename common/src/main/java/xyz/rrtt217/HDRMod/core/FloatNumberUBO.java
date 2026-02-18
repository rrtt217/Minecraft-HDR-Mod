package xyz.rrtt217.HDRMod.core;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class FloatNumberUBO implements AutoCloseable {
    private final GpuBuffer buffer;
    private final long memSize;
    private float[] lastValues;
    public FloatNumberUBO(String string, int blockSize) {
        GpuDevice gpudevice = RenderSystem.getDevice();
        lastValues = new float[blockSize];
        Std140SizeCalculator calcualtor = new Std140SizeCalculator();
        for(int i = 0; i < blockSize; i++) {
            calcualtor = calcualtor.putFloat();
        }
        memSize = calcualtor.get();
        this.buffer = gpudevice.createBuffer(() -> "Float UBO " + string, 136, Math.toIntExact(memSize));
    }
    public FloatNumberUBO(String string) {
       this(string, 1);
    }
    public GpuBuffer update(float[] f) {
        if(f.length != lastValues.length) throw new IllegalArgumentException("Updated array size must equals to previous");
        if(!Arrays.equals(lastValues, f)) {
            // Only write when dirty.
            try (MemoryStack memorystack = MemoryStack.stackPush()) {
                Std140Builder builder = Std140Builder.onStack(memorystack, (int) memSize);

                for (Float f1 : f) {
                    builder = builder.putFloat(f1);
                }
                ByteBuffer bytebuffer = builder.get();
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), bytebuffer);
            }
            lastValues = f;
        }
        return buffer;
    }
    public void close() {
        this.buffer.close();
    }
}
