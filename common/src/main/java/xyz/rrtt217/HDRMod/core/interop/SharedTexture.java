package xyz.rrtt217.HDRMod.core.interop;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.WindowsUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.WGLNVDXInterop.*;
import static org.lwjgl.system.windows.WinBase.GetLastError;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;

public class SharedTexture implements AutoCloseable{
    private int glTexture;

    private long interopDeviceHandle;
    private long interopSharedHandle;
    private long interopObjectHandle;
    private long interopTextureHandle;
    private boolean textureLocked = false;

    void lock(){
        if(textureLocked){
            LOGGER.warn("Texture already locked");
            return;
        }
        if (interopObjectHandle != 0 && interopDeviceHandle != 0) {
            try(var memStack = MemoryStack.stackPush()) {
                if(!wglDXLockObjectsNV(interopDeviceHandle, memStack.callocPointer(1).put(0, interopObjectHandle))) {
                    IntBuffer pi = memStack.ints(GetLastError());
                    WindowsUtil.windowsThrowException("Failed to lock shared texture", pi);
                }
            }
        }
        textureLocked = true;
    }

    void unlock(){
        if(!textureLocked){
            LOGGER.warn("Texture already unlocked");
            return;
        }
        if (interopObjectHandle != 0 && interopDeviceHandle != 0) {
            try(var memStack = MemoryStack.stackPush()) {
                if(!wglDXUnlockObjectsNV(interopDeviceHandle, memStack.callocPointer(1).put(0, interopObjectHandle))) {
                    IntBuffer pi = memStack.ints(GetLastError());
                    WindowsUtil.windowsThrowException("Failed to unlock shared texture", pi);
                }
            }
        }
        textureLocked = false;
    }

    void register(){
        if(interopObjectHandle != 0) {
            LOGGER.warn("Texture already registered");
            return;
        }
        if (interopDeviceHandle != 0 && glTexture != 0) {
            interopObjectHandle = wglDXRegisterObjectNV(interopDeviceHandle, interopTextureHandle, glTexture, GL_TEXTURE_2D, WGL_ACCESS_WRITE_DISCARD_NV);
            if(interopObjectHandle == 0) {
                try(var memStack = MemoryStack.stackPush()) {
                    IntBuffer pi = memStack.ints(GetLastError());
                    WindowsUtil.windowsThrowException("Failed to unregister the shared texture", pi);
                }
            }
        }
    }

    void unregister(){
        if(interopObjectHandle == 0) {
            LOGGER.warn("Texture already unregistered");
            return;
        }
        if (interopDeviceHandle != 0) {
            if(!wglDXUnregisterObjectNV(interopDeviceHandle, interopObjectHandle)) {
                try(var memStack = MemoryStack.stackPush()) {
                    IntBuffer pi = memStack.ints(GetLastError());
                    WindowsUtil.windowsThrowException("Failed to unregister the shared texture", pi);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {

    }
}
