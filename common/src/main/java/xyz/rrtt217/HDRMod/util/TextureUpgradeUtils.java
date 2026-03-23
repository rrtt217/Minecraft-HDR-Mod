package xyz.rrtt217.HDRMod.util;

import org.lwjgl.opengl.GL30;

public class TextureUpgradeUtils {
    public static final int srcTextureFormat = GL30.GL_RGBA8;
    private static final ThreadLocal<Integer> targetTextureFormat = ThreadLocal.withInitial(()-> -1);
    private static final ThreadLocal<Integer> targetReadPixelFormat = ThreadLocal.withInitial(()-> -1);
    public static void setTargetTextureFormat(int targetTextureFormat){
        TextureUpgradeUtils.targetTextureFormat.set(targetTextureFormat);
    }
    public static void setTargetReadPixelFormat(int targetReadPixelFormat){
        TextureUpgradeUtils.targetReadPixelFormat.set(targetReadPixelFormat);
    }
    public static void resetTargetTextureFormat(){
        TextureUpgradeUtils.targetTextureFormat.set(-1);
    }
    public static void resetTargetReadPixelFormat(){
        TextureUpgradeUtils.targetReadPixelFormat.set(-1);
    }
    public static int getTargetTextureFormat(){
        return targetTextureFormat.get();
    }
    public static int getTargetReadPixelFormat(){
        return targetReadPixelFormat.get();
    }
}
