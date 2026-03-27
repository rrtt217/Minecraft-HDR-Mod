package xyz.rrtt217.HDRMod.util;

public class HDRModInjectHooks {
    private static final ThreadLocal<Boolean> vanillaF2Screenshot = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> targetDisableBlend = ThreadLocal.withInitial(() -> false);

    public static void setVanillaF2Screenshot() { vanillaF2Screenshot.set(true); }

    public static void setTargetDisableBlend() { targetDisableBlend.set(true); }

    public static void unsetVanillaF2Screenshot() { vanillaF2Screenshot.set(false); }

    public static void unsetTargetDisableBlend() { targetDisableBlend.set(false); }

    public static boolean getVanillaF2Screenshot() {return vanillaF2Screenshot.get();}

    public static boolean getTargetDisableBlend() {return targetDisableBlend.get();}
}
